// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.adapter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.android.volley.toolbox.NetworkImageView;
import com.ichangemycity.swachhbharatengineer.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HomeTabLocalFeedAdapter$ViewHolder_ViewBinding implements Unbinder {
  private HomeTabLocalFeedAdapter.ViewHolder target;

  @UiThread
  public HomeTabLocalFeedAdapter$ViewHolder_ViewBinding(HomeTabLocalFeedAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.tv_username = Utils.findRequiredViewAsType(source, R.id.tv_username, "field 'tv_username'", TextView.class);
    target.complaint_status = Utils.findRequiredViewAsType(source, R.id.complaint_status, "field 'complaint_status'", TextView.class);
    target.moreInfo = Utils.findRequiredViewAsType(source, R.id.moreInfo, "field 'moreInfo'", TextView.class);
    target.created_on = Utils.findRequiredViewAsType(source, R.id.created_on, "field 'created_on'", TextView.class);
    target.complaint_category = Utils.findRequiredViewAsType(source, R.id.complaint_category, "field 'complaint_category'", TextView.class);
    target.complaint_location = Utils.findRequiredViewAsType(source, R.id.complaint_location, "field 'complaint_location'", TextView.class);
    target.tv_feed_user_name = Utils.findRequiredViewAsType(source, R.id.tv_feed_user_name, "field 'tv_feed_user_name'", TextView.class);
    target.tv_feed = Utils.findRequiredViewAsType(source, R.id.tv_feed, "field 'tv_feed'", TextView.class);
    target.votedUpCount = Utils.findRequiredViewAsType(source, R.id.votedUpCount, "field 'votedUpCount'", TextView.class);
    target.commentedCount = Utils.findRequiredViewAsType(source, R.id.commentedCount, "field 'commentedCount'", TextView.class);
    target.ic_directions = Utils.findRequiredViewAsType(source, R.id.ic_directions, "field 'ic_directions'", ImageView.class);
    target.rl_cc_top = Utils.findRequiredViewAsType(source, R.id.rl_cc_top, "field 'rl_cc_top'", RelativeLayout.class);
    target.rl_top_feed = Utils.findRequiredViewAsType(source, R.id.rl_top_feed, "field 'rl_top_feed'", RelativeLayout.class);
    target.complaint_image = Utils.findRequiredViewAsType(source, R.id.complaint_image, "field 'complaint_image'", NetworkImageView.class);
    target.category_image = Utils.findRequiredViewAsType(source, R.id.category_image, "field 'category_image'", ImageView.class);
    target.feed_flag = Utils.findRequiredViewAsType(source, R.id.feed_flag, "field 'feed_flag'", ImageView.class);
    target.changeStatus = Utils.findRequiredViewAsType(source, R.id.changeStatus, "field 'changeStatus'", Spinner.class);
    target.frameSpinner = Utils.findRequiredViewAsType(source, R.id.frameSpinner, "field 'frameSpinner'", FrameLayout.class);
    target.card = Utils.findRequiredViewAsType(source, R.id.card, "field 'card'", LinearLayout.class);
    target.user_image = Utils.findRequiredViewAsType(source, R.id.user_image, "field 'user_image'", CircleImageView.class);
    target.rl_cc_images = Utils.findRequiredViewAsType(source, R.id.rl_cc_images, "field 'rl_cc_images'", RelativeLayout.class);
    target.complaintId = Utils.findRequiredViewAsType(source, R.id.complaintId, "field 'complaintId'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HomeTabLocalFeedAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tv_username = null;
    target.complaint_status = null;
    target.moreInfo = null;
    target.created_on = null;
    target.complaint_category = null;
    target.complaint_location = null;
    target.tv_feed_user_name = null;
    target.tv_feed = null;
    target.votedUpCount = null;
    target.commentedCount = null;
    target.ic_directions = null;
    target.rl_cc_top = null;
    target.rl_top_feed = null;
    target.complaint_image = null;
    target.category_image = null;
    target.feed_flag = null;
    target.changeStatus = null;
    target.frameSpinner = null;
    target.card = null;
    target.user_image = null;
    target.rl_cc_images = null;
    target.complaintId = null;
  }
}
