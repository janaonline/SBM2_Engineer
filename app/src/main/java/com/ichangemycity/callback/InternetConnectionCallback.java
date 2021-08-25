package com.ichangemycity.callback;

public interface InternetConnectionCallback {
    void onInternetConnected(boolean isConnected);
    void onInternetDisconnected(boolean isConnected);
}
