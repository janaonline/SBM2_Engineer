// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.VideoView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Splashscreen_ViewBinding implements Unbinder {
  private Splashscreen target;

  @UiThread
  public Splashscreen_ViewBinding(Splashscreen target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Splashscreen_ViewBinding(Splashscreen target, View source) {
    this.target = target;

    target.videoView = Utils.findRequiredViewAsType(source, R.id.videoView, "field 'videoView'", VideoView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Splashscreen target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.videoView = null;
  }
}
