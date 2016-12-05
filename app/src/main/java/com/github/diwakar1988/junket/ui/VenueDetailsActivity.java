package com.github.diwakar1988.junket.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.db.DataController;
import com.github.diwakar1988.junket.db.Settings;
import com.github.diwakar1988.junket.db.dao.Review;
import com.github.diwakar1988.junket.net.service.ServiceController;
import com.github.diwakar1988.junket.net.service.response.PhotoServiceResponse;
import com.github.diwakar1988.junket.net.service.response.TipServiceResponse;
import com.github.diwakar1988.junket.pojo.Location;
import com.github.diwakar1988.junket.pojo.Photo;
import com.github.diwakar1988.junket.pojo.Tip;
import com.github.diwakar1988.junket.pojo.User;
import com.github.diwakar1988.junket.pojo.Venue;
import com.github.diwakar1988.junket.ui.adaptor.ItemClickListener;
import com.github.diwakar1988.junket.ui.adaptor.VenueTipsAdapter;
import com.github.diwakar1988.junket.util.BackgroundExecutor;
import com.github.diwakar1988.junket.util.PhotoUtil;
import com.github.diwakar1988.junket.util.UiUtils;
import com.github.diwakar1988.junket.util.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueDetailsActivity extends AppBaseActivity implements View.OnClickListener, ItemClickListener,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private static final String KEY_VENUE="mVenue";
    private static final String KEY_INDEX ="index";
    private static final String KEY_DISLIKED="disliked";
    private static final String KEY_EXTRA="extra";
    private static final String KEY_FULL_IMAGE="full_image";

    public static Intent createIntent(Context context,Venue venue,int index){
        Intent i = new Intent(context,VenueDetailsActivity.class);
//        i.putExtra(KEY_VENUE,mVenue);
        //Venue object is using ArrayList, since List is not parcelable hence getting object in JSON format, we can fix it by making in Venue object Serializable
        i.putExtra(KEY_VENUE,new Gson().toJson(venue,Venue.class));
        i.putExtra(KEY_INDEX,index);

        return i ;
    }
    private Venue mVenue;

    private TextView mTvName;
    private TextView mTvContact;
    private TextView mTvAddress;
    private TextView mTvRatings;
    private TextView mTvDistance;
    private TextView mTvFavorite;
    private TextView mTvReview;
    private TextView mTvDirections;
    private SliderLayout mDemoSlider;



    private VenueTipsAdapter mTipsAdapter;
    private RecyclerView mRvTips;
    private TextView mTvPeopleSays;
    private ProgressBar mProgressBar;
    private View mRlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

