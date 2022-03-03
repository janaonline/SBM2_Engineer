package com.ichangemycity.adapter;

/**
 * Created by pattabi.raman on 24-07-2017.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ichangemycity.model.VotedUpData;
import com.ichangemycity.swachhbharatengineer.R;
import com.ichangemycity.webservice.ParseComplaintData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteupsAdapter extends RecyclerView.Adapter<VoteupsAdapter.AddRemarkViewHolder> {

    private ArrayList<VotedUpData> arrayList;
    private Activity activity;
    private boolean isToShowLoadMore;

    public VoteupsAdapter(Activity mAct, ArrayList<VotedUpData> arrayList, boolean isToShowLoadMore) {
        activity = mAct;
        this.arrayList = arrayList;
        this.isToShowLoadMore = isToShowLoadMore;
    }

    @Override
    public AddRemarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_voteups_list_item, parent, false);
        return new AddRemarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddRemarkViewHolder holder, int position) {
        VotedUpData voteupData = arrayList.get(position);

        holder.mDescription.setText(voteupData.getComplaint_count() + " posted");
        holder.mName.setText(voteupData.getFull_name());
        holder.postedOn.setText(voteupData.getVoted_up_on());
        ParseComplaintData.getInstance().setImage(activity, holder.mUserImage, null,
                voteupData.getUser_image_url(), true);
    }

    @Override
    public int getItemCount() {
        if (isToShowLoadMore) {
            return arrayList.size() > 5 ? 5 : arrayList.size();
        }
        return arrayList.size();
    }

    class AddRemarkViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userName)
        TextView mName;

        @BindView(R.id.description_count)
        TextView mDescription;

        @BindView(R.id.postedOn)
        TextView postedOn;

        @BindView(R.id.userImage)
        de.hdodenhof.circleimageview.CircleImageView mUserImage;

        public AddRemarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
