// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.textfield.TextInputLayout;
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
    target.done = Utils.findOptionalViewAsType(source, R.id.done, "field 'done'", Button.class);
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
    target.done = null;
    target.confirmPasswordTIL = null;
    target.toolbar = null;
  }
}
