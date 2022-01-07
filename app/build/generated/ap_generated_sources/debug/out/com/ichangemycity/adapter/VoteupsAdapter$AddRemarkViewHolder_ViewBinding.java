// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ichangemycity.swachhbharatengineer.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VoteupsAdapter$AddRemarkViewHolder_ViewBinding implements Unbinder {
  private VoteupsAdapter.AddRemarkViewHolder target;

  @UiThread
  public VoteupsAdapter$AddRemarkViewHolder_ViewBinding(VoteupsAdapter.AddRemarkViewHolder target,
      View source) {
    this.target = target;

    target.mName = Utils.findRequiredViewAsType(source, R.id.userName, "field 'mName'", TextView.class);
    target.mDescription = Utils.findRequiredViewAsType(source, R.id.description_count, "field 'mDescription'", TextView.class);
    target.postedOn = Utils.findRequiredViewAsType(source, R.id.postedOn, "field 'postedOn'", TextView.class);
    target.mUserImage = Utils.findRequiredViewAsType(source, R.id.userImage, "field 'mUserImage'", CircleImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    VoteupsAdapter.AddRemarkViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mName = null;
    target.mDescription = null;
    target.postedOn = null;
    target.mUserImage = null;
  }
}
