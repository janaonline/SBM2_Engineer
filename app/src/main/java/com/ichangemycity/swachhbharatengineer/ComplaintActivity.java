package com.ichangemycity.swachhbharatengineer;

import static android.R.color.holo_blue_bright;
import static android.R.color.holo_green_light;
import static android.R.color.holo_orange_light;
import static android.R.color.holo_red_light;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ichangemycity.adapter.HomeTabLocalFeedAdapter;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.model.ComplaintFilterModel;
import com.ichangemycity.swachhbharatengineer.databinding.ActivityComplaintBinding;
import com.ichangemycity.webservice.ParseComplaintData;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ComplaintActivity extends BaseAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityComplaintBinding binding;
    public static Activity activity;
    private Bundle bundle;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private ComplaintFilterModel complaintFilterModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complaint);
        activity = ComplaintActivity.this;
        bundle = getIntent().getExtras();
        if (bundle != null)
            complaintFilterModel = (ComplaintFilterModel) bundle.getSerializable("complaint");
        setToolbarAndCustomizeTitle(complaintFilterModel.getDisplayTitle());

        mRecyclerView = findViewById(R.id.list);
        refreshLayout = findViewById(R.id.swipe_container);
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(new HomeTabLocalFeedAdapter(activity));
        AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
        try {
            ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.loading));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppUtils.getInstance().showProgressDialog(activity);
        runHomeFeedWebService(complaintFilterModel.getComplaintType(), true);
        initSwipeOptions();

    }


    private void setToolbarAndCustomizeTitle(String title) {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        binding.toolbar.setNavigationOnClickListener(v -> activity.finish());
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(title);
        binding.toolbar.setTitleTextColor(Color.WHITE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (AppConstant.isToRefreshComplaint) {
                runHomeFeedWebService(complaintFilterModel.getComplaintType(), true);
            }
        } catch (Exception e) {

        }
    }


    private int currentPage;
    public static ArrayList<ComplaintData> data = new ArrayList<>();

    private void runHomeFeedWebService(final String ComplaintType, final boolean isToScroll) {
        {
            AppUtils.getInstance().hideProgressDialog(activity);
            if (isToScroll) {
                currentPage = 0;
                //            ((TextView) (mRecyclerView.getEmptyView().findViewById(R.id.emptyView))).setText(activity.getResources().getString(R.string.loading));
                //            mRecyclerView.getProgressView().setVisibility(View.VISIBLE);
                //            AppUtils.getInstance().showProgressDialog(activity, getString(R.string.loading));
            }
            currentPage += 1;
            if (currentPage == 1) {
                data.clear();
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                    try {
                        ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.loading));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            refreshLayout.setEnabled(false);
            final String url = URLData.BASE_URL + ComplaintType + URLData.PAGE + currentPage + "&per_page=30";
            new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
                @Override
                public void OnResponseFailure() {
                    refreshLayout.setEnabled(true);
                    AppUtils.getInstance().hideProgressDialog(activity);
                    hideSwipeProgress();
                    setProgressVisibility(View.GONE);
                    AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);

                }

                @Override
                public void OnResponseSuccess(final JSONObject response) {
                    AppUtils.getInstance().hideProgressDialog(activity);
                    refreshLayout.setEnabled(true);
                    AppController.traceLog("home", url + " ---> " + response);
                    if (isToScroll) {
                        data.clear();
                        if (mRecyclerView.getAdapter() != null) {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                            try {
                                ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.loading));

                            } catch (Exception e) {
                            }
                        }
                    } else {
                        if (mRecyclerView.getAdapter() != null) {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                            try {
                                findViewById(R.id.viewEmpty).setVisibility(View.GONE);
                            } catch (Exception e) {
                            }
                        }
                    }
                    new ParseJSONResponse(response, isToScroll).execute();
                }
            }, isToScroll, WebserviceHelper.HEADER_TYPE_NORMAL);


        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ParseJSONResponse extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject = new JSONObject();
        boolean isToScroll;

        public ParseJSONResponse(JSONObject response, final boolean isToScroll) {
            this.jsonObject = response;
            this.isToScroll = isToScroll;
            setProgressVisibility(View.VISIBLE);
            if (mRecyclerView.getAdapter() != null)
                mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {
            GetParsedJsonFromResponse(this.jsonObject);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mRecyclerView.setVisibility(View.VISIBLE);
            hideSwipeProgress();
            if (data != null && data.size() <= 0 && mRecyclerView.getAdapter() != null) {
                AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                try {
                    findViewById(R.id.viewEmpty).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.no_complaints));
                } catch (Exception e) {
                }
            }
            if (isToScroll) {
                AppUtils.getInstance().hideProgressDialog(activity);

                if(mRecyclerView.getAdapter()!=null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                    try {
                        ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.no_complaints));
                    } catch (Exception e) {
                    }
                }
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    /**
                     * Callback method to be invoked when the RecyclerView has been scrolled. This will be
                     * called after the scroll has completed.
                     * <p>
                     * This callback will also be called if visible item range changes after a layout
                     * calculation. In that case, dx and dy will be 0.
                     *
                     * @param recyclerView The RecyclerView which scrolled.
                     * @param dx           The amount of horizontal scroll.
                     * @param dy           The amount of vertical scroll.
                     */
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        boolean enable = false;
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                        if (visibleItemCount > 0 && mRecyclerView != null) {
                            boolean firstItemVisible = pastVisiblesItems == 0;
                            // check if the top of the first item is
                            // visible
                            boolean topOfFirstItemVisible = ((LinearLayoutManager) mLayoutManager).findFirstCompletelyVisibleItemPosition() == 0;
                            enable = firstItemVisible && topOfFirstItemVisible;
                        }
                        refreshLayout.setEnabled(enable);
                        if (isLoadMore) {
                            if ((visibleItemCount + pastVisiblesItems) >= (totalItemCount - 5)) {
                                setProgressVisibility(View.VISIBLE);
                                isLoadMore = false;
                                try {
                                    runHomeFeedWebService(complaintFilterModel.getComplaintType(), false);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                setProgressVisibility(View.GONE);
                            }

                        }

                    }
                });
            } else {
                if (mRecyclerView.getAdapter() != null)
                    mRecyclerView.getAdapter().notifyDataSetChanged();
            }
            setProgressVisibility(View.GONE);
            hideSwipeProgress();
            AppController.traceLog("TotalComplaintCountListed", "-------->" + mRecyclerView.getAdapter().getItemCount() + "");
        }

    }

    boolean isLoadMore;

    private ArrayList<ComplaintData> GetParsedJsonFromResponse(JSONObject json_comp_object) {
        try {
            JSONArray json_comp_array = json_comp_object.getJSONArray("complaints");
            if (json_comp_array.length() == 0) {
                isLoadMore = false;
            } else {

                data.addAll(ParseComplaintData.getParsedComplaintData(json_comp_array));
                isLoadMore = true;
            }
            return data;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isLoadMore = false;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (mRecyclerView.getAdapter() != null)
                mRecyclerView.getAdapter().notifyDataSetChanged();
            setProgressVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    private void initSwipeOptions() {
        refreshLayout.setOnRefreshListener(this);
        setAppearance();
        // enableSwipe();
        refreshLayout.setEnabled(true);

    }

    private void setAppearance() {
        refreshLayout.setColorSchemeResources(holo_red_light, holo_green_light, holo_orange_light, holo_blue_bright);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        refreshLayout.setRefreshing(false);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        mRecyclerView.setVisibility(View.GONE);
        runHomeFeedWebService(complaintFilterModel.getComplaintType(), true);
    }

    private void setProgressVisibility(final int VISIBILITY) {
        binding.progressBar.setVisibility(VISIBILITY);

    }

}