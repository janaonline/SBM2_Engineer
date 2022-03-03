package com.ichangemycity.swachhbharatengineer;

import static android.R.color.holo_blue_bright;
import static android.R.color.holo_green_light;
import static android.R.color.holo_orange_light;
import static android.R.color.holo_red_light;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ichangemycity.adapter.HomeTabLocalFeedAdapter;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.model.ComplaintFilterModel;
import com.ichangemycity.webservice.ParseComplaintData;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pattabi.raman on 23-10-2017.
 */

public class SearchComplaintsActivity extends BaseAppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    private Activity activity;
    private ImageView mBack;
    private ImageView mLocation_clear;
    private EditText mlocations_search;
    private SwipeRefreshLayout refreshLayout;
    private String TAG = this.getClass().getSimpleName();
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    int currentPage;

    private ComplaintFilterModel complaintFilterModel = new ComplaintFilterModel();
    public static ArrayList<ComplaintData> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(SearchComplaintsActivity.this);
        setContentView(R.layout.search_complaint);
        activity = SearchComplaintsActivity.this;
        mBack = findViewById(R.id.mBack);
        mLocation_clear = findViewById(R.id.location_clear);
        mlocations_search = findViewById(R.id.locationsearch);
        mBack.setOnClickListener(view -> activity.finish());
        onClear();
        refreshLayout = findViewById(R.id.swipe_container);
        initSwipeOptions();
        mRecyclerView = findViewById(R.id.mRecyclerview);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (getIntent().getExtras() != null)
            complaintFilterModel = (ComplaintFilterModel) getIntent().getExtras().getSerializable("complaint");

        mlocations_search.setOnEditorActionListener(
                (v, actionId, event) -> {
                    // Identifier of the action. This will be either the identifier you supplied,
                    // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        onRefresh();
                        return true;
                    }
                    // Return true if you have consumed the action, else false.
                    return false;
                });
        mRecyclerView
                .setAdapter(new HomeTabLocalFeedAdapter(activity));
        AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
        try {
            ((TextView) findViewById(R.id.viewEmpty)).setText("ⓘ Search by Complaint ID \n(Fetches maximum of 1 complaint as result)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSwipeOptions() {
        refreshLayout.setOnRefreshListener(this);
        setAppearance();
        // enableSwipe();
        refreshLayout.setEnabled(true);

    }

    private void setAppearance() {
        refreshLayout.setColorSchemeResources(holo_red_light,
                holo_green_light,
                holo_orange_light,
                holo_blue_bright);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        refreshLayout.setRefreshing(true);
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
        showSwipeProgress();
        runHomeFeedAPIAlone(true);
    }

    private void runHomeFeedAPIAlone(final boolean isToScroll) {
        if (isToScroll) {
            currentPage = 0;
            AppController.hideKeyboard(activity, mlocations_search);
//            ((TextView) (mRecyclerView.getEmptyView().findViewById(R.id.emptyView))).setText(getResources().getString(R.string.loading));
        }
        currentPage += 1;
        if (currentPage == 1) {
            data.clear();
            mRecyclerView.setAdapter(
                    new HomeTabLocalFeedAdapter(activity));
            AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
            try {
                ((TextView) findViewById(R.id.viewEmpty)).setText(activity.getResources().getString(R.string.loading));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String url = URLData.SEARCH_COMPLAINTS.replace("_KEYWORD_", mlocations_search.getText().toString()).replace("_LANG_", ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.selectedLanguage, "en"));

        new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null,
                new OnResponseListener() {
                    @Override
                    public void OnResponseFailure() {
                        hideSwipeProgress();
                        AppUtils.getInstance().hideProgressDialog(activity);
                        AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
                        try {
                            ((TextView) findViewById(R.id.viewEmpty)).setText("Complaint not found");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnResponseSuccess(JSONObject response) {
                        new ParseResponse(response, isToScroll).execute();
                    }
                }, false, WebserviceHelper.HEADER_TYPE_NORMAL);

    }

    boolean isLoadMore;

    private class ParseResponse extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject = new JSONObject();
        boolean isToScroll;

        public ParseResponse(JSONObject response, boolean isToScroll) {
            this.jsonObject = response;
            this.isToScroll = isToScroll;
            if (isToScroll) {
                data.clear();
                if (mRecyclerView.getAdapter() != null)
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                mRecyclerView.setVisibility(View.GONE);
            }
        }


        /**
         * Override this method to perform a computation on a background thread. The specified
         * parameters are the parameters passed to {@link #execute} by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray json_comp_array = jsonObject
                        .optJSONArray("complaints");
                if (json_comp_array != null && json_comp_array.length() == 0) {
                    isLoadMore = false;
                } else {
                    data.addAll(ParseComplaintData.getParsedComplaintData(json_comp_array));
                    isLoadMore = true;
                }
                Glide.get(activity).clearDiskCache();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isLoadMore = false;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideSwipeProgress();

            mRecyclerView.setVisibility(View.VISIBLE);
            AppUtils.getInstance().hideProgressDialog(activity);
            mRecyclerView.setAdapter(
                    new HomeTabLocalFeedAdapter(activity));
            AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerView);
            try {
                ((TextView) findViewById(R.id.viewEmpty)).setText("Complaint not found");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void onClear() {
        data.clear();
        mlocations_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().trim().length() > 0) {
                    mLocation_clear.setVisibility(View.VISIBLE);
                } else {
                    mLocation_clear.setVisibility(View.GONE);
                }
            }
        });
        mLocation_clear.setOnClickListener(v -> {
            if (mlocations_search != null) {
                mlocations_search.setText("");
            }
        });
    }

}
