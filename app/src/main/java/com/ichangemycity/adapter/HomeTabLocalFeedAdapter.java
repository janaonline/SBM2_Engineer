package com.ichangemycity.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.swachhbharatengineer.CommentsActivity;
import com.ichangemycity.swachhbharatengineer.ComplaintDetailNew;
import com.ichangemycity.swachhbharatengineer.MainActivity;
import com.ichangemycity.swachhbharatengineer.R;
import com.ichangemycity.webservice.ParseComplaintData;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeTabLocalFeedAdapter extends RecyclerView.Adapter<HomeTabLocalFeedAdapter.ViewHolder> {

    private static Activity activity;
    // ArrayList<ComplaintData> data = new ArrayList<ComplaintData>();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public HomeTabLocalFeedAdapter(Activity activity) {
        HomeTabLocalFeedAdapter.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        if (i == TYPE_HEADER) {
            v = LayoutInflater.from(activity).inflate(R.layout.inflate_primer, null, false);
        } else if (i == TYPE_ITEM) {
            v = LayoutInflater.from(activity).inflate(R.layout.home_complaint_card, null, false);
        }
        return new ViewHolder(v, i);
    }

    @Override
    public int getItemCount() {
        return MainActivity.data.size();
    }

    @Override
    public int getItemViewType(int position) {
        // if (position == 0)
        // return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_username)
        TextView tv_username;
        @BindView(R.id.complaint_status)
        TextView complaint_status;
        @BindView(R.id.moreInfo)
        TextView moreInfo;
        @BindView(R.id.created_on)
        TextView created_on;
        @BindView(R.id.complaint_category)
        TextView complaint_category;
        @BindView(R.id.complaint_location)
        TextView complaint_location;
        @BindView(R.id.tv_feed_user_name)
        TextView tv_feed_user_name;
        @BindView(R.id.tv_feed)
        TextView tv_feed;
        /* @BindView(R.id.votedUpCount)
         TextView votedUpCount;
         @BindView(R.id.share)
         TextView share;*/
        @BindView(R.id.commentedCount)
        TextView commentedCount;
        @BindView(R.id.ic_directions)
        ImageView ic_directions;
        @BindView(R.id.rl_cc_top)
        RelativeLayout rl_cc_top;
        @BindView(R.id.rl_top_feed)
        RelativeLayout rl_top_feed;
        @BindView(R.id.complaint_image)
        NetworkImageView complaint_image;
        @BindView(R.id.category_image)
        ImageView category_image;
        @BindView(R.id.feed_flag)
        ImageView feed_flag;
        @BindView(R.id.changeStatus)
        Spinner changeStatus;
        @BindView(R.id.frameSpinner)
        FrameLayout frameSpinner;
        @BindView(R.id.card)
        LinearLayout card;
        @BindView(R.id.user_image)
        CircleImageView user_image;
        @BindView(R.id.rl_cc_images)
        RelativeLayout rl_cc_images;
        @BindView(R.id.complaintId)
        TextView complaintId;

        public ViewHolder(final View convertView, int type) {
            super(convertView);
            if (type == TYPE_ITEM) {
                ButterKnife.bind(this, convertView);
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder v, final int arg1) {
        // if (arg1 == 0) {
        // v.textPostComplaint.setText("All Complaints");
        // } else {
        final ComplaintData cData = MainActivity.data.get(arg1);
        v.tv_username.setText(cData.getFull_name());
        v.created_on.setText(cData.getCreated_at());
        v.complaint_status.setText(cData.getComplaint_status());
        ParseComplaintData.setImage(activity, v.user_image, null, cData.getUser_image(), true);
        v.complaint_image.requestLayout();
        v.complaint_category.setText(cData.getCategory_name());
        v.complaint_location.setText(cData.getLocation());
        v.complaint_status.setText(cData.getComplaint_status());
        v.moreInfo.setText(cData.getLandmark());
        v.complaintId.setText(activity.getResources().getString(R.string.id_) + ": " + cData.getGeneric_id());

        //        v.votedUpCount.setText(cData.getVote_up_count() + "");
        v.commentedCount.setText(cData.getComment_count() + "");
        v.rl_cc_top.setTag(cData);
        v.card.setOnClickListener(m -> {
            // TODO Auto-generated method stub
            ComplaintData mCData = (ComplaintData) v.rl_cc_top.getTag();
            AppController.selectedComplaintData = mCData;
            Intent toComplaintDetail = new Intent(activity, ComplaintDetailNew.class);
            activity.startActivity(toComplaintDetail);

        });

        v.commentedCount.setOnClickListener(m -> {
            // TODO Auto-generated method stub
            final ComplaintData mCData = (ComplaintData) v.rl_cc_top.getTag();
            AppController.selectedComplaintData = mCData;
            AppController.selectedComplaintData.setToChangeStatus(false);
            Intent toCommentsActivity = new Intent(activity, CommentsActivity.class);
            activity.startActivity(toCommentsActivity);
        });
        v.ic_directions.setOnClickListener(m -> {
            // TODO Auto-generated method stub
            final ComplaintData complaintDetailData = (ComplaintData) v.rl_cc_top.getTag();
            String uri = "google.navigation:q=" + complaintDetailData.getLatitude() + "," + complaintDetailData.getLongitude();
            // Uri uri = Uri.parse("geo:" + cData.getLatitude() + ","
            // + cData.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
            mapIntent.setPackage("com.google.android.apps.maps");
            // if (mapIntent.resolveActivity(getPackageManager()) != null) {
            activity.startActivity(mapIntent);
            // }

        });
      /*  v.share.setOnClickListener(m -> {
            final ComplaintData mCData = (ComplaintData) v.rl_cc_top.getTag();
            ParseComplaintData.shareComplaint(activity, mCData);
        });*/
        if (Integer.parseInt(cData.getComplaint_status_id()) == AppController.COMPLAINT_REJECTED) {
            v.rl_top_feed.setVisibility(View.GONE);
        } else {
            v.rl_top_feed.setVisibility(View.VISIBLE);
            v.tv_feed.setText(cData.getFeed_description());
            v.tv_feed_user_name.setText(cData.getFeed_full_name());
            setTopFeedForCard(cData, v.rl_top_feed, v.tv_feed, v.tv_feed_user_name, v.feed_flag);
        }
        ParseComplaintData.getInstance().setCategoryImage(activity, v.category_image, cData);

        if (!TextUtils.isEmpty(cData.getComplaint_image())) {
            v.rl_cc_images.setVisibility(View.VISIBLE);
            ParseComplaintData.setImage(activity, null, v.complaint_image, cData.getComplaint_image(), false);
        } else {
            v.rl_cc_images.setVisibility(View.GONE);
        }
        AppController.customizeChangeStatusDropdown(activity, cData, v.changeStatus, v.frameSpinner);
        // }
    }

    private void setTopFeedForCard(final ComplaintData cData, final RelativeLayout rl_top_feed, final TextView tv_feed, final TextView tv_feed_user_name, ImageView feed_flag) {
        if (cData.isHasFeed()) {
            rl_top_feed.setVisibility(View.VISIBLE);
            if (cData.get_is_feed_high_priority().equalsIgnoreCase("1")) {
                feed_flag.setVisibility(View.VISIBLE);
                feed_flag.setColorFilter(activity.getResources().getColor(R.color.red_reopn_open));
            } else {
                feed_flag.setVisibility(View.GONE);
            }

            if (cData.getFeed_color().equalsIgnoreCase("R")) {
                tv_feed_user_name.setTextColor(activity.getResources().getColor(R.color.red_reopn_open));
                tv_feed.setTextColor(activity.getResources().getColor(R.color.red_reopn_open));
            } else if (cData.getFeed_color().equalsIgnoreCase("G")) {
                tv_feed_user_name.setTextColor(activity.getResources().getColor(R.color.green_resolved));
                tv_feed.setTextColor(activity.getResources().getColor(R.color.green_resolved));
            } else if (cData.getFeed_color().equalsIgnoreCase("B")) {
                tv_feed_user_name.setTextColor(activity.getResources().getColor(R.color.blue_on_the_job));
                tv_feed.setTextColor(activity.getResources().getColor(R.color.blue_on_the_job));
            } else {
                tv_feed_user_name.setTextColor(activity.getResources().getColor(R.color.black));
                tv_feed.setTextColor(activity.getResources().getColor(R.color.black));
            }
        } else {
            rl_top_feed.setVisibility(View.GONE);
        }
    }


}
