package com.github.diwakar1988.junket.ui.adaptor;

import android.view.View;
import android.widget.TextView;

import com.github.diwakar1988.junket.R;

/**
 * Created by diwakar.mishra on 03/12/16.
 */

public class EmptyVenuesViewHolder extends BaseVenueViewHolder {

    public TextView mTvTitle;
    public TextView mTvDescription;

    public EmptyVenuesViewHolder(View view) {
        super(view);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mTvDescription = (TextView) view.findViewById(R.id.tv_description);
    }
}