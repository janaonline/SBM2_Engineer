package com.ichangemycity.adapter;

import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ichangemycity.appdata.AppController;
import com.ichangemycity.model.CommentsData;
import com.ichangemycity.swachhbharatengineer.R;
import com.ichangemycity.webservice.ParseComplaintData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by srimadhu.s on 19-07-2017.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.AddRemarkViewHolder> {

    //    private ComplaintData arrayList;
    public static Activity activity;
    private static Handler handler = new Handler();
    float wt_px, ht_px, margin;

    public CommentsAdapter(Activity mAct) {
        activity = mAct;
        //        arrayList = complaintData;
        wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, activity.getResources().getDisplayMetrics());
        ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, activity.getResources().getDisplayMetrics());
        margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
    }

    @Override
    public AddRemarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_comment, parent, false);
        return new AddRemarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddRemarkViewHolder holder, int position) {

        final CommentsData commentData = AppController.selectedComplaintData.getCommentsData().get(position);
        //        String text = "<b><font color=" + commentData.getSpanColorForCoplaintStatus() + " >" + commentData.getComment_complaint_status()
        //                .toUpperCase()
        //                + "</font></b>" + "<font color=#212121>" + " " + commentData.getComment_description() + "</font>";
        //        holder.mDescription.setText(Html.fromHtml(text));
        //        holder.mName.setText(commentData.getComment_full_name());
        //        holder.mpostedOn.setText(commentData.getComment_posted_on());
        //        ParseComplaintData.setImage(activity, holder.mUserImage, null, commentData.getUser_image_url(), true);

        holder.bind(commentData);

    }

    @Override
    public int getItemCount() {
        return AppController.selectedComplaintData.getCommentsData().size();
    }

    class AddRemarkViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.userName)
        TextView mName;
        @Nullable
        @BindView(R.id.description_count)
        TextView mDescription;
        @Nullable
        @BindView(R.id.postedOn)
        TextView mpostedOn;
        @Nullable
        @BindView(R.id.userImage)
        de.hdodenhof.circleimageview.CircleImageView mUserImage;
        @BindView(R.id.comment_image)
        ImageView comment_image;
        @BindView(R.id.cvCommentImage)
        CardView cvCommentImage;

        public AddRemarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final CommentsData commentData) {
            String text = (!TextUtils.isEmpty(commentData.getComment_complaint_status()) ? ((commentData.getComment_complaint_status() + " - ")) : "") + commentData.getComment_description();
            mDescription.setText(Html.fromHtml(text));
            mName.setText(commentData.getComment_full_name());
            mpostedOn.setText(commentData.getComment_posted_on());
            ParseComplaintData.getInstance().setImage(activity, mUserImage, null, commentData.getUser_image_url(), true);
            if(TextUtils.isEmpty(commentData.getComment_image_url())) {
                cvCommentImage.setVisibility(View.GONE);
                comment_image.setVisibility(View.GONE);
            } else {
                cvCommentImage.setVisibility(View.VISIBLE);
                comment_image.setVisibility(View.VISIBLE);
                ParseComplaintData.getInstance().setImage(activity, null, comment_image, commentData.getComment_image_url().trim(), false);
            }
        }
    }
}
