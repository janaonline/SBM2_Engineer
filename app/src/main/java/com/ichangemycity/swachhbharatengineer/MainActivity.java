package com.ichangemycity.swachhbharatengineer;

import static android.R.color.holo_blue_bright;
import static android.R.color.holo_green_light;
import static android.R.color.holo_orange_light;
import static android.R.color.holo_red_light;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.ichangemycity.adapter.ComplaintAdapter;
import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnResponseListener;
import com.ichangemycity.model.ChangeStatusModel;
import com.ichangemycity.model.ComplaintData;
import com.ichangemycity.model.ComplaintFilterModel;
import com.ichangemycity.webservice.URLData;
import com.ichangemycity.webservice.URLDataSwachhManch;
import com.ichangemycity.webservice.WebserviceHelper;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ComplaintAdapter.ActionCallBack {

    private AppCompatActivity activity;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<ComplaintFilterModel> complaintFilterModel = new ArrayList<ComplaintFilterModel>();
    public static androidx.appcompat.app.ActionBar actionBar;
    private DrawerLayout drawer;
    private RecyclerView complaintList;
    private CircleImageView circleUserImage;
    private TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.assignLanguage(MainActivity.this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        activity = MainActivity.this;
        AppController.getInstance().checkInAppAutoUpdate(activity);
        clearBackStack();

        toolbar = findViewById(R.id.toolbar);

        circleUserImage = findViewById(R.id.circleUserImage);
        refreshLayout = findViewById(R.id.swipe_container);
        initSwipeOptions();
        setToolbarAndCustomizeTitle(toolbar);
        complaintList = findViewById(R.id.complaintList);
        complaintList.setLayoutManager(new GridLayoutManager(activity, 2));


        toolbar_title.setOnClickListener(v -> {
            onRefresh();
        });

        // to redirect from deeplinking
        onNewIntent(getIntent());
    }

    private void clearBackStack() {
        try {
            UserMobileNumber.activity.finish();
        } catch (Exception e) {
        }
        try {
            OTPVerification.activity.finish();
        } catch (Exception e) {
        }
        try {
            SelectLanguage.act.finish();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String data = intent.getDataString();
        if (data != null) {
            if (Intent.ACTION_VIEW.equals(action) && data != null) {
                AppController.selectedComplaintData.setComplaintId(data.substring(data.lastIndexOf("/") + 1));
                startActivity(new Intent(activity, ComplaintDetailNew.class));
            }
        } else {
            // do nothing
        }
        redirectIfFromPushNotification();

    }

    private void redirectIfFromPushNotification() {
        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getString("body") != null) {
                    //        showNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(AppController.selectedComplaintData.getComplaintId())) {
                startActivity(new Intent(MainActivity.this, ComplaintDetailNew.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerData() {

        // TODO Auto-generated method stub
        complaintFilterModel.clear();
        ComplaintFilterModel mComplaintFilterModel = new ComplaintFilterModel();
        if (ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.roleId, "").equalsIgnoreCase("2")) {
            // 2 or ULB , 4 for Engineer
            mComplaintFilterModel.setDisplayTitle(activity.getResources().getString(R.string.un_assigned_complaints));
            mComplaintFilterModel.setComplaintType(URLData.UN_ASSIGNED_COMPLAINTS);
            mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.un_assigned_count, "0"));

        } else {
            mComplaintFilterModel.setDisplayTitle(activity.getResources().getString(R.string.assigned_complaints));
            mComplaintFilterModel.setComplaintType(URLData.ASSIGNED_COMPLAINTS_ENGINEER);
            mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.assignedCount, "0"));

        }
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[0]);
        mComplaintFilterModel.setResId(R.drawable.acknowledged);

        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.ALL_COMPLAINTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.all_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[1]);
        mComplaintFilterModel.setResId(R.drawable.acknowledged);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, "", ""));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.PRIORITY_COMPLAINTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.high_priority_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[2]);
        mComplaintFilterModel.setResId(R.drawable.escalated);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.high_priority_count, "0"));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.ON_THE_JOB_COMPLAINT_LISTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.on_the_job_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[3]);
        mComplaintFilterModel.setResId(R.drawable.on_the_job);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.on_the_job_count, "0"));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.REOPENED_COMPLAINT_LISTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.re_opened_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[4]);
        mComplaintFilterModel.setResId(R.drawable.open);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.re_opened_count, "0"));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.RESOLVED_COMPLAINT_LISTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.resolved_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[5]);
        mComplaintFilterModel.setResId(R.drawable.resolved);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.resolved_count, "0"));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.GET_REJECTED_COMPLAINT_LISTS);
        mComplaintFilterModel.setDisplayTitle(getString(R.string.rejected_complaints));
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[6]);
        mComplaintFilterModel.setResId(R.drawable.rejected);
        mComplaintFilterModel.setComplaintCount(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.rejected_count, "0"));
        complaintFilterModel.add(mComplaintFilterModel);

        mComplaintFilterModel = new ComplaintFilterModel();
        mComplaintFilterModel.setComplaintType(URLData.SEARCH_COMPLAINTS);
        mComplaintFilterModel.setDisplayTitle("Complaint");
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[7]);
        mComplaintFilterModel.setComplaintColor(AppConstant.BG_COLOR_DEFAULT[7]);
        mComplaintFilterModel.setResId(R.drawable.ic_baseline_search_24);
        mComplaintFilterModel.setComplaintCount("Search");
        complaintFilterModel.add(mComplaintFilterModel);


        ComplaintAdapter complaintAdapter = new ComplaintAdapter(activity, complaintFilterModel);
        complaintList.setAdapter(complaintAdapter);
        complaintAdapter.setActionCallBack(this);

    }
