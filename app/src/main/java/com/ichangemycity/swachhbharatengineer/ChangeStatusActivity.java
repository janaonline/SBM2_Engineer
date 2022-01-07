package com.ichangemycity.swachhbharatengineer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnButtonClick;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.SelectedImageModel;
import com.ichangemycity.permission.GetPermissionResult;
import com.ichangemycity.webservice.AppHelper;
import com.ichangemycity.webservice.MyLocation;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.VolleyMultipartRequest;
import com.ichangemycity.webservice.VolleySingleton;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeStatusActivity extends BaseAppCompatActivity {
    Toolbar toolbar;
    RelativeLayout postComm;
    public static Activity activity;
    ImageView addImage, send;
    private ImageView imageToUpload;
    TextView statusTitleValue;
    TextView messageToShow;
    String mStatus = "";
    ListView list;
    ArrayList<String> listOfReasonToRejectComplaint = new ArrayList<>();
    @BindView(R.id.cvimagePreview)
    CardView cvimagePreview;
    @BindView(R.id.clear)
    ImageView clear;

    private JSONObject geoFencingJsonObject = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(ChangeStatusActivity.this);
        setContentView(R.layout.change_status_activity);

        ButterKnife.bind(this);
        activity = ChangeStatusActivity.this;
        BaseAppCompatActivity.activity = activity;
        try {
            geoFencingJsonObject = new JSONObject(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.validate_location_json_object, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mStatus = AppController.selectedComplaintChangeStatusOptions.getStatusName();
        statusTitleValue = findViewById(R.id.statusTitleValue);
        imageToUpload = findViewById(R.id.imageToUpload);
        send = findViewById(R.id.send);
        postComm = findViewById(R.id.postComm);
        messageToShow = findViewById(R.id.messageToShow);
        list = findViewById(R.id.list);
        listOfReasonToRejectComplaint.clear();
        toolbar = findViewById(R.id.toolbar);
        addImage = findViewById(R.id.addImage);
        addImage.setOnClickListener(v -> showAlertToPickImage());
        setToolbarAndCustomizeTitle(getString(R.string.change_status));
        send.setOnClickListener(v -> {
            JSONArray statusIdsArray = geoFencingJsonObject.optJSONArray("status_ids");
            Type type = new TypeToken<List<Integer>>() {
            }.getType();

            assert statusIdsArray != null;
            ArrayList<Integer> statusIds = new Gson().fromJson(statusIdsArray.toString(), type);
            if (geoFencingJsonObject.optBoolean("validate_location") && statusIds.contains(AppController.selectedComplaintChangeStatusOptions.getStatusID())) {
                checkForLocationPermission();
            } else {
                changeStatusCTA();
            }
        });
        postComm.setVisibility(View.VISIBLE);

        String statusText = ((String) activity.getResources().getString(R.string.you_re_changing_the_status_of_the_complaint_to_new_status));
        statusText = statusText.replace("#NEW_STATUS", mStatus);
        messageToShow.setText(statusText);
        statusTitleValue.setText(getString(R.string.id_) + " " + AppController.selectedComplaintData.getGeneric_id());
        setStatusForTitle(AppController.selectedComplaintChangeStatusOptions.getStatusID());
        clear.setOnClickListener(v -> initializeImageView());
        initializeImageView();
    }

    private void changeStatusCTA() {
        if (((EditText) findViewById(R.id.textComment)).getText().toString().trim().length() > 0) {
            AppUtils.getInstance().showProgressDialog(activity);
            new InitiateChangeStatus().execute();
        } else {
            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, activity.getResources().getString(R.string.write_a_comment));
        }
    }

    private void checkForLocationPermission() {

        ArrayList<String> permissionsRequired = new ArrayList<>();
        permissionsRequired.add(android.Manifest.permission.INTERNET);
        permissionsRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsRequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsRequired.add(android.Manifest.permission.CAMERA);
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
                if (AppUtils.getInstance().setLatitudeLongitude(activity)) {
                    AppUtils.getInstance().showProgressDialog(activity);
                    validateLocationGeofencingToResolveComplaint();
                } else {

                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, "Please wait until we fetch your location");
                }
            }

            @Override
            public void resultPermissionRevoked() {
                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, "We suggest to allow permissions to make app work as expected");
            }
        });
    }

    private void validateLocationGeofencingToResolveComplaint() {
        try {
            MyLocation myLocation = new MyLocation();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @SuppressLint("DefaultLocale")
                @Override
                public void gotLocation(Location location, Timer timer, LocationManager lm) {
                    AppUtils.getInstance().hideProgressDialog(activity);
                    if (location != null) {
                        myLocation.removePendingIntentAndLocationUpdates();
                        AppController.latitude = location.getLatitude();
                        AppController.longitude = location.getLongitude();
                        final double distanceInMetresFromComplaintLocation = AppUtils.getInstance().computeDistanceBetweenLatLngs(new LatLng(AppController.latitude, AppController.longitude), new LatLng(Double.parseDouble(AppController.selectedComplaintData.getLatitude()), Double.parseDouble(AppController.selectedComplaintData.getLongitude())));
                        if (distanceInMetresFromComplaintLocation <= geoFencingJsonObject.optInt("radius_for_validation_in_mtr")) {
                            changeStatusCTA();
                        } else {
                            String distance = String.format("%.2f", distanceInMetresFromComplaintLocation) + " metre(s)";
                            if (distanceInMetresFromComplaintLocation > 1000) {
                                distance = String.format("%.2f", (distanceInMetresFromComplaintLocation / 1000)) + " Kilometre(s)";
                            }
                            AppController.showAlert(activity, "Geo-fencing Alert", "You are " + distance + " away from complaint location :\n" + AppController.selectedComplaintData.getLocation() + ".\n\n Move closer to complaint location and try to change the status of complaint to " + AppController.selectedComplaintChangeStatusOptions.getStatusName(), false, new OnButtonClick() {
                                @Override
                                public void onPositiveButtonClicked(DialogInterface dialogInterface) {

                                }

                                @Override
                                public void onNegativeButtonClicked() {

                                }

                                @Override
                                public void onNegativeButtonClicked(DialogInterface dialogInterface) {

                                }
                            });
                        }
                    }
                }
            };
            myLocation.getLocation(activity, locationResult);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.getInstance().hideProgressDialog(activity);
        }
    }

    private class InitiateChangeStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            postComm.setVisibility(View.GONE);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
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
            /**
             * Image mandatory to resolve complaint
             * */
            if (AppController.selectedComplaintChangeStatusOptions.getStatusID() == AppController.COMPLAINT_RESOLVED && TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {

                AppController.showAlert(activity, "", getResources().getString(R.string.please_upload_an_image_and_then_resolve_the_complaint_to_resolved), false, new OnButtonClick() {
                    @Override
                    public void onPositiveButtonClicked(DialogInterface dialogInterface) {
                        postComm.setVisibility(View.VISIBLE);
                        addImage.performClick();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onNegativeButtonClicked(DialogInterface dialogInterface) {

                    }

                });
            } else {
                if (!TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {
                    uploadImage();
                } else {
                    if (!TextUtils.isEmpty(((EditText) findViewById(R.id.textComment)).getText().toString()))
                        changeStatus(false);
                    else
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, getResources().getString(R.string.write_a_comment));
                }
            }
        }
    }

    private void changeStatus(final boolean hasImage) {
        //need clarify
        AppUtils.getInstance().showProgressDialog(activity);
        final String url = URLData.BASE_URL + URLData.COMPLAINT_STATUS;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("apiKey", URLData.API_KEY);
        params.put("statusId", "" + AppController.selectedComplaintChangeStatusOptions.getStatusID());
        params.put("userId", ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.id, ""));
        params.put("complaintId", AppController.selectedComplaintData.getComplaintId());
        params.put("commentDescription", ((EditText) findViewById(R.id.textComment)).getText().toString());

        if (hasImage)
            params.put("fileId", ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.commentUploadedImageFile, ""));
        String URLParams = "?apiKey=" + URLData.API_KEY + "&statusId=" + AppController.selectedComplaintChangeStatusOptions.getStatusID() + "&userId=" + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.id, "") + "&complaintId=" + AppController.selectedComplaintData.getComplaintId() + "&commentDescription=" + ((EditText) findViewById(R.id.textComment)).getText().toString().replace(" ", "%20");
        if (hasImage)
            URLParams = URLParams + "&fileId=" + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.commentUploadedImageFile, "");

        new WebserviceHelper(activity, WebserviceHelper.METHOD_PUT, url + URLParams, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {
                AppConstant.isToRefreshComplaint = false;
                AppUtils.getInstance().hideProgressDialog(activity);
                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, "Unknown error, please refresh complaints");
                activity.finish();
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {

                //  JSONObject responseJsonObject = null;
                try {
                    AppUtils.getInstance().hideProgressDialog(activity);
                    // responseJsonObject = new JSONObject(response);

                    try {
                        try {
                            AppConstant.isToRefreshComplaint = true;
                        } catch (Exception e) {
                        }

                        int httpCode = response.getInt("httpCode");
                        if (httpCode == 200 || httpCode == 201) {
                            ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.commentUploadedImageFile, "");
                            AppConstant.isToRefreshComplaint = true;
                            activity.finish();
                        }
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, response.optString("message"));
                        //                                Toast.makeText(activity,responseJsonObject.get("message").toString(),Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, true, WebserviceHelper.HEADER_TYPE_NORMAL);


      /*  StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLData.BASE_URL + URLData.COMPLAINT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject responseJsonObject = null;
                        try {
                            AppUtils.getInstance().hideProgressDialog(activity);
                            responseJsonObject = new JSONObject(response);
                            try {
                                int httpCode = responseJsonObject.getInt("httpCode");
                                if (httpCode == 200 || httpCode == 201) {
                                    ICMyCPreferenceData
                                            .setPreference(
                                                    activity,
                                                    ICMyCPreferenceData.commentUploadedImageFile,
                                                    "");
                                    isToRefresh = true;
                                    activity.finish();
                                }
                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, responseJsonObject.optString("message"));
//                                Toast.makeText(activity,
//                                        responseJsonObject.get("message").toString(),
//                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.getInstance().hideProgressDialog(activity);
                        AppController.handleVolleyError(activity, (RelativeLayout) activity.findViewById(R.id.parentLayout), error);


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apiKey", URLData.API_KEY);
                params.put("statusId", "" + AppController.selectedComplaintChangeStatusOptions.getStatusID());
                params.put("userId", ICMyCPreferenceData
                        .getPreferenceItem(activity,
                                ICMyCPreferenceData.id, ""));
                params.put("complaintId",
                        AppController.selectedComplaintData.getComplaintId());
                params.put("commentDescription",
                        ((EditText) findViewById(R.id.textComment)).getText().toString());
                if (hasImage)
                    params.put("fileId", ICMyCPreferenceData
                            .getPreferenceItem(
                                    activity,
                                    ICMyCPreferenceData.commentUploadedImageFile,
                                    ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = "Bearer " + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.token, "");
                final HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);*/
    }

    private void uploadImage() {
        final String uploadImageURL = URLData.BASE_URL_UPLOAD_IMAGE;
        AppUtils.getInstance().showProgressDialog(activity);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadImageURL, response -> {
            String resultResponse = new String(response.data);
            JSONObject mJsonObject;
            try {
                mJsonObject = new JSONObject(resultResponse);

                switch (mJsonObject.optInt("httpCode")) {
                    case 200:
                    case 201:
                        JSONObject fileJsonObject = (JSONObject) mJsonObject.get("file");
                        int fileId = fileJsonObject.optInt("id");
                        ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.commentUploadedImageFile, "" + fileId);
                        // runCommentsWebService();
                        break;

                    default:
                        break;
                }

                AppUtils.getInstance().hideProgressDialog(activity);
                changeStatus(true);
            } catch (JSONException e) {
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
            if (!TextUtils.isEmpty(AppController.mSelectedImageModels.getPathOfSelectedImage())) {
                previewImage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void previewImage() {
        if (AppController.mSelectedImageModels != null && AppController.mSelectedImageModels.getUriOfImage() != null) {
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

    private void setToolbarAndCustomizeTitle(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar.setNavigationOnClickListener(v -> {
            activity.finish();
            AppConstant.isToRefreshComplaint = false;
        });
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void showAlertToPickImage() {
        postComm.setVisibility(View.VISIBLE);
        AppController.selectedPurposeToUploadImage = AppController.PURPOSE_POST_COMMENT;
        startActivity(new Intent(activity, SelectImageDialogActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    private void setStatusForTitle(int complaintStatus) {
        int complaintStatusTextColor = 0;
        list.setVisibility(View.GONE);
        if (complaintStatus > 0) {
            switch (complaintStatus) {
               /* case AppController.COMPLAINT_REOPEN:
                    complaintStatus = R.drawable.complaint_status_red;
                    complaintStatusTextColor = activity.getResources().getColor(R.color.red_reopn_open);
                    break;
                case AppController.COMPLAINT_OPEN:
                    complaintStatus = R.drawable.complaint_status_red;
                    complaintStatusTextColor = activity.getResources().getColor(R.color.red_reopn_open);
                    break;
                case AppController.COMPLAINT_ON_THE_JOB:
                    complaintStatus = R.drawable.complaint_status_on_the_job;
                    complaintStatusTextColor = activity.getResources().getColor(R.color.blue_on_the_job);
                    break;
                case AppController.COMPLAINT_RESOLVED:
                    complaintStatus = R.drawable.complaint_status_resolved;
                    complaintStatusTextColor = activity.getResources().getColor(R.color.green_resolved);
                    break;*/
                case AppController.COMPLAINT_REJECTED:
                    list.setVisibility(View.VISIBLE);
                    messageToShow.setVisibility(View.GONE);
                    findViewById(R.id.messageToShow1).setVisibility(View.GONE);
                    //                    complaintStatus = R.drawable.complaint_status_closed;
                    //                    complaintStatusTextColor = activity.getResources().getColor(R.color.gray_closed);
                    listOfReasonToRejectComplaint.add("Complaint out of the city");
                    listOfReasonToRejectComplaint.add("Image not clear");
                    listOfReasonToRejectComplaint.add("Location not correct");
                    list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listOfReasonToRejectComplaint));
                    list.setOnItemClickListener((adapterView, view, i, l) -> {
                        view.setBackgroundColor(Color.LTGRAY);
                        ((EditText) findViewById(R.id.textComment)).setText(listOfReasonToRejectComplaint.get(i));
                        changeStatus(false);
                    });
                    final String rejectedStatus = getString(R.string.you_re_changing_the_status_of_the_complaint_to_new_status).replace("#New_STATUS", getString(R.string.change_status_rejected)) + "Please select a reason from below to reject this complaint";
                    messageToShow.setText(rejectedStatus);
                    postComm.setVisibility(View.GONE);
                    break;
                default:
                    //                    complaintStatus = R.drawable.complaint_status_closed;
                    //                    complaintStatusTextColor = activity.getResources().getColor(R.color.gray_closed);
                    break;
            }
            //            statusTitleValue.setBackgroundDrawable(getResources().getDrawable(complaintStatus));
            //            statusTitleValue.setTextColor(complaintStatusTextColor);
        }
    }
}
