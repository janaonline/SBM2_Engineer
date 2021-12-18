// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ichangemycity.swachhbharatengineer.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CommentsAdapter$AddRemarkViewHolder_ViewBinding implements Unbinder {
  private CommentsAdapter.AddRemarkViewHolder target;

  @UiThread
  public CommentsAdapter$AddRemarkViewHolder_ViewBinding(CommentsAdapter.AddRemarkViewHolder target,
      View source) {
    this.target = target;

    target.mName = Utils.findOptionalViewAsType(source, R.id.userName, "field 'mName'", TextView.class);
    target.mDescription = Utils.findOptionalViewAsType(source, R.id.description_count, "field 'mDescription'", TextView.class);
    target.mpostedOn = Utils.findOptionalViewAsType(source, R.id.postedOn, "field 'mpostedOn'", TextView.class);
    target.mUserImage = Utils.findOptionalViewAsType(source, R.id.userImage, "field 'mUserImage'", CircleImageView.class);
    target.comment_image = Utils.findRequiredViewAsType(source, R.id.comment_image, "field 'comment_image'", ImageView.class);
    target.cvCommentImage = Utils.findRequiredViewAsType(source, R.id.cvCommentImage, "field 'cvCommentImage'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CommentsAdapter.AddRemarkViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mName = null;
    target.mDescription = null;
    target.mpostedOn = null;
    target.mUserImage = null;
    target.comment_image = null;
    target.cvCommentImage = null;
  }
}
