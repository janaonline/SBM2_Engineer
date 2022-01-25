// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AndroidCustomGalleryActivity_ViewBinding implements Unbinder {
  private AndroidCustomGalleryActivity target;

  @UiThread
  public AndroidCustomGalleryActivity_ViewBinding(AndroidCustomGalleryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AndroidCustomGalleryActivity_ViewBinding(AndroidCustomGalleryActivity target,
      View source) {
    this.target = target;

    target.toolbar = Utils.findOptionalViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.imagegrid = Utils.findOptionalViewAsType(source, R.id.PhoneImageGrid, "field 'imagegrid'", GridView.class);
    target.frameLoader = Utils.findOptionalViewAsType(source, R.id.frameLoader, "field 'frameLoader'", FrameLayout.class);
    target.image = Utils.findOptionalViewAsType(source, R.id.image, "field 'image'", ImageView.class);
    target.changePic = Utils.findOptionalViewAsType(source, R.id.changePic, "field 'changePic'", TextView.class);
    target.next = Utils.findOptionalViewAsType(source, R.id.next, "field 'next'", Button.class);
    target.pb_loader = Utils.findOptionalViewAsType(source, R.id.pb_loader, "field 'pb_loader'", ProgressWheel.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AndroidCustomGalleryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.imagegrid = null;
    target.frameLoader = null;
    target.image = null;
    target.changePic = null;
    target.next = null;
    target.pb_loader = null;
  }
}
