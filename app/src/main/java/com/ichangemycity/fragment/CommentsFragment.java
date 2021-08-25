package com.ichangemycity.fragment;

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

import com.ichangemycity.adapter.CommentsAdapter;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.swachhbharatengineer.CommentsActivity;
import com.ichangemycity.swachhbharatengineer.ComplaintDetail;
import com.ichangemycity.swachhbharatengineer.R;

public class CommentsFragment extends Fragment {
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
        layout = v.findViewById(R.id.layoutData);
        addRemark();
        return v;
    }


    RecyclerView mRecyclerview;

    private void addRemark() {
        final CommentsAdapter commentsAdapter = new CommentsAdapter(ComplaintDetail.activity );
        mRecyclerview = v.findViewById(R.id.mRecyclerview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ComplaintDetail.activity);
        mRecyclerview.setLayoutManager(manager);
        mRecyclerview.addItemDecoration(new DividerItemDecoration(ComplaintDetail.activity, LinearLayoutManager.VERTICAL));
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mRecyclerview.setAdapter(commentsAdapter);
        AppController.getInstance().setEmptyViewForRecyclerViewFragments(ComplaintDetail.activity,mRecyclerview,
            v.findViewById(R.id.viewEmpty));

        if (Integer.parseInt(AppController.selectedComplaintData.getComment_count()) > 5) {
            try {
                v.findViewById(R.id.viewEmpty).setVisibility(View.GONE);
            } catch (Exception e) {
            }
            LayoutInflater inflater = (LayoutInflater) ComplaintDetail.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View loadMore = inflater.inflate(R.layout.inflate_loadmore, null);
            loadMore.findViewById(R.id.loadmore).setOnClickListener(
                v -> startActivity(new Intent(ComplaintDetail.activity, CommentsActivity.class)));
            layout.addView(loadMore);

        }


    }

}
