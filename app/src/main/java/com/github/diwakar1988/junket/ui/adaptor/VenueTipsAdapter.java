package com.github.diwakar1988.junket.ui.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.net.service.response.TipServiceResponse;
import com.github.diwakar1988.junket.pojo.Tip;
import com.github.diwakar1988.junket.pojo.User;
import com.github.diwakar1988.junket.util.CircleTransform;
import com.github.diwakar1988.junket.util.PhotoUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by diwakar.mishra on 04/12/16.
 */

public class VenueTipsAdapter extends RecyclerView.Adapter<VenueTipsAdapter.VenueTipViewHolder> {
    private Context mContext;
    private TipServiceResponse.Tips mTips;
    private LayoutInflater mInflater;
    private ItemClickListener mListener;

    public VenueTipsAdapter(Context context, TipServiceResponse.Tips tips, ItemClickListener listener) {
        this.mContext = context;
        this.mTips = tips;
        mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    public Tip getTip(int position) {
        return mTips.items.get(position);
    }


    public void setTips(TipServiceResponse.Tips tips) {
        this.mTips = tips;
        notifyDataSetChanged();
    }

    @Override
    public VenueTipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_venue_tip_list_item, parent, false);
        VenueTipViewHolder holder = new VenueTipViewHolder(mListener,view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VenueTipViewHolder vh, int position) {
        Tip tip=getTip(position);
        User user = tip.user;

        vh.tvName.setText(user.getName());
        vh.tvTip.setText(tip.text);
        vh.tvLikes.setText(String.valueOf(tip.agreeCount));
        vh.tvDislikes.setText(String.valueOf(tip.disagreeCount));

        // set tip user picture thumbnail if available
        String picURL= PhotoUtil.createSmallPhotoURL(user.photo);
        Picasso.with(mContext)
                .load(picURL)
                .transform(new CircleTransform())
                .into(vh.ivUserIcon);
    }


    @Override
    public int getItemCount() {
        return mTips.items==null?0: mTips.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class VenueTipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ItemClickListener listener;
        private ImageView ivUserIcon;
        private TextView tvName;
//        private TextView tvDate;
        private TextView tvTip;
        private TextView tvLikes;
        private TextView tvDislikes;

        public VenueTipViewHolder(ItemClickListener listener, View view) {
            super(view);
            this.listener = listener;

            ivUserIcon= (ImageView) view.findViewById(R.id.iv_tip_user);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvTip = (TextView) view.findViewById(R.id.tv_tip);
            tvLikes = (TextView) view.findViewById(R.id.tv_likes);
            tvDislikes = (TextView) view.findViewById(R.id.tv_dislikes);
//            tvDate = (TextView) view.findViewById(R.id.tv_date);

            ivUserIcon.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onItemClicked(v,getAdapterPosition());
        }
    }


}
