package com.ichangemycity.webservice;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.callback.OnButtonClick;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.swachhbharatengineer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by pattabi.raman on 20-02-2018.
 */

public class WebserviceHelper {
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

    public static final String TAG = AppController.class
            .getSimpleName();

    /**
     * @param activity               activity of calling API
     * @param methodType             either METHOD_GET, METHOD_POST,METHOD_PUT,METHOD_DELETE,METHOD_PATCH
     * @param url                    API url
     * @param params                 HashMap<String,String> if other than GET/PATCH method
     * @param onResponseListener     OnResponseListener callback for success/failure response
     * @param isToShowProgressDialog isToShowProgressDialog boolean-value if needed to show loader
     */

    public WebserviceHelper(final Activity activity, final int methodType, final String url, HashMap<String, String> params,
                            OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        AppUtils.getInstance().hideProgressDialog(activity);
        switch (methodType) {
            case METHOD_GET:
                doGet(activity, url, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_POST:
                doPost(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_PUT:
                doPut(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_PATCH:
                doPatch(activity, url.replace(" ", "%20"), onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_DELETE:
                doDelete(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            default:
                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, "unknown api method call");
                break;
        }
    }

    private void doPut(final Activity activity, final String url, final HashMap<String, String> params, final OnResponseListener onResponseListener,
                       final boolean isToShowProgressDialog, final int headerType) {
        AppUtils.getInstance().showProgressDialog(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response0) {
                try {
                    JSONObject response = new JSONObject(response0);
                    if (isToShowProgressDialog)
                        AppUtils.getInstance().hideProgressDialog(activity);
                    if (response.has("httpCode")) {
                        if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_SUCCESS, response.optString("message"));
                            onResponseListener.OnResponseSuccess(response);
                        } else {
                            onResponseListener.OnResponseFailure(response);
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, response.optString("message"));
                        }
                    } else {
                        try {
                            onResponseListener.OnResponseSuccess(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            onResponseListener.OnResponseFailure(response);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isToShowProgressDialog)
                            AppUtils.getInstance().hideProgressDialog(activity);
                        onResponseListener.OnResponseFailure(null);
                        AppController.handleVolleyError(activity, error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers = URLData.getHeaders(activity, headerType);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, AppController.TAG);
    }

    private void doDelete(final Activity activity, final String url, final HashMap<String, String> params, final OnResponseListener
            onResponseListener, final boolean
                                  isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog)
            AppUtils.getInstance().showProgressDialog(activity);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                (String) null, response -> {
            try {
                if (isToShowProgressDialog)
                    AppUtils.getInstance().hideProgressDialog(activity);
                if (response.has("httpCode")) {
                    if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_SUCCESS, response.optString("message"));
                        onResponseListener.OnResponseSuccess(response);
                    } else {
                        onResponseListener.OnResponseFailure(response);
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, response.optString("message"));
                    }
                } else {
                    try {
                        onResponseListener.OnResponseSuccess(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        onResponseListener.OnResponseFailure(response);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isToShowProgressDialog)
                            AppUtils.getInstance().hideProgressDialog(activity);
                        AppController.handleVolleyError(activity, error);
                        onResponseListener.OnResponseFailure(null);

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return URLData.getHeaders(activity, headerType);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, activity.getClass().getSimpleName());
    }

    private static void doGet(final Activity activity, final String url, final OnResponseListener onResponseListener,
                              final boolean isToShowProgressDialog, final int headerType) {
        AppUtils.getInstance().hideProgressDialog(activity);
        if (isToShowProgressDialog)
            AppUtils.getInstance().showProgressDialog(activity);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                response -> {
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
                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            }
                        } else if (response.optInt("httpCode") == 401) {
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, response.optString("message") + ", please try again");
//                                SecurePrefManager.with(activity).clear().confirm();
//                                activity.startActivity(new Intent(activity, Splashscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                                activity.finish();
//                                new AppController().cancelPendingRequests(AppController.TAG);
                        } else if (response.optInt("httpCode") == 404) {
                            onResponseListener.OnResponseFailure(response);
                        } else {
                            try {
                                onResponseListener.OnResponseFailure(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            }
                        }
                    } else {
                        try {
                            onResponseListener.OnResponseSuccess(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            onResponseListener.OnResponseFailure(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                if (isToShowProgressDialog)
                    AppUtils.getInstance().hideProgressDialog(activity);
                NetworkResponse response = volleyError.networkResponse;
                JSONObject responseObject = new JSONObject();
                try {
                    responseObject = new JSONObject(new String(response.data));
                    showErrorAlert(activity, responseObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                onResponseListener.OnResponseFailure(responseObject);
                AppController.handleVolleyError(activity, volleyError);
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                TAG);
    }

    private static void doPatch(final Activity activity, final String url, final OnResponseListener onResponseListener,
                                final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog)
            AppUtils.getInstance().showProgressDialog(activity);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PATCH,
                url, (String) null,
                response -> {
                    if (isToShowProgressDialog)
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
                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            }
                        } else if (response.optInt("httpCode") == 401) {
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, response.optString("message") + ", please try again");
//                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, response.optString("message"));
//                                SecurePrefManager.with(activity).clear().confirm();
//                                activity.startActivity(new Intent(activity, Splashscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                                activity.finish();
//                                new AppController().cancelPendingRequests(AppController.TAG);
                        } else if (response.optInt("httpCode") == 404) {
                            onResponseListener.OnResponseFailure(response);
                        } else {
                            try {
                                onResponseListener.OnResponseFailure(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            }
                        }
                    } else {
                        try {
                            onResponseListener.OnResponseSuccess(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            onResponseListener.OnResponseFailure(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                if (isToShowProgressDialog)
                    AppUtils.getInstance().hideProgressDialog(activity);
                NetworkResponse response = volleyError.networkResponse;
                JSONObject responseObject = new JSONObject();
                try {
                    responseObject = new JSONObject(new String(response.data));
                    showErrorAlert(activity, responseObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                onResponseListener.OnResponseFailure(responseObject);
                AppController.handleVolleyError(activity, volleyError);
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                TAG);
    }


    private static void doPost(final Activity activity, final String url, final HashMap<String, String> requestParams, final OnResponseListener
            onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog)
            AppUtils.getInstance().showProgressDialog(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                AppController.traceLog("URL : ", url);
                AppController.traceLog("Response : ", response + "");
                try {
                    if (isToShowProgressDialog)
                        AppUtils.getInstance().hideProgressDialog(activity);
                    final JSONObject mJsonObject = new JSONObject(response);

                    if (mJsonObject.optInt("httpCode") == 201 || mJsonObject.optInt("httpCode") == 200) {
                        try {
                            onResponseListener.OnResponseSuccess(mJsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    } else if (mJsonObject.optInt("httpCode") == 401) {
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, mJsonObject.optString("message") + ", please try again");
//                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, mJsonObject.optString("message"));
//                        ICMyCPreferenceData.clearPreferences(activity);
//                        activity.startActivity(new Intent(activity, Splashscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                        activity.finish();
//                        new AppController().cancelPendingRequests(AppController.TAG);
                    } else if (mJsonObject.optInt("httpCode") == 404) {
                        onResponseListener.OnResponseFailure(null);
                    } else if (!mJsonObject.has("httpCode")) { //event create success
                        try {
                            onResponseListener.OnResponseSuccess(mJsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    } else if (!mJsonObject.has("httpCode")) { //event create success
                        try {
                            onResponseListener.OnResponseSuccess(mJsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    } /*else {
                        try {
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, mJsonObject.optString("message"));
                            onResponseListener.OnResponseFailure(responsemJsonObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                        }
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isToShowProgressDialog)
                            AppUtils.getInstance().hideProgressDialog(activity);
                        AppController.handleVolleyError(activity, error);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return requestParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return URLData.getHeaders(activity, headerType);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    public static void showErrorAlert(final Activity activity, final JSONObject errorObject) {
        String message = "", title = "";
        message = errorObject.optString("msg");
        if (errorObject.has("errorDetails") && !TextUtils
                .isEmpty(errorObject.optString("errorDetails")) && errorObject.optBoolean("error")) {
            if (errorObject.optJSONObject("errorDetails") instanceof JSONObject) {
                message = errorObject.optJSONObject("errorDetails").optString("message");
                title = errorObject.optString("msg");
            }

            AppUtils.getInstance().showAlert(activity, title, message, false, new OnButtonClick() {
                @Override
                public void onPositiveButtonClicked(DialogInterface dialogInterface) {

                }

                @Override
                public void onNegativeButtonClicked() {

                }
            });
        }
    }

}