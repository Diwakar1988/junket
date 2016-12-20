package com.github.diwakar1988.junket.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.db.DataController;
import com.github.diwakar1988.junket.db.Settings;
import com.github.diwakar1988.junket.pojo.Location;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class SettingsActivity extends AppBaseActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{

    private static final int RC_PERMISSIONS = 0X1F;
    private static final int PLACE_PICKER_REQUEST = 0X2F;

    public static Intent createIntent(Context context){
        Intent i = new Intent(context,SettingsActivity.class);
        return i ;
    }

    private View mViewCustomLocation;
    private CheckBox mCBCurrentLocation;
    private TextView mTVRadius;
    private TextView mTVRecords;
    private TextView mTVPhotos;
    private TextView mTVCustomLocation;
    private SeekBar mSBRadius;
    private SeekBar mSBRecords;
    private SeekBar mSBPhotos;

    private Settings mSettings;
    private Settings mSettingsClone;

    private PlacePicker.IntentBuilder mBuilder = new PlacePicker.IntentBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpTitleBar(getString(R.string.settings),true);

        mCBCurrentLocation = (CheckBox) findViewById(R.id.checkbox);
        mViewCustomLocation = findViewById(R.id.ll_custom_location);
        mTVCustomLocation = (TextView) mViewCustomLocation.findViewById(R.id.tv_custom_location);
        mTVRadius = (TextView) findViewById(R.id.tv_radius);
        mTVRecords = (TextView) findViewById(R.id.tv_records_per_page);
        mTVPhotos = (TextView) findViewById(R.id.tv_photos_per_page);
        mSBRadius = (SeekBar) findViewById(R.id.sb_radius);
        mSBRecords = (SeekBar) findViewById(R.id.sb_records_per_page);
        mSBPhotos = (SeekBar) findViewById(R.id.sb_photos_per_page);

        mSBPhotos.setMax(Settings.MAX_PHOTOS);
        mSBRadius.setMax(Settings.MAX_RADIUS);
        mSBRecords.setMax(Settings.MAX_RECORDS);

        mCBCurrentLocation.setOnClickListener(this);
        mViewCustomLocation.setOnClickListener(this);
        mSBPhotos.setOnSeekBarChangeListener(this);
        mSBRadius.setOnSeekBarChangeListener(this);
        mSBRecords.setOnSeekBarChangeListener(this);

        mSettings = DataController.getInstance().getSettings();
        try {
            mSettingsClone = (Settings) mSettings.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        loadDefaultData();
    }

    private void loadDefaultData() {

        mCBCurrentLocation.setChecked(mSettings.useCurrentLocation());
        if (mCBCurrentLocation.isChecked()){

            mViewCustomLocation.setVisibility(View.GONE);
        }else{
            mViewCustomLocation.setVisibility(View.VISIBLE);

        }
        if (mSettings.getCustomLocation()!=null && !TextUtils.isEmpty(mSettings.getCustomLocation().address)) {
            mTVCustomLocation.setText(mSettings.getCustomLocation().address);
        }
        loadDefaultSeekbarData();

    }

    private void loadDefaultSeekbarData() {
        mTVRecords.setText(String.format(getString(R.string.records_per_page), mSettings.getRecordsPerPage()));
        mSBRecords.setProgress(mSettings.getRecordsPerPage());

        mTVRadius.setText(String.format(getString(R.string.radius), mSettings.getRadius()));
        mSBRadius.setProgress(mSettings.getRadius());

        mTVPhotos.setText(String.format(getString(R.string.photos_per_page), mSettings.getPhotosPerPage()));
        mSBPhotos.setProgress(mSettings.getPhotosPerPage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkbox:
                mViewCustomLocation.setVisibility(mCBCurrentLocation.isChecked()?View.GONE:View.VISIBLE);
                mSettings.setUseCurrentLocation(mCBCurrentLocation.isChecked());
                DataController.getInstance().saveSettings();
                break;
            case R.id.ll_custom_location:launchMaps();break;

        }
    }

    private void launchMaps() {

        showProgress(getString(R.string.please_wait));
        //ask permission to launch map
        String[] requiredPermissions = anyPermissionsRequired();
        if (requiredPermissions.length == 0) {
            //no permissions required
            startPlacePicker();
        } else {
            ActivityCompat.requestPermissions(this,
                    requiredPermissions,
                    RC_PERMISSIONS);
        }

    }

    private String[] anyPermissionsRequired() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ArrayList<String> requiredPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permissions[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permissions[i]);
            }
        }
        permissions = new String[requiredPermissions.size()];
        for (int i = 0; i < requiredPermissions.size(); i++) {
            permissions[i] = requiredPermissions.get(i);
        }
        return permissions;
    }

    private void startPlacePicker() {
        try {
            Location savedLocation = mSettings.getCustomLocation();
            if (savedLocation!=null && savedLocation.hasValues()) {
                LatLng center = new LatLng(savedLocation.lat, savedLocation.lng);
                LatLngBounds.Builder b = LatLngBounds.builder();
                b.include(center);
                mBuilder.setLatLngBounds(b.build());
            }
            startActivityForResult(mBuilder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            hideProgress();
            Toast.makeText(this, getString(R.string.play_service_not_updated), Toast.LENGTH_LONG).show();

        } catch (GooglePlayServicesNotAvailableException e) {
            hideProgress();
            Toast.makeText(this, getString(R.string.play_service_not_found) , Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            hideProgress();
            Toast.makeText(this, getString(R.string.oops_went_wrong) + e, Toast.LENGTH_LONG).show();

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
                startPlacePicker();
            } else {
                Toast.makeText(this, getString(R.string.no_location_selected), Toast.LENGTH_LONG).show();
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            hideProgress();
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);

                mTVCustomLocation.setText(place.getAddress());
                Location loc = new Location();
                loc.address=place.getAddress().toString();
                loc.lat=place.getLatLng().latitude;
                loc.lng=place.getLatLng().longitude;
                mSettings.setCustomLocation(loc);
                mSettings.setUseCurrentLocation(false);
                DataController.getInstance().saveSettings();

            }else{
                //reset values
                if (mSettings.getCustomLocation()!=null && mSettings.getCustomLocation().hasValues()){
                    mTVCustomLocation.setText(mSettings.getCustomLocation().address);
                }else{
                    mTVCustomLocation.setText(getString(R.string.select_custom_location));
                }
            }

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress==0){
            //set respective min value
            switch (seekBar.getId()){
                case R.id.sb_photos_per_page:
                    progress= Settings.MIN_PHOTOS;
                    break;
                case R.id.sb_records_per_page:
                    progress= Settings.MIN_RECORDS;
                    break;
                case R.id.sb_radius:
                    progress= Settings.MIN_RADIUS;
                    break;
            }
            seekBar.setProgress(progress);
        }

        //save data
        switch (seekBar.getId()){
            case R.id.sb_photos_per_page:
                mSettings.setPhotosPerPage(progress);
                break;
            case R.id.sb_records_per_page:
                mSettings.setRecordsPerPage(progress);
                break;
            case R.id.sb_radius:
                mSettings.setRadius(progress);
                break;
        }
        DataController.getInstance().saveSettings();
        loadDefaultSeekbarData();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        if (mCBCurrentLocation.isChecked() || mSettings.getCustomLocation()==null){
            mSettings.setUseCurrentLocation(true);
        }
        DataController.getInstance().saveSettings();

        if (settingsModified()){
            setResult(RESULT_OK);
        }
        super.onBackPressed();

    }

    private boolean settingsModified() {
        if (!mSettings.getLastQuery().equalsIgnoreCase(mSettingsClone.getLastQuery())){
            return true;
        }
        if (mSettings.useCurrentLocation() != mSettingsClone.useCurrentLocation()){
            return true;
        }
        if (mSettings.getRadius()!= mSettingsClone.getRadius()){
            return true;
        }
        if (mSettings.getRecordsPerPage()!= mSettingsClone.getRecordsPerPage()){
            return true;
        }
        if (mSettings.getPhotosPerPage()!= mSettingsClone.getPhotosPerPage()){
            return true;
        }
        return false;
    }
}
