package com.github.diwakar1988.junket.ui.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.net.service.response.VenueServiceResponse;
import com.github.diwakar1988.junket.pojo.Tip;
import com.github.diwakar1988.junket.pojo.Venue;
import com.github.diwakar1988.junket.util.PhotoUtil;
import com.github.diwakar1988.junket.util.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 03/12/16.
 */

public class VenuesAdapter extends RecyclerView.Adapter<BaseVenueViewHolder> {
    private static final int ITEM_TYPE_VENUE=0X1F;
    private static final int ITEM_TYPE_PROGRESS=0X2F;
    private static final int ITEM_TYPE_EMPTY=0X3F;

    private static final Transformation ROUNDED_CORNERS_TRANSFORMATION = new RoundedCornersTransformation(10, 5);

    private Context mContext;
    private ArrayList<VenueServiceResponse.VenueItem> mVenueItems;
    private LayoutInflater mInflater;
    private ItemClickListener mListener;
    private OnLoadMoreListener mLoadMoreListener;

    public VenuesAdapter(Context context, ArrayList<VenueServiceResponse.VenueItem> venueItems,ItemClickListener listener,OnLoadMoreListener loadMoreListener) {
        this.mContext = context;
        this.mVenueItems = venueItems==null ? new ArrayList<VenueServiceResponse.VenueItem>() : venueItems;
        mInflater = LayoutInflater.from(context);
        this.mListener = listener;
        this.mLoadMoreListener = loadMoreListener;
    }

    public VenueServiceResponse.VenueItem getVenueItem(int position) {
        return mVenueItems.get(position);
    }

    public void addVenueItems(ArrayList<VenueServiceResponse.VenueItem> venueItems) {

        if (venueItems!=null && venueItems.size()>0) {
            this.mVenueItems.addAll(venueItems);
        }
        notifyDataSetChanged();
    }
    public void clear(){
        this.mVenueItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public BaseVenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        BaseVenueViewHolder holder;
        if (viewType==ITEM_TYPE_EMPTY){
            view = mInflater.inflate(R.layout.layout_empty_venues, parent, false);
            holder = new EmptyVenuesViewHolder(view);

        }
        else if (viewType==ITEM_TYPE_VENUE){
            view = mInflater.inflate(R.layout.layout_venue_list_item, parent, false);
            holder = new VenueViewHolder(mListener,view);

        }else{
            view = mInflater.inflate(R.layout.layout_view_venue_load_more, parent, false);
            holder = new LoadMoreVenuesViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseVenueViewHolder holder, int position) {

        if (holder instanceof VenueViewHolder) {
            VenueViewHolder vh = (VenueViewHolder) holder;

            VenueServiceResponse.VenueItem venueItem = getVenueItem(position);
            Venue venue = venueItem.venue;

            //create name
            StringBuilder sb = new StringBuilder();
            sb.append((position + 1)).append('.').append(' ').append(venue.name);

            vh.mTvName.setText(sb.toString());
            sb.setLength(0);
            sb = null;

            // set venue contact if available
            if (venue.contact != null && !TextUtils.isEmpty(venue.contact.getNumber())) {
                vh.mTvContact.setText(venue.contact.getNumber());
                vh.mTvContact.setTextColor(mContext.getResources().getColor(R.color.blue));
            }

            // set venue address if available
            if (venue.location != null) {

                //set venue hours and distance if available
                float distance = (venue.location.distance / (float) 1000);
                String hour = venue.hours != null ? mContext.getString(R.string.open) : mContext.getString(R.string.closed);
                vh.mTvDistance.setText(String.format(mContext.getString(R.string.text_venue_distance), new DecimalFormat("0.##").format(distance), hour));

                if (!TextUtils.isEmpty(venue.location.getCompleteAddress())) {
                    vh.mTvAddress.setText(venue.location.getCompleteAddress());


                } else if (!TextUtils.isEmpty(venue.location.address)) {
                    vh.mTvAddress.setText(venue.location.address);
                }

            } else {
                String hour = venue.hours != null ? mContext.getString(R.string.open) : mContext.getString(R.string.closed);
                vh.mTvDistance.setText(String.format(mContext.getString(R.string.text_venue_distance), "0", hour));
            }

            // set venue ratings
            vh.mTvRatings.setText(String.valueOf(venue.rating));

            // set venue rating color if available otherwise BLACK bg
            try {
                if (!venue.ratingColor.startsWith("#")) {
                    vh.mTvRatings.setBackgroundColor(Color.parseColor("#" + venue.ratingColor));
                } else {
                    vh.mTvRatings.setBackgroundColor(Color.parseColor(venue.ratingColor));

                }
            } catch (Exception e) {
                vh.mTvRatings.setBackgroundColor(Color.BLACK);
            }


            //set venue tip if available
            Tip tip = venueItem.getFirstTip();
            if (tip != null && !TextUtils.isEmpty(tip.text)) {
                vh.mTvTip.setText(String.format(mContext.getString(R.string.tip), tip.text));
            } else {
                vh.mTvTip.setText(String.format(mContext.getString(R.string.tip_place_holder), ""));
            }

            // set venue picture thumbnail if available
            String picURL = PhotoUtil.createSmallPhotoURL(venue.getFirstPhoto());
            if (venue.photos != null && !TextUtils.isEmpty(picURL)) {
                Picasso.with(mContext)
                        .load(picURL)
                        .placeholder(R.drawable.bg_rounded_gray)
                        .transform(ROUNDED_CORNERS_TRANSFORMATION)
                        .into(vh.mIvIcon);
            }
        }
        else if (holder instanceof EmptyVenuesViewHolder){
            EmptyVenuesViewHolder vh = (EmptyVenuesViewHolder) holder;
            vh.mTvTitle.setText(mContext.getString(R.string.venue_list_empty_view_title));
            vh.mTvDescription.setText(mContext.getString(R.string.venue_list_empty_view_desc));

        }
        else{
            LoadMoreVenuesViewHolder vh = (LoadMoreVenuesViewHolder) holder;
            vh.mProgressBar.setVisibility(View.VISIBLE);
            mLoadMoreListener.onLoadMore();
        }

    }



    public int getVenueItemCount(){
        return mVenueItems.size();
    }
    public boolean isEmpty(){
        return getVenueItemCount()<=0;
    }

    @Override
    public int getItemCount() {
        //add one more item for empty or loadMore view item
        return getVenueItemCount()+1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position==0 && getVenueItemCount()==0){
            return ITEM_TYPE_EMPTY;
        }
        else if (position< getVenueItemCount()){
            return ITEM_TYPE_VENUE;
        }
        return ITEM_TYPE_PROGRESS;
    }

    public void deleteVenue(int position) {
        if (mVenueItems !=null){
            mVenueItems.remove(position);
            notifyItemRemoved(position);
        }
    }


}
