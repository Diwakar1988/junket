package com.github.diwakar1988.junket.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.util.UiUtils;
import com.github.diwakar1988.junket.util.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ZoomImageActivity extends AppCompatActivity implements Callback{

    public static final String KEY_IMAGE_URL="image_url";


    public static Intent createIntent(Context context, String url){
        Intent i = new Intent(context,ZoomImageActivity.class);
        i.putExtra(KEY_IMAGE_URL,url);
        return i ;
    }
    private View mRoot;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Snackbar mSnackbar;
    private PhotoViewAttacher mAttacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        mRoot = findViewById(R.id.activity_zoom_image);
        mImageView = (ImageView) findViewById(R.id.iv_zoom);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mAttacher = new PhotoViewAttacher(mImageView);

        loadImage();
    }

    private void loadImage(){
        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);

        String url = getIntent().getStringExtra(KEY_IMAGE_URL);
        Picasso.with(this).load(url).into(mImageView,this);
    }
    @Override
    public void onSuccess() {
        mImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mAttacher.update();
    }

    @Override
    public void onError() {
        mProgressBar.setVisibility(View.GONE);
        mSnackbar = UiUtils.showSnackBar(getString(Utils.isNetworkAvailable(this)?R.string.oops_went_wrong:R.string.no_internet_connection), getString(R.string.retry), mRoot, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.dismiss();
                loadImage();
            }
        });
    }
}
