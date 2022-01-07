package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ichangemycity.adapter.CommentsAdapter;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.ChangeStatusListData;
import com.ichangemycity.model.CommentsData;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.model.VotedUpData;
import com.ichangemycity.webservice.ParseComplaintData;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.URLDataSwachhManch;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ComplaintDetailNew extends BaseAppCompatActivity {

    private Activity activity;

    public ComplaintData complaintDetailData = new ComplaintData();

    @BindView(R.id.maintoolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_username)
    TextView tv_username;

    @BindView(R.id.hours_ago)
    TextView hours_ago;

    @BindView(R.id.complaint_category)
    TextView complaint_category;

    @BindView(R.id.complaintLocation)
    TextView complaintLocation;

    @BindView(R.id.commentedCount)
    TextView commentedCount;

    @BindView(R.id.share)
    ImageView share;

    @BindView(R.id.votedUpCount)
    TextView votedUpCount;

    @BindView(R.id.ic_directions)
    ImageView ic_directions;

    @BindView(R.id.convertedtoEvent)
    TextView convertedtoEvent;

    @BindView(R.id.complaint_status)
    TextView complaint_status;

    @BindView(R.id.moreInfo)
    TextView moreInfo;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.user_image)
    CircleImageView user_image;

    @BindView(R.id.complaint_image)
    ImageView complaint_image;

    //    @BindView(R.id.not_resolved)
    //    LinearLayout not_resolved;

    //    @BindView(R.id.feedbackBtn)
    //    TextView feedback;

    @BindView(R.id.locationImage)
    ImageView locationImage;

    @BindView(R.id.directionsbtn)
    ImageView directionsbtn;

    @BindView(R.id.profileCategory)
    RelativeLayout profileCategory;

    @BindView(R.id.tv_feed)
    TextView tv_feed;

    @BindView(R.id.mRecyclerviewComments)
    RecyclerView mRecyclerviewComments;

    @BindView(R.id.changeStatus)
    Spinner changeStatus;

    @BindView(R.id.frameSpinner)
    FrameLayout frameSpinner;

    public static Dialog d;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(ComplaintDetailNew.this);
        setContentView(R.layout.complaint_details_new);
        activity = ComplaintDetailNew.this;
        ButterKnife.bind(this);
        d = new Dialog(activity);

        setToolbarAndCustomizeTitle(toolbar, getString(R.string.complaint_details));
        convertedtoEvent.setVisibility(View.GONE);
        profileCategory.setVisibility(View.GONE);
        ic_directions.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        runGetComplaintWebService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(AppConstant.isToRefreshComplaint) {
                AppConstant.isToRefreshComplaint = false;
                runGetComplaintWebService();
            }
            if(d != null) {
                if(d.isShowing()) {
                    d.dismiss();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolbarAndCustomizeTitle(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(v -> activity.finish());
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void runGetComplaintWebService() {
        //        frameLoading.setVisibility(View.GONE);
        findViewById(R.id.parentLayout).setVisibility(View.GONE);
        findViewById(R.id.progressBarRim).setVisibility(View.VISIBLE);
        AppUtils.getInstance().showProgressDialog(activity);
        final String url = URLData.BASE_URL + URLData.COMPLAINT_ID + AppController.selectedComplaintData.getComplaintId() + "&userId=" + ICMyCPreferenceData.getPreferenceItem(ComplaintDetailNew.this, ICMyCPreferenceData.id, "");

        new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {
                //                frameLoading.setVisibility(View.GONE);
                AppUtils.getInstance().hideProgressDialog(activity);
               activity.finish();
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                // AppController.logTrace(activity, url + " ---> " + response);

                new ParseComplaintDetailResponse(response).execute();
            }

        }, false, WebserviceHelper.HEADER_TYPE_NORMAL);
    }

    private class ParseComplaintDetailResponse extends AsyncTask<Void, Void, Void> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        private JSONObject response;

        ParseComplaintDetailResponse(final JSONObject response) {
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... params) {
            parseComplaintDetailResponse(this.response);
            AppController.selectedComplaintData = complaintDetailData;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //            frameLoading.setVisibility(View.GONE);
            findViewById(R.id.parentLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBarRim).setVisibility(View.GONE);
            AppUtils.getInstance().hideProgressDialog(activity);
            loadDataIntoComponents();
            //            initiateChangeStatusEventListener();
        }
    }

    private ComplaintData parseComplaintDetailResponse(final JSONObject json_comp_object) {
        if(json_comp_object != null) {

            try {
                try {
                    // apiResponse =
                    // IChangeMyCity.loadJSONFromAsset(PostedComplaints.this,
                    // "complaints");
                    // commentData.clear();
                    // votedUpData.clear();
                    AppController.commentData.clear();
                    AppController.votedUpData.clear();
                    String complaintString = json_comp_object.optString("complaint");
                    JSONObject json_obj = new JSONObject(complaintString);

                    complaintDetailData.setComplaintId(json_obj.optInt("id") + "");
                    complaintDetailData.setLatitude(json_obj.get("latitude").toString());
                    complaintDetailData.setLongitude(json_obj.get("longitude").toString());

                    complaintDetailData.setComplaint_url(json_obj.optString("complaint_url"));
                    complaintDetailData.setGeneric_id(json_obj.optString("generic_id"));
                    complaintDetailData.setCity_id(json_obj.optInt("city_id") + "");
                    complaintDetailData.setUser_id(json_obj.optInt("user_id") + "");
                    complaintDetailData.setPosted_on(json_obj.optString("posted_on"));
                    complaintDetailData.setAccess_token(json_obj.optString("access_token"));

                    complaintDetailData.setCategory_id(json_obj.optInt("category_id") + "");
                    complaintDetailData.setVote_up_count(json_obj.optInt("vote_up_count") + "");
                    complaintDetailData.setComment_count(json_obj.optInt("comment_count") + "");
                    complaintDetailData.setCategory_name(json_obj.optString("category_name"));
                    if(json_obj.has("complaint_image"))
                        complaintDetailData.setComplaint_image(json_obj.optString("complaint_image"));
                    else
                        complaintDetailData.setComplaint_image("http://icmycsaasqa.ichangemycity.com/android/garbage.jpg");

                    complaintDetailData.setLocation(json_obj.optString("location"));
                    if(json_obj.has("landmark"))
                        complaintDetailData.setLandmark(json_obj.optString("landmark"));
                    else complaintDetailData.setLandmark("Landmark missing in web service");

                    if(json_obj.has("complaint_image_height"))
                        complaintDetailData.setComplaint_image_height(json_obj.optInt("complaint_image_height") + "");
                    else complaintDetailData.setComplaint_image_height(320 + "");

                    complaintDetailData.setParent_id(json_obj.optString("parent_id"));
                    complaintDetailData.setFull_name(json_obj.optString("full_name"));
                    complaintDetailData.setAffected(json_obj.optInt("affected") + "");
                    if(json_obj.has("user_image"))
                        complaintDetailData.setUser_image(json_obj.optString("user_image"));
                    else
                        complaintDetailData.setUser_image("http://icmycsaasqa.ichangemycity.com/android/account.png");

                    complaintDetailData.setComplaint_status_id(json_obj.optString("complaint_status_id"));
                    complaintDetailData.setComplaint_status(json_obj.optString("complaint_status"));
                    complaintDetailData.setRadius("" + json_obj.optInt("radius"));
                    AppController.commentData = new ArrayList<>();
                    if(json_obj.has("comments")) {
                        String comments = json_obj.optString("comments");
                        JSONArray commentsArray = new JSONArray(comments);
                        try {
                            for(int j = 0; j < commentsArray.length(); j++) {
                                JSONObject commentsJsonObject = commentsArray.getJSONObject(j);
                                CommentsData ccData = new CommentsData();
                                ccData.setComment_id(commentsJsonObject.optInt("id") + "");
                                ccData.setComment_user_id(commentsJsonObject.optInt("user_id") + "");
                                ccData.setComment_full_name(commentsJsonObject.optString("full_name"));
                                ccData.setComment_description(commentsJsonObject.optString("description"));
                                ccData.setComment_posted_on(commentsJsonObject.optString("posted_on"));
                                ccData.setComment_complaint_status(commentsJsonObject.optString("complaint_status"));
                                ccData.setComment_complaint_status_id(commentsJsonObject.get("complaint_status_id").toString() + "");
                                ccData.setComment_image_url(commentsJsonObject.optString("comment_image_url"));
                                if(commentsJsonObject.has("user_image_url"))
                                    ccData.setUser_image_url(commentsJsonObject.optString("user_image_url"));
                                try {
                                    ccData.setSpanColorForCoplaintStatus(ParseComplaintData.getSpanColorForStatusTitle(Integer.parseInt(ccData.getComment_complaint_status_id())));
                                } catch(NumberFormatException w) {
                                    ccData.setSpanColorForCoplaintStatus("#00000000");
                                }
                                AppController.commentData.add(ccData);
                            }
                            complaintDetailData.setCommentsData(AppController.commentData);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    AppController.votedUpData = new ArrayList<>();
                    if(json_obj.has("voted_up_users")) {
                        String voted_up_users = json_obj.optString("voted_up_users");
                        JSONArray votedUpJsonArray = new JSONArray(voted_up_users);
                        for(int m = 0; m < votedUpJsonArray.length(); m++) {
                            JSONObject voted_up_usersJsonObject = votedUpJsonArray.getJSONObject(m);
                            VotedUpData mVotedUpData = new VotedUpData();
                            mVotedUpData.setId(voted_up_usersJsonObject.optInt("id") + "");
                            mVotedUpData.setComplaint_count(voted_up_usersJsonObject.optString("complaint_count") + "");
                            mVotedUpData.setFull_name(voted_up_usersJsonObject.optString("full_name"));
                            mVotedUpData.setUser_id(voted_up_usersJsonObject.optInt("user_id") + "");
                            mVotedUpData.setUser_image_url(voted_up_usersJsonObject.optString("user_image_url"));
                            mVotedUpData.setVoted_up_on(voted_up_usersJsonObject.optString("voted_up_on"));
                            AppController.votedUpData.add(mVotedUpData);
                        }
                        complaintDetailData.setVotedUpData(AppController.votedUpData);
                    }
                    if(json_obj.has("feedback_count")) {
                        String feedback_count = json_obj.optString("feedback_count");
                        JSONObject feedback = new JSONObject(feedback_count);
                        complaintDetailData.setFeedback_count(true);
                        complaintDetailData.setNeutral(feedback.optInt("neutral") + "");
                        complaintDetailData.setSatisfaction(feedback.optInt("satisfaction") + "");
                        complaintDetailData.setUn_satisfied(feedback.optInt("un_satisfied") + "");
                    } else {
                        complaintDetailData.setFeedback_count(false);
                        complaintDetailData.setNeutral("0");
                        complaintDetailData.setSatisfaction("0");
                        complaintDetailData.setUn_satisfied("0");
                    }

                    // IChangeMyCity.cData = new ComplaintDetailData();
                    AppController.selectedComplaintData = complaintDetailData;
                    // IChangeMyCity.selectedComplaintData.setAffected(cData
                    // .getAffected());
                    // IChangeMyCity.selectedComplaintData
                    // .setComplaint_status_id(complaintDetailData.getComplaint_status_id());

                } catch(JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return complaintDetailData;

            } catch(Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return complaintDetailData;
    }

    private void initiateChangeStatusEventListener() {

        switch(Integer.parseInt(AppController.selectedComplaintData.getAffected())) {
            case 1:
                if(!(AppController.selectedComplaintData.getComplaint_status_id().equalsIgnoreCase("" + AppController.COMPLAINT_REJECTED))) {
                    complaint_status.setVisibility(View.VISIBLE);
                    complaint_status.setOnClickListener(v -> inflateDialogtoShowChangeStatusMenu());
                } else if((AppController.selectedComplaintData.getComplaint_status_id().equalsIgnoreCase(AppController.COMPLAINT_REJECTED + "") || AppController.selectedComplaintData.getComplaint_status_id().equalsIgnoreCase(AppController.COMPLAINT_OPEN + "") || AppController.selectedComplaintData.getComplaint_status_id().equalsIgnoreCase(AppController.COMPLAINT_REOPEN + "")) && (AppController.selectedComplaintData.getUser_id().trim().equalsIgnoreCase(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.id, "")))) {
                    complaint_status.setVisibility(View.VISIBLE);
                    // if owner of complaint - show
                    // edit/delete
                    complaint_status.setOnClickListener(v -> inflateDialogtoShowChangeStatusMenu());
                } else {
                    complaint_status.setVisibility(View.INVISIBLE);
                }
                break;
            case 0:
                complaint_status.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void loadDataIntoComponents() {
        AppUtils.getInstance().hideProgressDialog(activity);
        profileCategory.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        setCommentsRecyclerView();
        AppController.customizeChangeStatusDropdown(activity, complaintDetailData, changeStatus, frameSpinner);
        //        setOffsetChangeListenerWhileScroll();
        location.setText(complaintDetailData.getLocation());
        tv_username = findViewById(R.id.tv_username);
        moreInfo.setText(complaintDetailData.getLandmark());
        moreInfo.setText(Html.fromHtml("<font><b>" + getString(R.string.more_information) + "</font></b> - " + complaintDetailData.getLandmark()));

        tv_username.setText(complaintDetailData.getFull_name());
        hours_ago.setText(complaintDetailData.getPosted_on());
        ParseComplaintData.setImage(activity, user_image, null, complaintDetailData.getUser_image(), true);
        ParseComplaintData.getInstance().setImage(activity, null, locationImage, URLDataSwachhManch.MAP_THUMBNAIL.replace("<COORDINATES>", complaintDetailData.getLatitude() + "," + complaintDetailData.getLongitude()), false);
        locationImage.setOnClickListener(v -> complaintLocation.performClick());
        complaint_category.setText(complaintDetailData.getCategory_name());
        complaintLocation.setText(complaintDetailData.getLocation());
        if(complaintDetailData.getLocation().trim().equalsIgnoreCase("unknown location, india")) {
            complaintLocation.setText("Click to view complaint location");
        } else {
            complaintLocation.setText(Html.fromHtml(complaintDetailData.getLocation()));
        }
        complaintLocation.setOnClickListener(view -> {
            String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + AppController.getInstance().selectedComplaintData.getLatitude() + "," + AppController.getInstance().selectedComplaintData.getLongitude() + "&z=16 (" + getResources().getString(R.string.app_name) + " complaint location" + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        directionsbtn.setOnClickListener(v -> {
            //        AppUtils.getInstance()
            //            .navigateToLocation(activity,
            //                complaintDetailData.getLatitude() + "," + complaintDetailData.getLongitude());
            complaintLocation.performClick();
        });

        votedUpCount.setText(complaintDetailData.getVote_up_count() + "");
        commentedCount.setText(complaintDetailData.getComment_count() + "");
        complaint_status.setText(complaintDetailData.getComplaint_status());
        tv_feed.setText(complaintDetailData.getGeneric_id());

        ParseComplaintData.getInstance().setImage(activity, null, complaint_image, complaintDetailData.getComplaint_image(), false);
        //        ParseComplaintData.setBgDrawableForComplaintStatus(activity, complaintDetailData, complaint_status);
        commentedCount.setOnClickListener(m -> {
            // TODO Auto-generated method stub
            AppController.selectedComplaintData = complaintDetailData;
            AppController.selectedComplaintData.setToChangeStatus(false);
            Intent toCommentsActivity = new Intent(activity, CommentsActivity.class);
            activity.startActivity(toCommentsActivity);
        });
        votedUpCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.selectedComplaintData = complaintDetailData;
                startActivity(new Intent(activity, VoteupsActivity.class));
            }
        });

        share.setOnClickListener(m -> ParseComplaintData.getInstance().shareComplaintAction(activity, complaintDetailData));
        //        share.setOnClickListener(
        //                m -> ParseComplaintData.shareComplaint(activity, complaintDetailData));

        //        locateComplaint.setOnClickListener(v -> {
        //            String uri = String.format(
        //                    Locale.ENGLISH,
        //                    "geo:0,0?q=" + complaintDetailData.getLatitude() + ","
        //                            + complaintDetailData.getLongitude() + "&z=12 ("
        //                            + complaintDetailData.getLocation() + ")");

        // Uri uri = Uri.parse("geo:" + cData.getLatitude() + ","
        // + cData.getLongitude());
        //            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri
        //                    .parse(uri));
        //            mapIntent.setPackage("com.google.android.apps.maps");
        //            // if (mapIntent.resolveActivity(getPackageManager()) != null) {
        //            startActivity(mapIntent);
        // }
        //        });
        //        navigateComplaint.setOnClickListener(v -> {
        //            // TODO Auto-generated method stub
        //
        //            String uri = String.format(Locale.ENGLISH,
        //                    "google.navigation:q=" + complaintDetailData.getLatitude() + ","
        //                            + complaintDetailData.getLongitude());
        //            // Uri uri = Uri.parse("geo:" + cData.getLatitude() + ","
        //            // + cData.getLongitude());
        //            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri
        //                    .parse(uri));
        //            mapIntent.setComponent(new ComponentName(
        //                    "com.google.android.apps.maps",
        //                    "com.google.android.maps.MapsActivity"));
        //            mapIntent.setPackage("com.google.android.apps.maps");
        //            // if (mapIntent.resolveActivity(getPackageManager()) != null) {
        //            startActivity(mapIntent);
        //            // }
        //
        //        });
        AppController.initiateCTAForShareComment();
        //        complaint_status.setOnClickListener(v -> inflateDialogtoShowChangeStatusMenu());
    }

    private void inflateDialogtoShowChangeStatusMenu() {

        d.setContentView(R.layout.inflate_listview_change_status);
        cStatusListData = new ArrayList<>();
        new SetListData(d).execute();
    }

    ArrayList<ChangeStatusListData> cStatusListData;

    private class SetListData extends AsyncTask<Void, Void, Void> {

        SetListData(Dialog dialog) {
            d = dialog;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // cStatusListData.clear();
            cStatusListData = AppController.customizeListData(ComplaintDetailNew.this, cStatusListData);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //            ChangeStatusListAdapter adapter = new ChangeStatusListAdapter(
            //                    ComplaintDetail.this, cStatusListData);
            //            list.setAdapter(adapter);
            d.show();
        }
    }

    //dummy
    public void setCommentsRecyclerView() {
        CommentsAdapter commentsAdapter = new CommentsAdapter(activity);
        mRecyclerviewComments.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerviewComments.setAdapter(commentsAdapter);
        AppController.getInstance().setEmptyViewForRecyclerView(activity, mRecyclerviewComments);
    }

}
