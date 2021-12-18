// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ComplaintDetailNew_ViewBinding implements Unbinder {
  private ComplaintDetailNew target;

  @UiThread
  public ComplaintDetailNew_ViewBinding(ComplaintDetailNew target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ComplaintDetailNew_ViewBinding(ComplaintDetailNew target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.maintoolbar, "field 'toolbar'", Toolbar.class);
    target.tv_username = Utils.findRequiredViewAsType(source, R.id.tv_username, "field 'tv_username'", TextView.class);
    target.hours_ago = Utils.findRequiredViewAsType(source, R.id.hours_ago, "field 'hours_ago'", TextView.class);
    target.complaint_category = Utils.findRequiredViewAsType(source, R.id.complaint_category, "field 'complaint_category'", TextView.class);
    target.complaintLocation = Utils.findRequiredViewAsType(source, R.id.complaintLocation, "field 'complaintLocation'", TextView.class);
    target.commentedCount = Utils.findRequiredViewAsType(source, R.id.commentedCount, "field 'commentedCount'", TextView.class);
    target.share = Utils.findRequiredViewAsType(source, R.id.share, "field 'share'", ImageView.class);
    target.votedUpCount = Utils.findRequiredViewAsType(source, R.id.votedUpCount, "field 'votedUpCount'", TextView.class);
    target.ic_directions = Utils.findRequiredViewAsType(source, R.id.ic_directions, "field 'ic_directions'", ImageView.class);
    target.convertedtoEvent = Utils.findRequiredViewAsType(source, R.id.convertedtoEvent, "field 'convertedtoEvent'", TextView.class);
    target.complaint_status = Utils.findRequiredViewAsType(source, R.id.complaint_status, "field 'complaint_status'", TextView.class);
    target.moreInfo = Utils.findRequiredViewAsType(source, R.id.moreInfo, "field 'moreInfo'", TextView.class);
    target.location = Utils.findRequiredViewAsType(source, R.id.location, "field 'location'", TextView.class);
    target.user_image = Utils.findRequiredViewAsType(source, R.id.user_image, "field 'user_image'", CircleImageView.class);
    target.complaint_image = Utils.findRequiredViewAsType(source, R.id.complaint_image, "field 'complaint_image'", ImageView.class);
    target.viewLine = Utils.findRequiredView(source, R.id.view, "field 'viewLine'");
    target.locationImage = Utils.findRequiredViewAsType(source, R.id.locationImage, "field 'locationImage'", ImageView.class);
    target.directionsbtn = Utils.findRequiredViewAsType(source, R.id.directionsbtn, "field 'directionsbtn'", ImageView.class);
    target.profileCategory = Utils.findRequiredViewAsType(source, R.id.profileCategory, "field 'profileCategory'", RelativeLayout.class);
    target.tv_feed = Utils.findRequiredViewAsType(source, R.id.tv_feed, "field 'tv_feed'", TextView.class);
    target.mRecyclerviewComments = Utils.findRequiredViewAsType(source, R.id.mRecyclerviewComments, "field 'mRecyclerviewComments'", RecyclerView.class);
    target.changeStatus = Utils.findRequiredViewAsType(source, R.id.changeStatus, "field 'changeStatus'", Spinner.class);
    target.frameSpinner = Utils.findRequiredViewAsType(source, R.id.frameSpinner, "field 'frameSpinner'", FrameLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ComplaintDetailNew target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.tv_username = null;
    target.hours_ago = null;
    target.complaint_category = null;
    target.complaintLocation = null;
    target.commentedCount = null;
    target.share = null;
    target.votedUpCount = null;
    target.ic_directions = null;
    target.convertedtoEvent = null;
    target.complaint_status = null;
    target.moreInfo = null;
    target.location = null;
    target.user_image = null;
    target.complaint_image = null;
    target.viewLine = null;
    target.locationImage = null;
    target.directionsbtn = null;
    target.profileCategory = null;
    target.tv_feed = null;
    target.mRecyclerviewComments = null;
    target.changeStatus = null;
    target.frameSpinner = null;
  }
}
