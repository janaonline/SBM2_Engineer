package com.ichangemycity.swachhbharatengineer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.callback.InternetConnectionCallback;


public class CheckConnectivity extends BroadcastReceiver {
    private static InternetConnectionCallback internetConnectionCallback;
    public CheckConnectivity(){}

    public CheckConnectivity(InternetConnectionCallback internetConnectionCallback) {
        this.internetConnectionCallback = internetConnectionCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            final String className = context.getClass().getSimpleName();
            if (className != null) {
                if (className.equalsIgnoreCase("Splashscreen")) {
                    if (isOnline(context)) { // connected

                        new Splashscreen().dialog(true);
                    } else { // disconnected
                        new Splashscreen().dialog(false);
                        AppConstant.isInternetInterrupted = true;

                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(final Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            //should check null because in airplane mode it will be null
            boolean isAppOnline = (netInfo != null && netInfo.isConnected());
            if(isAppOnline){
                this.internetConnectionCallback.onInternetConnected(isAppOnline);
            }else{
                this.internetConnectionCallback.onInternetDisconnected(isAppOnline);
            }
            return isAppOnline;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
/*  needed when internet check happens in independent screen
    private boolean isInternetConnected(final Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            AppConstant.isInternetInterrupted = true;
           AppUtils.getInstance().showToast(context, AppConstant.TOAST_TYPE_ERROR, "Internet Connection Lost");
        } else {
            AppConstant.isInternetInterrupted = false;
           AppUtils.getInstance().showToast(context, AppConstant.TOAST_TYPE_SUCCESS, "Internet Connected");

        }
        return isConnected;
    }*/
}
