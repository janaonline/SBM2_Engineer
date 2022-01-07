package com.ichangemycity.appdata;

import com.ichangemycity.swachhbharatengineer.R;

import java.util.ArrayList;

/**
 * Created by pattabi.raman on 08-02-2018.
 */

public class AppConstant {

    private static AppConstant instance;

    public static AppConstant getInstance() {
        return instance == null ? instance = new AppConstant() : instance;
    }

    public static final int[] BG_COLOR_DEFAULT = new int[]{R.color.complaint_stats_card_acknowledged, R.color.complaint_stats_card_open, R.color.complaint_stats_card_on_the_job, R.color.complaint_stats_card_resolved, R.color.complaint_stats_card_escalated, R.color.complaint_stats_card_rejected, R.color.primaryDark, R.color.secondaryDark, R.color.tertiaryDark, R.color.secondaryLight, R.color.greyDark, R.color.secondary, R.color.tertiary, R.color.primaryLight, R.color.tertiaryLight, R.color.secondaryLight, R.color.primerColorBlack};

    public static final int TOAST_TYPE_ERROR = 100, TOAST_TYPE_SUCCESS = 200, TOAST_TYPE_INFO = 101;
    public static final int PUBLIC_TOILET_OUTSIDE_PIC = 0;
    public static final int PUBLIC_TOILET_INSIDE_PIC = 1;
    public static final int PUBLIC_TOILET_ADDITIONAL_PIC = 2;
    public static int selectedPublicToiletSection = -1;
    public static final String ONBOARDING_TYPE_SET_EMAIL_PASSWORD = "setEmailPassword";
    public static final String ONBOARDING_TYPE_LOGIN = "login";
    public static String deviceToken = "";
    public static boolean isInternetInterrupted = false;
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static boolean isToRefreshComplaint = false;

    public static String USER_TEMP_MOBILE_NUMBER = "";


    public ArrayList<String> imagePreviewList = new ArrayList<>();


}
