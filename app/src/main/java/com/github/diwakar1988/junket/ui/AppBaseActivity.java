package com.github.diwakar1988.junket.ui;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.diwakar1988.junket.net.service.event.Event;
import com.github.diwakar1988.junket.net.service.event.EventListener;

/**
 * Created by diwakar.mishra on 02/12/16.
 */
public class AppBaseActivity extends AppCompatActivity implements EventListener{


    private ProgressDialog mProgressDialog;

    protected void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "",
                    message, true);
        } else if (mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(message);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);


    }

    protected void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    public void setUpTitleBar(String title, boolean backButton) {
        // Setup action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(backButton);
        if (!actionBar.isShowing()) {
            actionBar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEvent(Event event) {

    }
}
