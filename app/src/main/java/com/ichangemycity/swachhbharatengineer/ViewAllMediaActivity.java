package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.fragment.ViewPagerWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewAllMediaActivity extends BaseAppCompatActivity {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.view_pager)
  ViewPager viewPager;
  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_all_media);
    ButterKnife.bind(this);
    activity = ViewAllMediaActivity.this;
    setToolbarAndCustomizeTitle(toolbar, "Preview");
    ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager(),
        AppConstant.getInstance().imagePreviewList.size());
    viewPager.setAdapter(adapter);
    viewPager.setOffscreenPageLimit(1);
    try {
      if (getIntent().getExtras().getInt("position") >= 0) {
        viewPager.setCurrentItem(getIntent().getExtras().getInt("position"));
      }
    } catch (Exception e) {
      viewPager.setCurrentItem(0);
    }
  }


  private void setToolbarAndCustomizeTitle(Toolbar toolbar, String title) {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(title);
    toolbar.setNavigationOnClickListener(v -> activity.finish());
    toolbar.setTitleTextColor(Color.WHITE);
    final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
    upArrow.setColorFilter(getResources().getColor(R.color.white), Mode.SRC_ATOP);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);
  }

  private class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private final int mSize;

    ImagePagerAdapter(FragmentManager fm, int size) {
      super(fm);
      mSize = size;
    }

    @Override
    public int getCount() {
      return mSize;
    }

    @Override
    public Fragment getItem(int position) {
      return ViewPagerWebView.newInstance(position, AppConstant.getInstance().imagePreviewList);
    }
  }


}