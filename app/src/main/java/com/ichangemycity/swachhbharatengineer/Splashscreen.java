package com.ichangemycity.swachhbharatengineer;

import static com.ichangemycity.appdata.AppController.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.InternetConnectionCallback;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.LanguageData;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pattabi.raman on 23-09-2017.
 */

public class Splashscreen extends BaseAppCompatActivity {

    //    List<String> permissionsRequired = new ArrayList<>();
    public static Activity activity;
    private static TextView tv_check_connection;
    CheckConnectivity mNetworkReceiver;
    @BindView(R.id.videoView)
    VideoView videoView;

    public Activity getActivity() {
        if (activity == null) {
            activity = Splashscreen.this;
        }
        return activity;
    }

    InternetConnectionCallback internetConnectionCallback = new InternetConnectionCallback() {
        @Override
        public void onInternetConnected(boolean isConnected) {

            proceedAfterPermissionGranted();
        }

        @Override
        public void onInternetDisconnected(boolean isConnected) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ButterKnife.bind(this);
        activity = Splashscreen.this;
        tv_check_connection = findViewById(R.id.tv_check_connection);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mNetworkReceiver = new CheckConnectivity(internetConnectionCallback);
        activity = Splashscreen.this;
        mNetworkReceiver = new CheckConnectivity(internetConnectionCallback);
        registerNetworkBroadcastForNougat();

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_screen);
        videoView.setVideoURI(video);
        videoView.start();

        /*comment below lines - dummy*/
//        AppController.selectedComplaintData.setComplaintId("26216907");

    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private void proceedAfterPermissionGranted() {
        new RegisterBackground().execute();
    }

    String msg = "";

    @SuppressLint("StaticFieldLeak")
    public class RegisterBackground extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg0) {

            try {
                Thread.sleep(2000);
                performGCMRegistration();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return msg;

        }

        private void performGCMRegistration() {
            try {

                FirebaseApp.initializeApp(activity);
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    AppController.traceLog(TAG, "getInstanceId failed :" + task.getException());
                                    return;
                                } else {
                                    String refreshedToken = task.getResult();
                                    if (!TextUtils.isEmpty(refreshedToken)) {
                                        AppConstant.deviceToken = refreshedToken;
                                        AppController.traceLog("deviceToken", refreshedToken);
                                    } else {
                                        AppController.traceLog(TAG, "device token is null or empty");
                                    }
                                }
                            }
                        });
            } catch (Exception ex) {
                msg = "Error :" + ex.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String msg) {

            final String url = URLData.GET_LANGUAGES;
            new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
                @Override
                public void OnResponseFailure() {
                    AppUtils.getInstance().hideProgressDialog(activity);
                }

                @Override
                public void OnResponseSuccess(JSONObject response) {
                    new GetParsedData(activity, response.optJSONArray("languages")).execute();
                }
            }, false, WebserviceHelper.HEADER_TYPE_NORMAL);

            final String urlToValidateLocationForResolution = URLData.URL_VALIDATE_LOCATION;
            new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, urlToValidateLocationForResolution, null, new OnResponseListener() {
                @Override
                public void OnResponseFailure() {
                    AppUtils.getInstance().hideProgressDialog(activity);
                }

                @Override
                public void OnResponseSuccess(JSONObject response) {
                    try {
                        ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.validate_location_json_object, Objects.requireNonNull(response.optJSONObject("geo-fencing")).toString());
                    } catch (Exception e) {
                        ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.validate_location_json_object, new JSONObject().toString());
                    }
                }
            }, false, WebserviceHelper.HEADER_TYPE_NORMAL);
        }

    }

    public class GetParsedData extends AsyncTask<Void, Void, Void> {

        JSONArray response;

        public GetParsedData(Activity activity, JSONArray response) {
            this.response = response;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            // TODO Auto-generated method stub
            try {
                if (AppController.languageArrayList.size() <= 0) {
                    AppController.languageArrayList.clear();

                    for (int i = 0; i < this.response.length(); i++) {
                        JSONObject mJsonObject = this.response.optJSONObject(i);
                        LanguageData lData = new LanguageData();
                        lData.setLanguage_code(mJsonObject.optString(AppController.language_code));
                        lData.setLanguage_label(mJsonObject.optString(AppController.language_label));
                        if (!AppController.languageArrayList.contains(lData)) {
                            AppController.languageArrayList.add(lData);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //            AppUtils.getInstance().hideProgressDialog(activity);
            setConditionToNavigateScreens();

        }
    }

    private void setConditionToNavigateScreens() {
        DoSetMandatoryData mDoSetMandatoryData = new DoSetMandatoryData();
        mDoSetMandatoryData.execute();

    }

    class DoSetMandatoryData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            AppController.setMACAddressInPreference(activity);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //      if (Integer.parseInt(ICMyCPreferenceData.getPreferenceItem(
            //          Splashscreen.this, ICMyCPreferenceData.activated, "0")) == 0) {
            if (!(ICMyCPreferenceData.getPreferenceItem(Splashscreen.this, ICMyCPreferenceData.activated, "0").equalsIgnoreCase("1"))) {
                startActivity(new Intent(Splashscreen.this, UserMobileNumber.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                startActivity(new Intent(Splashscreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
            Splashscreen.this.finish();

        }

    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public void dialog(boolean value) {
        if (value) {
            tv_check_connection.setVisibility(View.GONE);
        } else {
            tv_check_connection.setVisibility(View.VISIBLE);
            tv_check_connection.setText(R.string.could_not_connect_to_internet);
            tv_check_connection.setBackgroundColor(Color.RED);
            tv_check_connection.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String data = intent.getDataString();
        if (data != null) {
            ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.isDeeplinked, "1");
            if (Intent.ACTION_VIEW.equals(action) && data.contains("/complaints/")) {
                //              Complaint deeplink
                ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.isDeeplinked, "1");
                AppController.selectedComplaintData.setComplaintId(data.substring(data.lastIndexOf("/") + 1));
            }
        } else {
            // do nothing
        }

    }
}
