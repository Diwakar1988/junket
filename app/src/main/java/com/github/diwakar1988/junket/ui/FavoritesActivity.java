package com.github.diwakar1988.junket.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.db.DataController;
import com.github.diwakar1988.junket.pojo.Venue;
import com.github.diwakar1988.junket.ui.adaptor.FavoriteVenuesAdapter;
import com.github.diwakar1988.junket.ui.adaptor.ItemClickListener;
import com.github.diwakar1988.junket.ui.adaptor.OnLoadMoreListener;
import com.github.diwakar1988.junket.util.BackgroundExecutor;
import com.github.diwakar1988.junket.util.PhotoUtil;
import com.github.diwakar1988.junket.util.UiUtils;
import com.github.diwakar1988.junket.util.Utils;

import java.util.ArrayList;

public class FavoritesActivity extends AppBaseActivity implements ItemClickListener,OnLoadMoreListener {
    public static final int LIMIT_DB_RECORDS=20;
    public static Intent createIntent(Context context){
        Intent i = new Intent(context,FavoritesActivity.class);
        return i ;
    }

    private View mRoot;
    private View mProgressView;
    private RecyclerView mRecyclerView;
    private FavoriteVenuesAdapter mAdapter;
    private int mFromRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setUpTitleBar(getString(R.string.favorites),true);

        mProgressView =  findViewById(R.id.ll_progress);


        mRoot = findViewById(R.id.activity_favorites);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_venues);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FavoriteVenuesAdapter(this, new ArrayList<Venue>(),this,this);
        mRecyclerView.setAdapter(mAdapter);

        loadVenues();
    }

    private void loadVenues() {
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {
                try{
                    final ArrayList<Venue> venues = DataController.getInstance().loadFavoriteVenue(mFromRecord,LIMIT_DB_RECORDS);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onVenuesLoaded(venues);
                        }
                    });
                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onVenuesLoadError(e);
                        }
                    });

                }
            }
        });
    }
    private void onVenuesLoaded(ArrayList<Venue> venues ){
        mProgressView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        hideListLoadingProgress();
        if (venues!=null && !venues.isEmpty()){
            //set loaded data into adaptor
            mAdapter.addVenues(venues);
            mFromRecord = mAdapter.getVenueCount();

        }else{
            UiUtils.showSnackBar(getString(R.string.no_more_favorite_venues),mRoot, Snackbar.LENGTH_SHORT);
        }

    }
    private void onVenuesLoadError(Exception e){
            mProgressView.setVisibility(View.GONE);
            hideListLoadingProgress();
            UiUtils.showSnackBar(getString(R.string.oops_went_wrong),mRoot, Snackbar.LENGTH_SHORT);
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

    private Dialog mDialog;
    @Override
    public void onItemClicked(View view, int position) {

        final Venue venue= mAdapter.getVenue(position);

        if (view.getId()==R.id.tv_venue_contact){
            if (venue.contact!=null && !TextUtils.isEmpty(venue.contact.getNumber())){
                Utils.call(this,venue.contact.getNumber());
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
                    startActivity(ZoomImageActivity.createIntent(FavoritesActivity.this,PhotoUtil.createFullPhotoURL(venue.getFirstPhoto())));
                    mDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onLoadMore() {
        loadVenues();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_activity_favorites, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                if (mAdapter.getVenueCount()>0) {
                    UiUtils.showConfirmation(this, getString(R.string.warning_delete_all_favorite_venues), getString(R.string.yes), getString(R.string.no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAllFavoriteVenues();
                        }
                    });
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllFavoriteVenues() {
        showProgress(getString(R.string.please_wait));
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {
                DataController.getInstance().deleteAllFavoriteVenues();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Toast.makeText(FavoritesActivity.this, "Favorite venues deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
