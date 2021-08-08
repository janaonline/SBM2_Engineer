package com.ichangemycity.webservice;

public class URLDataConstants {

  private static URLDataConstants mInstance;

  public static URLDataConstants getInstance() {
    return mInstance == null ? mInstance = new URLDataConstants() : mInstance;
  }

  //    QA
  public final String BASE_URL = "http://3.109.23.35:7000/user/v1/";

//  LIVE
//public final String BASE_URL = "http://3.109.23.35:7000/user/v1/";


  //  CONSTANT API PATHS
  public final String USER_REGISTER = BASE_URL + "register";
  public final String USER_REGISTER_VERIFY_OTP = USER_REGISTER + "/otp/verify";
  public final String USER_LOGIN = BASE_URL + "login";
  public final String USER_GENERATE_OTP_FOR_MOBILE_ON_SIGNUP_WITH_EMAIL =
      BASE_URL + "otp/generate/mobile";
  public final String USER_LOGIN_OTP_VERIFY = BASE_URL + "loginOtpVerify";

}
