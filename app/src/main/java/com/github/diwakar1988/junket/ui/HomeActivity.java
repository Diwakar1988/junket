package com.github.diwakar1988.junket.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.db.DataController;
import com.github.diwakar1988.junket.db.Settings;
import com.github.diwakar1988.junket.net.service.ServiceController;
import com.github.diwakar1988.junket.net.service.event.Event;
import com.github.diwakar1988.junket.net.service.event.EventManager;
import com.github.diwakar1988.junket.net.service.response.VenueServiceResponse;
import com.github.diwakar1988.junket.pojo.Venue;
import com.github.diwakar1988.junket.ui.adaptor.ItemClickListener;
import com.github.diwakar1988.junket.ui.adaptor.OnLoadMoreListener;
import com.github.diwakar1988.junket.ui.adaptor.VenuesAdapter;
import com.github.diwakar1988.junket.util.PhotoUtil;
import com.github.diwakar1988.junket.util.UiUtils;
import com.github.diwakar1988.junket.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppBaseActivity implements ItemClickListener,OnLoadMoreListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int RC_PERMISSIONS = 0X1F;
    private static final int RC_LOCATION_SETTINGS = 0X2F;
    private static final int RC_SETTINGS = 0X3F;

    private static final int LOCATION_UPDATE_INTERVAL=5*1000;// ms
    private static final int LOCATION_UPDATE_FASTEST_INTERVAL=2*1000;// ms

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private View mRoot;
    private View mProgressView;
    private RecyclerView mRecyclerView;
    private VenuesAdapter mAdapter;
    private Snackbar mSnackbar;
    private Dialog mDialog;

    private boolean mClearAndReload;
    private boolean mFirstLaunch=true;
    private int mFromLimit =0;
    private Settings mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mProgressView =  findViewById(R.id.ll_progress);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_venues);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new VenuesAdapter(this, new ArrayList<VenueServiceResponse.VenueItem>(),this,this);
        mRecyclerView.setAdapter(mAdapter);

        mRoot = findViewById(R.id.activity_home);

        mSettings = DataController.getInstance().getSettings();

        initGoogleApiClient();
        enableLocationSettingsIfRequired();

        if (!mSettings.useCurrentLocation()){
                mFirstLaunch=false;
                loadVenues(true);
        }

        EventManager.getInstance().register(this);
    }



    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_activity_home, menu);

        final MenuItem searchMenu = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) searchMenu.getActionView();
        SpannableString hint = new SpannableString(getString(R.string.i_am_looking_for));
        hint.setSpan(new ForegroundColorSpan(Color.WHITE),0,hint.length(),0);
        searchView.setQueryHint(hint);
