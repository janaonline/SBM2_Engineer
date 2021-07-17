package com.ichangemycity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ichangemycity.adapter.VoteupsAdapter;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.swachhbharatengineer.ComplaintDetail;
import com.ichangemycity.swachhbharatengineer.R;
import com.ichangemycity.swachhbharatengineer.VoteupsActivity;

/**
 * Created by pattabi.raman on 20-10-2017.
 */

public class VoteupFragment extends Fragment {

    public static Activity activity;
    View v;
    LinearLayout layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(ComplaintDetail.activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.comments_fragment, null);
        activity = getActivity();
        layout = v.findViewById(R.id.layoutData);
        addRemark();
        return v;
    }

    RecyclerView mAdd_remarkrecyclerview;

    private void addRemark() {
        AppController.votedUpData = AppController.selectedComplaintData.getVotedUpData();
        final VoteupsAdapter commentsAdapter = new VoteupsAdapter(activity, AppController.votedUpData, true);
        mAdd_remarkrecyclerview = v.findViewById(R.id.mRecyclerview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        mAdd_remarkrecyclerview.setLayoutManager(manager);
        mAdd_remarkrecyclerview.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        mAdd_remarkrecyclerview.setItemAnimator(new DefaultItemAnimator());
        mAdd_remarkrecyclerview.setAdapter(commentsAdapter);
        AppController.getInstance().setEmptyViewForRecyclerViewFragments(ComplaintDetail.activity,mAdd_remarkrecyclerview,
            v.findViewById(R.id.viewEmpty));

        if (AppController.selectedComplaintData.getVotedUpData().size() > 5) {
            try {
                v.findViewById(R.id.viewEmpty).setVisibility(View.GONE);
            } catch (Exception e) {
            }
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View loadMore = inflater.inflate(R.layout.inflate_loadmore, null);
            loadMore.findViewById(R.id.loadmore).setOnClickListener(
                v -> startActivity(new Intent(activity, VoteupsActivity.class)));
            layout.addView(loadMore);


        }


    }
}
