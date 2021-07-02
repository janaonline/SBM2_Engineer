// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.andexert.library.RippleView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SetEmailPasswordActivity_ViewBinding implements Unbinder {
  private SetEmailPasswordActivity target;

  @UiThread
  public SetEmailPasswordActivity_ViewBinding(SetEmailPasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SetEmailPasswordActivity_ViewBinding(SetEmailPasswordActivity target, View source) {
    this.target = target;

    target.email = Utils.findOptionalViewAsType(source, R.id.email, "field 'email'", EditText.class);
    target.password = Utils.findOptionalViewAsType(source, R.id.password, "field 'password'", EditText.class);
    target.confirmPassword = Utils.findOptionalViewAsType(source, R.id.confirmPassword, "field 'confirmPassword'", EditText.class);
    target.rippleView = Utils.findOptionalViewAsType(source, R.id.rippleView, "field 'rippleView'", RippleView.class);
    target.confirmPasswordTIL = Utils.findOptionalViewAsType(source, R.id.confirmPasswordTIL, "field 'confirmPasswordTIL'", TextInputLayout.class);
    target.toolbar = Utils.findOptionalViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SetEmailPasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.email = null;
    target.password = null;
    target.confirmPassword = null;
    target.rippleView = null;
    target.confirmPasswordTIL = null;
    target.toolbar = null;
  }
}
