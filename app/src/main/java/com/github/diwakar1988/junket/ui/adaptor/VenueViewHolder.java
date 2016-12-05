package com.github.diwakar1988.junket.ui.adaptor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.diwakar1988.junket.R;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class VenueViewHolder extends BaseVenueViewHolder implements View.OnClickListener {

    public ItemClickListener mListener;
    public TextView mTvName;
    public TextView mTvContact;
    public TextView mTvAddress;
    public TextView mTvTip;
    public ImageView mIvIcon;
    public TextView mTvRatings;
    public TextView mTvDistance;

    public VenueViewHolder(ItemClickListener listener,View view) {
        super(view);
        this.mListener = listener;

        mTvName = (TextView) view.findViewById(R.id.tv_venue_name);
        mTvContact = (TextView) view.findViewById(R.id.tv_venue_contact);
        mTvAddress = (TextView)view.findViewById(R.id.tv_venue_address);
        mTvTip = (TextView) view.findViewById(R.id.tv_venue_tip);
        mTvRatings = (TextView) view.findViewById(R.id.tv_venue_rating);
        mTvDistance = (TextView) view.findViewById(R.id.tv_venue_distance);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_venue_thumb);
        view.setOnClickListener(this);
        mTvContact.setOnClickListener(this);
        mIvIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClicked(v,getAdapterPosition());
    }
}
