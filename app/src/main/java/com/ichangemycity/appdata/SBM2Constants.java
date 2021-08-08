package com.ichangemycity.appdata;
public class SBM2Constants {
    private static SBM2Constants mInstance;
    public static SBM2Constants getInstance() {
        return mInstance == null ? mInstance = new SBM2Constants() : mInstance;
    }

    /**
     * Message types
     * */
    public static final int MESSAGE_TYPE_TOAST = 1;
    public static final int MESSAGE_TYPE_ALERT_DIALOG = 2;

    /**
     * Constants for OnBoarding
     * */
    public static final int FROM_SIGN_IN=8;
    public static final int FROM_SIGN_UP=9;

}