/*
    private void getPrimerCardsAPI() {
        final String url = URLData.URL_PRIMER_CARD + "&cityID=" + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.city_id, "1") + URLData._LANGUAGE + ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.selectedLanguage, "en") + "&deviceOs=" + URLDataSwachhManch.CHANNEL_VALUE;
        new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                AppController.getInstance().setSwachhSurveyPrimerCardData(response);
            }
        }, false, WebserviceHelper.HEADER_TYPE_NORMAL);
    }*/

    private void setToolbarAndCustomizeTitle(Toolbar toolbar) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notifs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notifs) {
            activity.startActivity(new Intent(activity, NotificationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    TextView on_the_job, resolved, rejected, re_opened, high_priority;
//    NavigationView navigationView;

    @SuppressWarnings("deprecation")
   /* private void initializeCountForNavItems() {
        high_priority = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.high_priority));
        on_the_job = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.on_the_job));
        resolved = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.resolved));
        rejected = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.rejected));
        re_opened = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.re_opened));
        setPropertyForNavItemCount();
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        setLeftMenuProfileDetails();
        setSpinnerData();
    }*/

    /*private TextView userNameLeftMenu, textViewLocation, userDesignationLeftMenu;

    private void setLeftMenuProfileDetails() {
        if (navigationView.getHeaderView(0) != null)
            navigationView.removeHeaderView(navigationView.getHeaderView(0));
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        CircleImageView imageView = drawer.findViewById(R.id.imageView1);

        LinearLayout llProfile = drawer.findViewById(R.id.llProfile);

        imageView.setImageResource(R.mipmap.ic_not_found);
        imageView.setTag(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.userProfileImage, URLData.DEFAULT_AVATAR));

        AppUtils.setImage(imageView, null, ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.userProfileImage, ""), true);
        userNameLeftMenu = drawer.findViewById(R.id.userNameLeftMenu);
        textViewLocation = drawer.findViewById(R.id.textViewLocation);
        userDesignationLeftMenu = drawer.findViewById(R.id.userDesignationLeftMenu);

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                startActivity(new Intent(activity, ProfileViewActivity.class));
            }
        });


        userNameLeftMenu.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.user_full_name, activity.getResources().getString(R.string.you)));
        textViewLocation.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.location, ""));
        if (!TextUtils.isEmpty(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.designation, ""))) {
            userDesignationLeftMenu.setVisibility(View.GONE);
            userDesignationLeftMenu.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.designation, ""));
        } else {
            userDesignationLeftMenu.setVisibility(View.GONE);
        }

    }

    private void setPropertyForNavItemCount() {

        high_priority.setGravity(Gravity.CENTER_VERTICAL);
        high_priority.setTypeface(null, Typeface.BOLD);
        high_priority.setTextColor(getResources().getColor(R.color.secondary_text_color));
        high_priority.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.high_priority_count, "0"));

        on_the_job.setGravity(Gravity.CENTER_VERTICAL);
        on_the_job.setTypeface(null, Typeface.BOLD);
        on_the_job.setTextColor(getResources().getColor(R.color.secondary_text_color));
        on_the_job.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.on_the_job_count, "0"));

        resolved.setGravity(Gravity.CENTER_VERTICAL);
        resolved.setTypeface(null, Typeface.BOLD);
        resolved.setTextColor(getResources().getColor(R.color.secondary_text_color));
        resolved.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.resolved_count, "0"));

        rejected.setGravity(Gravity.CENTER_VERTICAL);
        rejected.setTypeface(null, Typeface.BOLD);
        rejected.setTextColor(getResources().getColor(R.color.secondary_text_color));
        rejected.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.rejected_count, "0"));

        re_opened.setGravity(Gravity.CENTER_VERTICAL);
        re_opened.setTypeface(null, Typeface.BOLD);
        re_opened.setTextColor(getResources().getColor(R.color.secondary_text_color));
        re_opened.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.re_opened_count, "0"));

    }*/

            boolean isFirstTime = true;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (item != null) {
            if (!isFirstTime) {
                int id = item.getItemId();
                switch (id) {
//                    case R.id.high_priority:
//                        complaintFilter.setSelection(2);
//                        break;
//                    case R.id.on_the_job:
//                        complaintFilter.setSelection(3);
//                        break;
//                    case R.id.resolved:
//                        complaintFilter.setSelection(5);
//                        break;
//                    case R.id.rejected:
//                        complaintFilter.setSelection(6);
//                        break;
//                    case R.id.re_opened:
//                        complaintFilter.setSelection(4);
//                        break;
//
//                    //Added by Sindhu BC(ITC Infotech)
//                  /*  case R.id.public_toilet_nearby:
//
//                        startActivity(new Intent(MainActivity.this,
//                                PublicToiletNearbyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                        *//*String appPackageName = activity.getPackageName();
//                        try {
//                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
//                                    .parse("http://play.google.com/store/apps/details?id="
//                                            + appPackageName)));
////                        AppController.trackEvent(AppController.RATE_US_ON_PLAYSTORE,
////                                AppController.RATE_US_ON_PLAYSTORE_LANDED,
////                                AppController.RATE_US_ON_PLAYSTORE_LANDED);
//                        } catch (android.content.ActivityNotFoundException anfe) {
//
//                        }*//*
//                        break;*//**/
                    case R.id.rate_us_on_playstore:
                        String appPackageName = activity.getPackageName();
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            //                        AppController.trackEvent(AppController.RATE_US_ON_PLAYSTORE,
                            //                                AppController.RATE_US_ON_PLAYSTORE_LANDED,
                            //                                AppController.RATE_US_ON_PLAYSTORE_LANDED);
                        } catch (android.content.ActivityNotFoundException anfe) {

                        }
                        break;
                    case R.id.nav_privacypolicy:
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URLData.PRIVACY_POLICY)));
                        } catch (android.content.ActivityNotFoundException anfe) {

                        }
                        break;

                    case R.id.report_bug:
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "swachhbharat@janaagraha.org,pattabi.raman@janaagraha.org", null));
                            String sAux = "\n";
                            sAux = sAux + "Bug : \n";
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.app_name) + " - Android App - Bug Report");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, sAux);
                            activity.startActivity(Intent.createChooser(emailIntent, "Report bug using"));

                            //                        AppController.trackEvent(AppController.REPORT_BUG,
                            //                                AppController.REPORT_BUG_LANDED,
                            //                                AppController.REPORT_BUG_LANDED);

                        } catch (Exception e) { // e.toString();
                        }
                        break;
                    case R.id.nav_logout:
                        //                    AppController.trackEvent(
                        //                            AppController.LOGOUT,
                        //                            AppController.LOGGED_OUT_SUCCESS,
                        //                            AppController.LOGGED_OUT_SUCCESS);
                        //            SecurePrefManager.with(activity).clear().confirm();

                        break;
                }
            }
        }
        if (isFirstTime) {
            isFirstTime = false;
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void getProfileDetailsAndRunHomeFeed() {
        final String url = URLData.BASE_URL + URLData.USERS + "?apiKey=" + URLData.API_KEY;
        new WebserviceHelper(activity, WebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure() {
                hideSwipeProgress();
                setSpinnerData();
            }

            @Override
            public void OnResponseSuccess(final JSONObject response) {
                hideSwipeProgress();
                try {
                    if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                        try {
                            handleSuccessResponse(response);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, response.optString("message"));

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, false, WebserviceHelper.HEADER_TYPE_NORMAL);

    }

    private void handleSuccessResponse(final JSONObject mJsonObject) {
        try {
            if (mJsonObject.has("engineer") && mJsonObject.optJSONObject("engineer") != null) {
                JSONObject userData = mJsonObject.optJSONObject("engineer");
                String name = userData.optString("name");
                String mobile_number = userData.optString("mobile_number");
                String latitude = userData.optString("latitude") + "";
                String roleId = userData.optString("role_id");
                String designation = userData.optString("designation");

                String longitude = userData.optString("longitude") + "";
                String location = userData.optString("location");
                String language_code = userData.optString("lang");
                String imageUrl = userData.optString("image_urls");
                String unReadNotificationCount = userData.optString("unread_notification_count");
                String high_priority_count = userData.optString("high_priority_count");
                String on_the_job_count = userData.optString("on_the_job_count");
                String resolved_count = userData.optString("resolved_count");
                String re_opened_count = userData.optString("re_opened_count");
                String rejected_count = userData.optString("rejected_count");
                String un_assigned_count = "0";
                if (userData.has("un_assigned_count")) {
                    un_assigned_count = userData.getString("un_assigned_count");
                }

                if (!imageUrl.equalsIgnoreCase("")) {
                    JSONObject image_urls = new JSONObject(imageUrl);
                    imageUrl = image_urls.optString("original");
                    ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.userProfileImage, imageUrl);
                } else {
                    ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.userProfileImage, "");
                }
                if (roleId != null) {
                    ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.roleId, roleId);
                    // 2 or ULB , 4 for Engineer
                    if (roleId.equalsIgnoreCase("2")) {

                    } else if (roleId.equalsIgnoreCase("4")) {
                        ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.assignedCount, userData.getString("assignedCount"));
                    }
                }
                if (!TextUtils.isEmpty(designation)) {
                    ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.designation, designation);
                } else {
                    ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.designation, "");
                }
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.un_assigned_count, un_assigned_count);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.unreadNotificationsCnt, unReadNotificationCount);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.high_priority_count, high_priority_count);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.on_the_job_count, on_the_job_count);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.resolved_count, resolved_count);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.re_opened_count, re_opened_count);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.rejected_count, rejected_count);

                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.Mobile_No, mobile_number);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.location, location.replace("%20", " "));
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.Latitude, latitude);
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.Longitude, longitude);
                AppUtils.setImage(circleUserImage, null, ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.userProfileImage, ""), true);
                circleUserImage.setOnClickListener(v -> {
                    startActivity(new Intent(activity, ProfileViewActivity.class));
                });
                ICMyCPreferenceData.setPreference(MainActivity.this, ICMyCPreferenceData.user_full_name, name);
            }
