package com.ichangemycity.appdata;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SBMPreferenceData {

  private static SBMPreferenceData mInstance;
  public static final String BEARER_TOKEN = "bearer_token";
  public static final String USER_PROFILE_DATA_JSONOBJECT = "user_profile_data_json_object";
  public static final String MOBILE = "mobile";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_Name";

  public static SBMPreferenceData getInstance() {
    return mInstance == null ? mInstance = new SBMPreferenceData() : mInstance;
  }

  public static void setPreference(Context activity, String key, String value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.apply();
    AppController.traceLog("SET PREFERENCE", key + "------>" + value);
  }

  public static String getPreferenceItem(Activity activity, String key,
                                         String defaultValue) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
    String valueString = preferences.getString(key, defaultValue);
    AppController.traceLog("GET PREFERENCE", key + "------>" + valueString);
    return valueString;
  }


  public static void clearPreferences(Activity activity) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.commit();
    new AppController().cancelPendingRequests(AppController.TAG);
  }
}
