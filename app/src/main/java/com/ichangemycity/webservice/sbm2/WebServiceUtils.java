package com.ichangemycity.webservice.sbm2;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.SBM2Constants;
import com.ichangemycity.appdata.sbm2.SBM2Utils;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.webservice.URLData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebServiceUtils {

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;
    public static final int METHOD_PATCH = 5;

    public static final int HEADER_TYPE_NORMAL = 0;
    public static final int HEADER_TYPE_EVENTS = 1;
    public static final int HEADER_TYPE_NONE = 2;
    public static final int HEADER_TYPE_ANNOUNCEMENT = 3;
    public static final int HEADER_TYPE_ROLE = 4;
    public static final int HEADER_TYPE_NOTIFICATION = 5;
    public static final int HEADER_TYPE_PUBLIC_TOILETS = 6;
    public static final int HEADER_TYPE_AUTH = 7;
    public static final int HEADER_TYPE_PROFILE = 8;
    public static final int HEADER_TYPE_CONVERT_COMPLAINT_TO_EVENT = 9;
    public static final int HEADER_TYPE_AUTH_REGENERATE_TOKEN = 10;

    public static final String TAG = AppController.class.getSimpleName();

    /**
     * @param activity               activity of calling API
     * @param methodType             either METHOD_GET, METHOD_POST,METHOD_PUT,METHOD_DELETE,METHOD_PATCH
     * @param url                    API url
     * @param params                 HashMap<String,String> if other than GET/PATCH method
     * @param onResponseListener     OnResponseListener callback for success/failure response
     * @param isToShowProgressDialog isToShowProgressDialog boolean-value if needed to show loader
     */

    public WebServiceUtils(final Activity activity, final int methodType, final String url, JSONObject params, OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {

        //Make network call
        switch (methodType) {
            case METHOD_GET:
                doGet(activity, url, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_POST:
                doPost(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;

        }

    }

    private static void doGet(final Activity activity, final String url, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog) {
            AppUtils.getInstance().showProgressDialog(activity);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //                        if (isToShowProgressDialog)
                AppUtils.getInstance().hideProgressDialog(activity);
                AppController.traceLog("URL : ", url);
                AppController.traceLog("Response : ", response + "");
                if (response.has("httpCode")) {
                    if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                        try {
                            onResponseListener.OnResponseSuccess(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            onResponseListener.OnResponseFailure(response);
                            AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    } else if (response.optInt("httpCode") == 401) {
                        AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_ERROR, response.optString("message"));

                    } else if (response.optInt("httpCode") == 404) {
                        onResponseListener.OnResponseFailure(response);
                    } else {
                        try {
                            onResponseListener.OnResponseFailure(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    }
                } else {
                    try {
                        onResponseListener.OnResponseSuccess(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        onResponseListener.OnResponseFailure(response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    if (isToShowProgressDialog) {
                        AppUtils.getInstance().hideProgressDialog(activity);
                    }
                    onResponseListener.OnResponseFailure(new JSONObject().put("error", volleyError.getMessage()));
                    AppUtils.getInstance().handleVolleyError(activity, volleyError);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return URLData.getHeaders(activity, headerType);

            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(AppController.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, AppController.TAG);
        
    }

    public void doPost(final Activity activity, final String url, final JSONObject requestParams, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog) {
            AppUtils.getInstance().showProgressDialog(activity);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestParams, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject responseJSONObject) {
                try {
                    if (isToShowProgressDialog) {
                        AppUtils.getInstance().hideProgressDialog(activity);
                    }
                    AppController.traceLog("doPost response", responseJSONObject.toString());
                    onResponseListener.OnResponseSuccess(responseJSONObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                try {
                    if (isToShowProgressDialog) {
                        AppUtils.getInstance().hideProgressDialog(activity);
                    }
                    NetworkResponse response = error.networkResponse;
                    JSONObject responseObject = new JSONObject();
                    try {
                        responseObject = new JSONObject(new String(response.data));
                        SBM2Utils.getInstance().showErrorResponse(activity, SBM2Constants.MESSAGE_TYPE_ALERT_DIALOG, responseObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                    onResponseListener.OnResponseFailure(responseObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(AppController.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }
}
