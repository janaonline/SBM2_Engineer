// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ViewAllMediaActivity_ViewBinding implements Unbinder {
  private ViewAllMediaActivity target;

  @UiThread
  public ViewAllMediaActivity_ViewBinding(ViewAllMediaActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ViewAllMediaActivity_ViewBinding(ViewAllMediaActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.viewPager = Utils.findRequiredViewAsType(source, R.id.view_pager, "field 'viewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ViewAllMediaActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.viewPager = null;
  }
}
