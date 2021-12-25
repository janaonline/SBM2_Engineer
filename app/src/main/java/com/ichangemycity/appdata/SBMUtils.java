package com.ichangemycity.appdata;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.swachhbharatengineer.R;
import com.ichangemycity.webservice.sbm2.URLDataConstants;
import com.ichangemycity.webservice.sbm2.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

public class SBMUtils {
    /*Constants*/
    private static SBMUtils mInstance;
    public View view; // to show/hide progress dialog view

    /*get instance*/
    public static SBMUtils getInstance() {
        return mInstance == null ? mInstance = new SBMUtils() : mInstance;
    }

    public void showErrorResponse(final Activity activity, final int MESSAGE_TYPE, final JSONObject errorObject) {
        StringBuilder message = new StringBuilder();
        String title = "";
        message = new StringBuilder(errorObject.optString("msg"));
        title = errorObject.optString("msg");
    /*if(errorObject.has("errors") && !TextUtils.isEmpty(errorObject.optString("errors"))
            && errorObject.optBoolean("error")) {
      if((errorObject.optJSONObject("errors") != null) && (errorObject.optJSONObject("errors") != null)) {
        message = Objects.requireNonNull(errorObject.optJSONObject("errors")).optString("msg");
      } else if(errorObject.optJSONArray("errors") != null) {
        message = "";
        for(int i = 0; i < errorObject.optJSONArray("errors").length(); i++) {
          message += Objects.requireNonNull(errorObject.optJSONArray("errors")).optJSONObject(i).optString("msg") + ".\n\n";
        }
      }
    }*/
        if (errorObject.has("msg")) {
            if (errorObject.has("errorDetails")
                    && (errorObject.optJSONObject("errorDetails") != null && (errorObject.optJSONObject("errorDetails") instanceof JSONObject))) {
                message = new StringBuilder(Objects.requireNonNull(errorObject.optJSONObject("errorDetails")).optString("msg"));
            } else if (errorObject.optJSONArray("errors") != null && errorObject.optJSONArray("errors") instanceof JSONArray) {
                message = new StringBuilder();
                for (int i = 0; i < errorObject.optJSONArray("errors").length(); i++) {
                    message.append(Objects.requireNonNull(errorObject.optJSONArray("errors")).optJSONObject(i).optString("msg")).append(".\n\n");
                }
            } else {
                title = "";
                message = new StringBuilder(errorObject.optString("msg"));
            }
        }else{
            message =new StringBuilder();
            message.append("Something went wrong!");
        }
        showMessage(activity, MESSAGE_TYPE, title, message.toString());
    }

    /* show alert / toast based on usability*/
    public void showMessage(final Context activity, int METHOD_TYPE, final String title, final String message) {
        switch(METHOD_TYPE) {
            case SBM2Constants.MESSAGE_TYPE_ALERT_DIALOG:
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setMessage(message);
                if(!TextUtils.isEmpty(title)) {
                    ab.setTitle(title);
                }
                ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ab.show();
                break;
            case SBM2Constants.MESSAGE_TYPE_TOAST:
                Toast.makeText(activity, title + " " + message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*Show progress animation loader*/
    public void showProgressDialog(final Activity activity, final String loading) {
        if(view != null) {
            hideProgressDialog(activity);
        }
        view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_loading, null);
        CardView viewLoadingCardView = view.findViewById(R.id.viewLoadingCardView);
        viewLoadingCardView.setCardBackgroundColor(activity.getResources().getColor(AppController.BG_COLOR_DEFAULT[new Random().nextInt(AppController.BG_COLOR_DEFAULT.length - 1)]));
        activity.addContentView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    /*hide progress animation loader*/
    public void hideProgressDialog(final Activity activity) {
        try {
            (activity.findViewById(R.id.animationView)).setVisibility(View.GONE);
        } catch(Exception e) {
        }
        try {
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            for(int i = 0; i < rootView.getChildCount(); i++) {
                if(rootView.getChildAt(i) == view) {
                    rootView.removeView(view);
                }
            }
        } catch(Exception e) {
        }
        try {
            (activity.findViewById(R.id.progress)).setVisibility(View.GONE);
        } catch(Exception e) {
        }
    }

    /*Login / Resend OTP for Login */
    public void  onSignInOrResendOTPForSignIn(final AppCompatActivity activity, OnResponseListener onResponseListener) {
        final String URL = URLDataConstants.getInstance().USER_LOGIN;
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("userName", SBMPreferenceData.getPreferenceItem(activity, SBMPreferenceData.MOBILE, ""));
            requestParams.put("otp", true);
        } catch(Exception e) {e.printStackTrace();}
        new WebServiceUtils(activity, WebServiceUtils.METHOD_POST, URL, requestParams, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {
                SBMUtils.getInstance().hideProgressDialog(activity);
                onResponseListener.OnResponseFailure(response);
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                SBMUtils.getInstance().hideProgressDialog(activity);
                onResponseListener.OnResponseSuccess(response);

            }
        }, true, WebServiceUtils.HEADER_TYPE_NONE);
    }

    /*Register or resend otp for register*/
    public void submitRegistrationOrResendOTPToRegisterAPI(final AppCompatActivity activity, final OnResponseListener onResponseListener) {
        final String URL = URLDataConstants.getInstance().USER_REGISTER;
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("userName", SBMPreferenceData.getPreferenceItem(activity, SBMPreferenceData.MOBILE, ""));
            requestParams.put("first_name", SBMPreferenceData.getPreferenceItem(activity, SBMPreferenceData.FIRST_NAME, ""));
            requestParams.put("last_name", SBMPreferenceData.getPreferenceItem(activity, SBMPreferenceData.LAST_NAME, ""));
        } catch(Exception e) {e.printStackTrace();}
        new WebServiceUtils(activity, WebServiceUtils.METHOD_POST, URL, requestParams, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {
                SBMUtils.getInstance().hideProgressDialog(activity);
                onResponseListener.OnResponseFailure(response);
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                SBMUtils.getInstance().hideProgressDialog(activity);
                onResponseListener.OnResponseSuccess(response);
            }
        }, true, WebServiceUtils.HEADER_TYPE_NONE);
    }
}
