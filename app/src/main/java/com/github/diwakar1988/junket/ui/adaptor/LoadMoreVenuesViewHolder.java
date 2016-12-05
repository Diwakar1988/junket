package com.github.diwakar1988.junket.ui.adaptor;

import android.view.View;
import android.widget.ProgressBar;

import com.github.diwakar1988.junket.R;

/**
 * Created by diwakar.mishra on 04/12/16.
 */

public class LoadMoreVenuesViewHolder extends BaseVenueViewHolder {

    public ProgressBar mProgressBar;
    public LoadMoreVenuesViewHolder(View view) {
        super(view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }
}