package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.ichangemycity.adapter.CommentsAdapter;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.CommentsData;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.model.SelectedImageModel;
import com.ichangemycity.webservice.AppHelper;
import com.ichangemycity.webservice.ParseComplaintData;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.VolleyMultipartRequest;
import com.ichangemycity.webservice.VolleySingleton;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ichangemycity.webservice.URLData.BASE_URL_UPLOAD_IMAGE;

public class CommentsActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    public static Activity activity;
    private RecyclerView recycler_view;
    private static String url;
    private int currentPage = 0;
    ComplaintData data = new ComplaintData();
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    RecyclerView.LayoutManager layoutManager;
    boolean isLoadMore = true;
    ImageView addImage, send;
    private ImageView imageToUpload;
    RelativeLayout postComm;

    @BindView(R.id.cvimagePreview)
    CardView cvimagePreview;
    @BindView(R.id.clear)
    ImageView clear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(CommentsActivity.this);
        setContentView(R.layout.comments_activity);
        ButterKnife.bind(this);
        activity = CommentsActivity.this;
        BaseAppCompatActivity.activity = activity;
        imageToUpload = findViewById(R.id.imageToUpload);
        postComm = findViewById(R.id.postComm);
        send = findViewById(R.id.send);
        data = AppController.selectedComplaintData;
        url = URLData.BASE_URL + URLData.GET_POSTED_COMMENT + data.getComplaintId() + URLData.GET_POSTED_COMMENT_SORT;
        toolbar = findViewById(R.id.toolbar);
        addImage = findViewById(R.id.addImage);
        addImage.setOnClickListener(v -> showAlertToPickImage());
        recycler_view = findViewById(R.id.mRecyclerview);
        setToolbarAndCustomizeTitle(getResources().getString(R.string.comments));
        layoutManager = new LinearLayoutManager(activity);
        recycler_view.setLayoutManager(layoutManager);

        send.setOnClickListener(v -> {
            if(((EditText) findViewById(R.id.textComment)).getText().toString().trim().length() > 0) {
                AppUtils.getInstance().showProgressDialog(activity);
                new InitiatePostComment().execute();
            } else {
                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, activity.getResources().getString(R.string.write_a_comment));

                //                    Toast.makeText(activity, getResources().getString(R.string.write_a_comment), Toast
                //                            .LENGTH_SHORT).show();
            }

        });
        initializeImageView();
        findViewById(R.id.postComm).setVisibility(View.VISIBLE);
        runCommentFeedWebService(true);
        clear.setOnClickListener(v -> initializeImageView());
    }

    private void previewImage() {
        if(AppController.mSelectedImageModels != null && AppController.mSelectedImageModels.getUriOfImage() != null) {
            cvimagePreview.setVisibility(View.VISIBLE);
            imageToUpload.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageToUpload.setImageURI(AppController.mSelectedImageModels.getUriOfImage());
            clear.setVisibility(View.VISIBLE);
        } else {
            initializeImageView();
        }
    }
    private void initializeImageView() {
        AppController.mSelectedImageModels = new SelectedImageModel();
        ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.commentUploadedImageFile, "");
        clear.setVisibility(View.GONE);
        cvimagePreview.setVisibility(View.GONE);
    }

    private class InitiatePostComment extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.postComm).setVisibility(View.GONE);

        }

        /**
         * Override this method to perform a computation on a background thread. The specified
         * parameters are the parameters passed to {@link #execute} by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates on the UI thread.
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AppUtils.getInstance().hideProgressDialog(activity);
            if(!TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {
                uploadImage();
            }/* else if (TextUtils.isEmpty(ICMyCPreferenceData.getPreferenceItem(activity,
                    ICMyCPreferenceData.commentUploadedImageFile, null))) {
                postComment(true);
            }*/ else {
                if(!TextUtils.isEmpty(((EditText) findViewById(R.id.textComment)).getText().toString())) {
                    postComment(false);
                } else
                //                    Toast.makeText(activity, getResources().getString(R.string.write_a_comment), Toast.LENGTH_SHORT).show();
                {
                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, activity.getResources().getString(R.string.write_a_comment));
                }

            }
        }
    }

    private void postComment(final boolean hasImage) {
        final String url = URLData.BASE_URL + URLData.COMMENT;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("complaintId", AppController.selectedComplaintData.getComplaintId());
        params.put("apiKey", URLData.API_KEY);
        params.put("commentTypeId", Integer.toString(1));
        params.put("commentDescription", ((EditText) findViewById(R.id.textComment)).getText().toString());

        if(hasImage) {
            params.put("fileId", ICMyCPreferenceData.getPreferenceItem(CommentsActivity.this, ICMyCPreferenceData.commentUploadedImageFile, ""));
        }

        String urlParams = "?complaintId=" + AppController.selectedComplaintData.getComplaintId() + "&apiKey=" + URLData.API_KEY + "&commentTypeId=" + Integer.toString(1) + "&commentDescription=" + ((EditText) findViewById(R.id.textComment)).getText().toString().trim().replace(" ", "%20");
        if(hasImage) {
            urlParams += "&fileId=" + ICMyCPreferenceData.getPreferenceItem(CommentsActivity.this, ICMyCPreferenceData.commentUploadedImageFile, "");
        }

        new WebserviceHelper(activity, WebserviceHelper.METHOD_POST, url + urlParams, params, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {

                //                        Toast.makeText(UserMobileNumber.this, error.toString(), Toast.LENGTH_LONG).show();
                AppUtils.getInstance().hideProgressDialog(activity);
                postComm.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnResponseSuccess(JSONObject responseJsonObject) {

                try {
                    AppUtils.getInstance().hideProgressDialog(activity);
                    ((EditText) CommentsActivity.this.findViewById(R.id.textComment)).setText("");
                    if(hasImage) {
                        //                                AppController.trackEvent(GAData.POST_A_COMMENT + GAData.WITH_IMAGE, GAData.DONE, GAData.DONE);
                        initializeImageView();
                    } else {
                        //                                AppController.trackEvent(GAData.POST_A_COMMENT, GAData.DONE, GAData.DONE);
                    }

                    //                            Toast.makeText(activity, responseJsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, responseJsonObject.optString("message"));

                    parseData(responseJsonObject);

                    if(recycler_view != null) {
                        if(recycler_view.getAdapter() != null) {
                            recycler_view.getAdapter().notifyDataSetChanged();
                        }
                    }
                    findViewById(R.id.postComm).setVisibility(View.VISIBLE);
                    try {
                        ComplaintDetailNew.isToRefresh = true;
                    }catch(Exception e){}
                } catch(Exception e) {
                    e.printStackTrace();
                }
                runCommentFeedWebService(true);

            }
        }, true, WebserviceHelper.HEADER_TYPE_NORMAL);
    }



    private void uploadImage() {
        final String uploadImageURL = BASE_URL_UPLOAD_IMAGE;
        AppUtils.getInstance().showProgressDialog(activity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadImageURL, response -> {
            String resultResponse = new String(response.data);
            try {
                AppUtils.getInstance().hideProgressDialog(activity);
                JSONObject result = new JSONObject(resultResponse);
                try {
                    switch(result.optInt("httpCode")) {
                        case 200:
                        case 201:
                            JSONObject fileJsonObject = (JSONObject) result.get("file");
                            int fileId = fileJsonObject.optInt("id");
                            ICMyCPreferenceData.setPreference(CommentsActivity.this, ICMyCPreferenceData.commentUploadedImageFile, "" + fileId);
                            postComment(true);
                            // runCommentsWebService();
                            break;

                        default:
                            break;
                    }
                } catch(JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                AppUtils.getInstance().hideProgressDialog(activity);

            } catch(JSONException e) {
                e.printStackTrace();
                AppUtils.getInstance().hideProgressDialog(activity);
            }
        }, error -> {
            AppUtils.getInstance().hideProgressDialog(activity);
            AppController.handleVolleyError(activity, error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apiKey", URLData.API_KEY);
                params.put("deviceWidth", 1024 + "");
                params.put("deviceHeight", 768 + "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = "Bearer " + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.token, "");
                final HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                params.put("file", new DataPart("image" + new Random().nextInt() + ".jpg", AppHelper.getFileDataFromDrawable(activity, AppController.mSelectedImageModels), "image/jpeg"));

                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(AppController.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(!TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {
                previewImage();
            }
            AppUtils.getInstance().hideProgressDialog(activity);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*private void showSelectedImage() {
        if(!TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {
            imageToUpload.setVisibility(View.VISIBLE);
            Glide.with(activity).load((AppController.mSelectedImageModels.getUriOfImage())).into(imageToUpload);
        } else {
            imageToUpload.setVisibility(View.GONE);
        }
    }*/

    private void setToolbarAndCustomizeTitle(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar.setNavigationOnClickListener(v -> activity.finish());
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        runCommentFeedWebService(true);

    }

    private void runCommentFeedWebService(final boolean isToScroll) {
        AppUtils.getInstance().hideProgressDialog(activity);
        if(isToScroll) {
            currentPage = 0;
        }
        ++currentPage;
        if(currentPage == 1) {
            AppController.commentData.clear();
            recycler_view.setAdapter(new CommentsAdapter(activity));
        }
        String getCommentRequestUrl = url + currentPage;
        //        if (AppController.getInstance().getRequestQueue().getCache().get(url) != null) {
        //            try {
        //                JSONObject response = new JSONObject(String.valueOf(AppController.getInstance().getRequestQueue().getCache().get(url).data));
        //                new ParseResponse(response, isToScroll).execute();
        //            } catch (JSONException e) {
        //                e.printStackTrace();
        //            }
        //        } else {
        //        AppController.logTrace(activity, url + currentPage);b

        new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, getCommentRequestUrl, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {
                if(isToScroll) {
                    AppUtils.getInstance().hideProgressDialog(activity);
                }
                AppController.getInstance().setEmptyViewForRecyclerView(activity, recycler_view);

            }

            @Override
            public void OnResponseSuccess(JSONObject response) {

                if(isToScroll) {
                    AppController.commentData.clear();
                    AppUtils.getInstance().hideProgressDialog(activity);
                }
                AppUtils.getInstance().hideProgressDialog(activity);
                new ParseResponse(response, isToScroll).execute();

            }
        }, isToScroll, WebserviceHelper.HEADER_TYPE_NORMAL);
    }

    private class ParseResponse extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;
        boolean isToScroll;

        ParseResponse(final JSONObject jsonObject, final boolean isToScroll) {
            this.jsonObject = jsonObject;
            this.isToScroll = isToScroll;
        }

        /**
         * Override this method to perform a computation on a background thread. The specified
         * parameters are the parameters passed to {@link #execute} by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates on the UI thread.
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            //            if (this.jsonObject.optJSONObject("paginator").optBoolean("hasMore"))
            //                isLoadMore = true;
            parseData(this.jsonObject);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            addComments();
            AppUtils.getInstance().hideProgressDialog(activity);
            if(isToScroll) {
                AppUtils.getInstance().hideProgressDialog(activity);
                recycler_view.setAdapter(new CommentsAdapter(activity));
                AppController.getInstance().setEmptyViewForRecyclerView(activity, recycler_view);
                recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        if(visibleItemCount > 0 && recycler_view != null) {
                            boolean firstItemVisible = pastVisiblesItems == 0;
                            // check if the top of the first item is
                            // visible
                            boolean topOfFirstItemVisible = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() == 0;
                            enable = firstItemVisible && topOfFirstItemVisible;
                        }

                        if(isLoadMore) {
                            if((visibleItemCount + pastVisiblesItems) >= (totalItemCount - 5)) {
                                isLoadMore = false;
                                // loading = false;
                                try {
                                    runCommentFeedWebService(false);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                });
            } else {
                recycler_view.getAdapter().notifyDataSetChanged();
            }
        }

    }

    private void parseData(final JSONObject json_comp_object) {
        try {

            String complaintString = json_comp_object.optString("comments");
            JSONArray jsonArray = new JSONArray(complaintString);
            if(jsonArray.length() == 0) {
                isLoadMore = false;
            } else {
                isLoadMore = true;
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject commentsJsonObject = jsonArray.getJSONObject(i);

                    try {
                        CommentsData ccData = new CommentsData();
                        ccData.setComment_id(commentsJsonObject.optInt("id") + "");
                        ccData.setComment_user_id(commentsJsonObject.optInt("user_id") + "");
                        ccData.setComment_full_name(commentsJsonObject.optString("full_name"));
                        ccData.setComment_description(commentsJsonObject.optString("description"));
                        ccData.setComment_posted_on(commentsJsonObject.optString("posted_on"));
                        ccData.setComment_complaint_status(commentsJsonObject.optString("complaint_status"));
                        ccData.setComment_complaint_status_id(commentsJsonObject.get("complaint_status_id").toString() + "");
                        ccData.setComment_image_url(commentsJsonObject.optString("comment_image_url"));
                        if(commentsJsonObject.has("user_image_url")) {
                            ccData.setUser_image_url(commentsJsonObject.optString("user_image_url"));
                        }
                        try {
                            ccData.setSpanColorForCoplaintStatus(ParseComplaintData.getSpanColorForStatusTitle(Integer.parseInt(ccData.getComment_complaint_status_id())));
                        } catch(NumberFormatException w) {
                            ccData.setSpanColorForCoplaintStatus("#00000000");
                        }
                        AppController.commentData.add(ccData);

                    } catch(Exception e) {
                        e.printStackTrace();
                        isLoadMore = false;

                    }
                }
                AppController.selectedComplaintData.setCommentsData(AppController.commentData);
                AppController.selectedComplaintData.setComment_count(AppController.commentData.size() + "");
            }

        } catch(JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void addComments() {
        final CommentsAdapter commentsAdapter = new CommentsAdapter(activity);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        recycler_view.setLayoutManager(manager);
        recycler_view.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.HORIZONTAL));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(commentsAdapter);

    }

    private void showAlertToPickImage() {
        postComm.setVisibility(View.VISIBLE);
        AppController.selectedPurposeToUploadImage = AppController.PURPOSE_POST_COMMENT;
        startActivity(new Intent(activity, SelectImageDialogActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

}