//        searchView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchMenu.collapseActionView();
                if (TextUtils.isEmpty(query) || query.length()<3){
                    UiUtils.showSnackBar(getString(R.string.invalid_query),mRoot,Snackbar.LENGTH_LONG);
                    return false;
                }

                if (!Utils.isNetworkAvailable(HomeActivity.this)){
                    mSnackbar = UiUtils.showSnackBar(getString(R.string.no_internet_connection), mRoot,Snackbar.LENGTH_SHORT);
                    return false;
                }
                mSettings.setLastQuery(query);
                DataController.getInstance().saveSettings();
                clearAndReload();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorites:
                startActivity(FavoritesActivity.createIntent(this));
                break;
            case R.id.action_settings:
                startActivityForResult(SettingsActivity.createIntent(this),RC_SETTINGS);
                break;
            case R.id.action_clear_reload:
                clearAndReload();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    private void loadVenues(boolean showProgress) {


        if (showProgress || mAdapter.getVenueItemCount()==0){

            mProgressView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        com.github.diwakar1988.junket.pojo.Location loc = mSettings.useCurrentLocation() ? mSettings.getCurrentLocation() : mSettings.getCustomLocation();

        Map<String, String> options=new HashMap<>();
        options.put("ll", loc.lat+","+loc.lng);
        options.put("query", mSettings.getLastQuery());
        options.put("limit",String.valueOf(mSettings.getRecordsPerPage()));
        options.put("offset",String.valueOf(mFromLimit));
        options.put("radius",String.valueOf(mSettings.getRadius()*1000));


        ServiceController.getInstance().loadVenues(options,new Callback<VenueServiceResponse>() {
            @Override
            public void onResponse(Call<VenueServiceResponse> call, Response<VenueServiceResponse> response) {
                hideProgress();
                mProgressView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                if (mClearAndReload){
                    mClearAndReload =false;
                }

                if (response.isSuccessful() && response.body().response.getVenueItems().size()>0){

                    ArrayList<VenueServiceResponse.VenueItem> r = response.body().response.getVenueItems();
                    mAdapter.addVenueItems(r);
                    mFromLimit = mAdapter.getVenueItemCount();

                }else{

                    //hide progress at bottom (if it was loading more)
                    hideListLoadingProgress();
                    UiUtils.showSnackBar(getString(R.string.no_results_from_server),mRoot, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<VenueServiceResponse> call, Throwable t) {
                hideProgress();
                mProgressView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                if (mClearAndReload){
                    mClearAndReload =false;
                }

                //hide progress at bottom (if it was loading more)
                hideListLoadingProgress();
                mSnackbar = UiUtils.showSnackBar((Utils.isNetworkAvailable(HomeActivity.this)?getString(R.string.oops_went_wrong):getString(R.string.no_internet_connection)), getString(R.string.retry), mRoot, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Utils.isNetworkAvailable(HomeActivity.this)){
                            return;
                        }
                        mSnackbar.dismiss();
                        loadVenues(false);
                    }
                });


            }
        });
    }
    private void hideListLoadingProgress() {
        //hide progress at bottom (if it was loading more)
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int position=layoutManager.findLastVisibleItemPosition();
            if (position>=0){
                View view = layoutManager.findViewByPosition(position);
                view.setVisibility(View.GONE);
            }

        }
    }
    @Override
    public void onItemClicked(View view, int position) {

        final Venue venue= mAdapter.getVenueItem(position).venue;

        if (view.getId()==R.id.tv_venue_contact){
            if (venue.contact!=null && !TextUtils.isEmpty(venue.contact.getNumber())){
                UiUtils.animateView(view, new Runnable() {
                    @Override
                    public void run() {
                        Utils.call(HomeActivity.this,venue.contact.getNumber());
                    }
                });
            }
        }else if (view.getId()==R.id.layout_venue_list_item){
            startActivity(VenueDetailsActivity.createIntent(this,venue,position));
        }
        else if (view.getId()==R.id.iv_venue_thumb){
            String url= PhotoUtil.createSmallPhotoURL(venue.getFirstPhoto());
           mDialog = UiUtils.showImageOnDialog(this, url, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show full screen image
                    startActivity(ZoomImageActivity.createIntent(HomeActivity.this,PhotoUtil.createFullPhotoURL(venue.getFirstPhoto())));
                    mDialog.dismiss();
                }
            });
        }
    }


    @Override
    public void onLoadMore() {

        if (mSnackbar!=null){
            mSnackbar.dismiss();
        }
        loadVenues(false);
    }


    /*****Location settings code**/

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect(); // connect the google api client when onstart is called
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect(); // disconnect the google api client when onstop is called
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationMonitoring();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        //put a network check
        mSnackbar = UiUtils.showSnackBar((Utils.isNetworkAvailable(HomeActivity.this)?getString(R.string.common_google_play_services_unknown_issue):getString(R.string.no_internet_connection)), getString(R.string.retry), mRoot, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.dismiss();
                mGoogleApiClient.connect();
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {
            mSettings.setCurrentLocation(location.getLatitude(), location.getLongitude());
            DataController.getInstance().saveSettings();

             if (mFirstLaunch){
                mFirstLaunch=false;
                clearAndReload();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS) {
            boolean allAllowed = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allAllowed = false;
                    break;
                }
            }
            if (allAllowed) {
                //all permissions granted now start app
                startLocationMonitoring();
            } else {
                Toast.makeText(this, "Please grant LOCATION permissions and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==47){
            //locations setting screen request code
            if (resultCode== RESULT_CANCELED && !Utils.isLocationEnabled(this)){
                finish();
            }
        }
        else if (requestCode==RC_SETTINGS && resultCode==RESULT_OK){
            clearAndReload();
        }
    }

    private void clearAndReload() {
        mClearAndReload =true;
        mFromLimit=0;
        mAdapter.clear();
        loadVenues(true);
    }


    private void startLocationMonitoring() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(HomeActivity.this,
                    permissions,
                    RC_PERMISSIONS);

        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }
    private void initGoogleApiClient() {
        //create client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //create a LocationRequest
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setInterval(LOCATION_UPDATE_FASTEST_INTERVAL);

    }

    private void enableLocationSettingsIfRequired() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {

                final Status status = result.getStatus();
                final LocationSettingsStates states= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    RC_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Toast.makeText(HomeActivity.this, getString(R.string.location_settings_change_unavailable), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case LocationSettingsStatusCodes.CANCELED:
                    case LocationSettingsStatusCodes.ERROR:finish();break;
                }
            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().unregister(this);
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == Event.TYPE_NETWORK_AVAILABLE){
            if (mAdapter.isEmpty()){
                if (mSnackbar!=null ){
                    mSnackbar.dismiss();
                }
                clearAndReload();
            }
        }
    }

}
