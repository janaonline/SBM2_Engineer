package com.ichangemycity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ichangemycity.model.ComplaintFilterModel;
import com.ichangemycity.swachhbharatengineer.R;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private Activity activity;
    private ArrayList<ComplaintFilterModel> complaintFilterList = new ArrayList<>();
    private ActionCallBack actionCallBack;

    public void setActionCallBack(ActionCallBack actionCallBack) {
        this.actionCallBack = actionCallBack;
    }

    public ComplaintAdapter(Activity activity, ArrayList<ComplaintFilterModel> complaintFilterList) {
        this.activity = activity;
        this.complaintFilterList = complaintFilterList;
    }

    @NonNull
    @Override
    public ComplaintAdapter.ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComplaintViewHolder(LayoutInflater.from(activity).inflate(R.layout.inflate_complaint_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintAdapter.ComplaintViewHolder holder, int position) {

        ComplaintFilterModel complaintFilter = complaintFilterList.get(position);

        holder.cardView.setTag(complaintFilter);

        holder.mCategorytitle.setText(complaintFilter.getComplaintCount() + "\n" + complaintFilter.getDisplayTitle());

        holder.mCategoryicon.setImageResource(complaintFilter.getResId());

        holder.cardView.setCardBackgroundColor(activity.getResources().getColor(complaintFilter.getComplaintColor()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComplaintFilterModel model = (ComplaintFilterModel) holder.cardView.getTag();
                actionCallBack.selectedComplaintType(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintFilterList.size();
    }

    public class ComplaintViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private RelativeLayout parentRelativeGrid;
        private AppCompatImageView mCategoryicon;
        private AppCompatTextView mCategorytitle;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            parentRelativeGrid = itemView.findViewById(R.id.parentRelativeGrid);
            mCategoryicon = itemView.findViewById(R.id.mCategoryicon);
            mCategorytitle = itemView.findViewById(R.id.mCategorytitle);

        }
    }

    public interface ActionCallBack {

        void selectedComplaintType(ComplaintFilterModel complaintFilterModel);
    }
}