//            initializeCountForNavItems();
            setSpinnerData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.activated, "1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AppUtils.getInstance().hideProgressDialog(activity);
           /* if (AppConstant.isToRefreshComplaint) {
                AppConstant.isToRefreshComplaint = false;

            }*/
            getProfileDetailsAndRunHomeFeed();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectedComplaintType(ComplaintFilterModel complaintFilterModel) {
        if (complaintFilterModel != null && !complaintFilterModel.getComplaintType().equalsIgnoreCase(URLData.SEARCH_COMPLAINTS)) {
            Intent intent = new Intent(activity, ComplaintActivity.class);
            intent.putExtra("complaint", complaintFilterModel);
            startActivity(intent);
        } else {
            Intent intent = new Intent(activity, SearchComplaintsActivity.class);
            intent.putExtra("complaint", complaintFilterModel);
            startActivity(intent);
        }
    }


    private void initSwipeOptions() {
        refreshLayout.setOnRefreshListener(this);
        getProfileDetailsAndRunHomeFeed();
        refreshLayout.setEnabled(true);

    }

    private void setAppearance() {
        refreshLayout.setColorSchemeResources(holo_red_light, holo_green_light, holo_orange_light, holo_blue_bright);
    }

    /**
     * It hides the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        refreshLayout.setRefreshing(false);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        refreshLayout.setRefreshing(true);
    }


    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        showSwipeProgress();
        getProfileDetailsAndRunHomeFeed();
//        getPrimerCardsAPI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //clear all constants
            AppController.selectedComplaintData = new ComplaintData();
            AppController.selectedComplaintChangeStatusOptions = new ChangeStatusModel();
            AppController.getInstance().cancelPendingRequests(AppController.TAG);
            AppController.commentData = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