//        mVenue = getIntent().getParcelableExtra(KEY_VENUE);
        //Venue object is using ArrayList, since List is not parcelable hence getting object in JSON format, we can fix it by making in Venue object Serializable
        mVenue = new Gson().fromJson(getIntent().getStringExtra(KEY_VENUE),Venue.class);
        setUpTitleBar(mVenue.name,true);


        mTvName = (TextView) findViewById(R.id.tv_venue_name);
        mTvContact = (TextView) findViewById(R.id.tv_venue_contact);
        mTvAddress = (TextView)findViewById(R.id.tv_venue_address);
        mTvRatings = (TextView) findViewById(R.id.tv_venue_rating);
        mTvDistance = (TextView) findViewById(R.id.tv_venue_distance);
        mTvFavorite = (TextView) findViewById(R.id.tv_favorite);
        mTvReview = (TextView) findViewById(R.id.tv_review);
        mTvDirections = (TextView) findViewById(R.id.tv_direction);
        mTvPeopleSays = (TextView) findViewById(R.id.tv_people_says);

        mRlProgress = findViewById(R.id.rl_progress_bar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mDemoSlider = (SliderLayout)findViewById(R.id.venue_photo_slider);

        mTvContact.setOnClickListener(this);
        mTvFavorite.setOnClickListener(this);
        mTvReview.setOnClickListener(this);
        mTvDirections.setOnClickListener(this);

        setVenueHeaderDetails();

        //setup tips
        mRvTips = (RecyclerView) findViewById(R.id.rv_venue_tips);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvTips.setLayoutManager(layoutManager);
        mTipsAdapter = new VenueTipsAdapter(this, new TipServiceResponse.Tips(),this);
        mRvTips.setAdapter(mTipsAdapter);

        loadTips(mVenue.id);
        loadPhotos(mVenue.id);
    }

    private void loadPhotos(String venueId) {

        Map<String, String> options=new HashMap<>();
        options.put("limit",String.valueOf(DataController.getInstance().getSettings().getPhotosPerPage()));

        ServiceController.getInstance().loadPhotos(venueId,options,new Callback<PhotoServiceResponse>() {
            @Override
            public void onResponse(Call<PhotoServiceResponse> call, Response<PhotoServiceResponse> response) {
                hideProgress();
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    setUpPhotoSlider(response.body().getPhotos());
                }
            }

            @Override
            public void onFailure(Call<PhotoServiceResponse> call, Throwable t) {
                hideProgress();
                Log.d("ERROR",t.toString());
                mRlProgress.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mDemoSlider.setVisibility(View.GONE);
            }
        });


    }

    private void setUpPhotoSlider(PhotoServiceResponse.Photos photos) {


        if (photos.items==null || photos.items.size()==0){
            mDemoSlider.setVisibility(View.GONE);
            return;
        }
        mDemoSlider.setVisibility(View.VISIBLE);

        for (int i = 0; i < photos.items.size(); i++) {
            Photo item = photos.items.get(i);
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(item.user.getName())
                    .image(PhotoUtil.createSmallPhotoURL(item))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString(KEY_EXTRA,PhotoUtil.createSmallPhotoURL(item));
            textSliderView.getBundle()
                    .putString(KEY_FULL_IMAGE,PhotoUtil.createFullPhotoURL(item));
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

    }

    private void setVenueHeaderDetails() {
        //set name
        mTvName.setText(mVenue.name);

        // set mVenue contact if available
        if (mVenue.contact!=null && !TextUtils.isEmpty(mVenue.contact.getNumber())){
            mTvContact.setText(mVenue.contact.getNumber());
            mTvContact.setTextColor(getResources().getColor(R.color.blue));
        }

        // set mVenue address if available
        if (mVenue.location!=null){

            //set mVenue hours and distance if available
            float distance= (mVenue.location.distance/(float)1000);
            String hour = mVenue.hours!=null ? getString(R.string.open):getString(R.string.closed);
            mTvDistance.setText(String.format(getString(R.string.text_venue_distance),new DecimalFormat("0.##").format(distance),hour));

            if (!TextUtils.isEmpty(mVenue.location.getCompleteAddress())){
                mTvAddress.setText(mVenue.location.getCompleteAddress());


            }
            else             if (!TextUtils.isEmpty(mVenue.location.address)){
                mTvAddress.setText(mVenue.location.address);
            }

        }else{
            String hour = mVenue.hours!=null ? getString(R.string.open):getString(R.string.closed);
           mTvDistance.setText(String.format(getString(R.string.text_venue_distance),"0",hour));
        }

        // set mVenue ratings
        mTvRatings.setText(String.valueOf(mVenue.rating));

        // set mVenue rating color if available otherwise BLACK bg
        try{
            if (!mVenue.ratingColor.startsWith("#")){
                mTvRatings.setBackgroundColor(Color.parseColor("#"+ mVenue.ratingColor));
            }else{
                mTvRatings.setBackgroundColor(Color.parseColor(mVenue.ratingColor));

            }
        }catch (Exception e){
            mTvRatings.setBackgroundColor(Color.RED);
        }


    }

    private void loadTips(String venueId){

        Map<String, String> options=new HashMap<>();
        options.put("limit",String.valueOf(DataController.getInstance().getSettings().getRecordsPerPage()));
        options.put("sort","recent");

        ServiceController.getInstance().loadTips(venueId,options,new Callback<TipServiceResponse>() {
            @Override
            public void onResponse(Call<TipServiceResponse> call, Response<TipServiceResponse> response) {
                hideProgress();
                if (response.isSuccessful()){
                    onTipResponse(true,response.body().getTips());
                }else{
                    onTipResponse(false,null);
                }
            }

            @Override
            public void onFailure(Call<TipServiceResponse> call, Throwable t) {

                hideProgress();
                onTipResponse(false,null);

            }
        });

    }
    private void onTipResponse(boolean isSuccessful, TipServiceResponse.Tips tips){

            Review review=DataController.getInstance().getReview(mVenue.id);
            if (review!=null){
                //no review found for this mVenue
                Tip t = new Tip();
                t.text=review.getText();
                t.user=new User();
                t.user.firstName=getString(R.string.you);
                if (tips==null){
                    tips=new TipServiceResponse.Tips();
                }
                if (tips.items==null){
                    tips.items=new ArrayList<>();
                }
                tips.items.add(0,t);

            }
            if (tips==null){
                mTvPeopleSays.setVisibility(View.GONE);
                mRvTips.setVisibility(View.GONE);
            }else{
                mTvPeopleSays.setVisibility(View.VISIBLE);
                mRvTips.setVisibility(View.VISIBLE);
                mTipsAdapter.setTips(tips);
            }
    }

    @Override
    public void onClick(final View v) {
        UiUtils.animateView(v, new Runnable() {
            @Override
            public void run() {

                switch (v.getId()){
                    case R.id.tv_venue_contact:
                        if (mVenue.contact!=null && !TextUtils.isEmpty(mVenue.contact.getNumber())){
                            Utils.call(VenueDetailsActivity.this, mVenue.contact.getNumber());
                        }
                        break;
                    case R.id.tv_favorite:
                        saveAsFavoriteVenue();
                        break;
                    case R.id.tv_review: onAddReviewClicked();break;
                    case R.id.tv_direction: launchDirections();break;

                }
            }
        });
    }

    private void saveAsFavoriteVenue() {

        if (DataController.getInstance().hasFavoriteVenue(mVenue.id)){
            Toast.makeText(this, getString(R.string.already_marked_favorite), Toast.LENGTH_SHORT).show();
            return;
        }
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {

                DataController.getInstance().saveFavoriteVenue(mVenue);
            }
        });
        Toast.makeText(this, getString(R.string.marked_favorite), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    private Dialog mDialog;
    @Override
    public void onItemClicked(final View view, final int position) {

        if (view.getId()==R.id.iv_tip_user){
            final String url= PhotoUtil.createSmallPhotoURL(mTipsAdapter.getTip(position).user.photo);
            mDialog = UiUtils.showImageOnDialog(VenueDetailsActivity.this, url, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    /*PhotoUtil.createFullPhotoURL(mTipsAdapter.getTip(position).user.photo)*/
                    //use small pic URL since we're not getting full image URL in Tip service response
                    startActivity(ZoomImageActivity.createIntent(VenueDetailsActivity.this,url));
                }
            });
        }
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

        startActivity(ZoomImageActivity.createIntent(this,slider.getBundle().get(KEY_FULL_IMAGE).toString()));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void onAddReviewClicked() {

        UiUtils.showInputDialog(this,getString(R.string.add_review),getString(R.string.review_warning), new OnInputSaveListener() {
            @Override
            public void onInputSave(final String input) {
                saveReview(input);

            }
        });
    }

    private void saveReview(String msg) {
        final Review review=new Review();
        review.setVenueId(mVenue.id);
        review.setDate(new Date());
        review.setText(msg);
        BackgroundExecutor.getInstance().run(new Runnable() {
            @Override
            public void run() {

                DataController.getInstance().saveReview(review);
            }
        });
        Toast.makeText(this, getString(R.string.review_added), Toast.LENGTH_SHORT).show();
        loadTips(mVenue.id);

    }
    private void launchDirections(){

        Settings settings = DataController.getInstance().getSettings();
        Location srcLoc = settings.useCurrentLocation() ? settings.getCurrentLocation():settings.getCustomLocation();

        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.google.com/maps?");
        sb.append("saddr=").append(srcLoc.lat).append(',').append(srcLoc.lng);
        sb.append('&');
        sb.append("daddr=").append(mVenue.location.lat).append(',').append(mVenue.location.lng);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(sb.toString()));

        startActivity(intent);
    }

}
