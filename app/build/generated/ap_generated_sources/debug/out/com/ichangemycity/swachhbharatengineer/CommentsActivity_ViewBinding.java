// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CommentsActivity_ViewBinding implements Unbinder {
  private CommentsActivity target;

  @UiThread
  public CommentsActivity_ViewBinding(CommentsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CommentsActivity_ViewBinding(CommentsActivity target, View source) {
    this.target = target;

    target.cvimagePreview = Utils.findRequiredViewAsType(source, R.id.cvimagePreview, "field 'cvimagePreview'", CardView.class);
    target.clear = Utils.findRequiredViewAsType(source, R.id.clear, "field 'clear'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CommentsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cvimagePreview = null;
    target.clear = null;
  }
}
