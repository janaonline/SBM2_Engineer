package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnButtonClick;
import com.ichangemycity.swachhbharatengineer.databinding.ProfileActivityBinding;
import com.ichangemycity.webservice.ParseComplaintData;

public class ProfileViewActivity extends BaseAppCompatActivity {

    private ProfileActivityBinding binding;


    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.profile_activity);
        activity = ProfileViewActivity.this;
        setToolbarAndCustomizeTitle(activity.getResources().getString(R.string.profile));

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.showAlert(activity, activity.getResources().getString(R.string.logout) + "?", activity.getResources().getString(R.string
                        .are_you_sure_to_logout), true, new OnButtonClick() {
                    @Override
                    public void onPositiveButtonClicked(DialogInterface dialogInterface) {
                        ICMyCPreferenceData.clearPreferences(activity);
                        activity.startActivity(new Intent(activity, Splashscreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        activity.finish();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onNegativeButtonClicked(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });
        ParseComplaintData.setImage(activity, binding.userImage, null, ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.userProfileImage,
                ""), true);


        String version = "<font color=" + activity.getResources().getColor(R.color.greyDark) + ">" + BuildConfig.VERSION_NAME + "</font>";
        binding.tvVersion.setText(Html.fromHtml(activity.getResources().getString(R.string.app_name) + " " + version));
    }

    private void setTextForNameEmailMobileNo() {
        binding.userName.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData
                .user_full_name, getResources().getString(R.string.you)));
        binding.userLocation.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.location, "N/A"));
        binding.mobileNumber.setText(ICMyCPreferenceData.getPreferenceItem(activity, ICMyCPreferenceData.Mobile_No, "N/A"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setTextForNameEmailMobileNo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setToolbarAndCustomizeTitle(String title) {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        binding.toolbar.setNavigationOnClickListener(v -> activity.finish());
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(title);
        binding.toolbar.setTitleTextColor(Color.WHITE);

    }


}
