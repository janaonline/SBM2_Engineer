// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UserMobileNumber_ViewBinding implements Unbinder {
  private UserMobileNumber target;

  @UiThread
  public UserMobileNumber_ViewBinding(UserMobileNumber target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UserMobileNumber_ViewBinding(UserMobileNumber target, View source) {
    this.target = target;

    target.mobileNumber = Utils.findRequiredViewAsType(source, R.id.et_mobno, "field 'mobileNumber'", EditText.class);
    target.submit = Utils.findRequiredViewAsType(source, R.id.done, "field 'submit'", Button.class);
    target.signupLinkBtn = Utils.findRequiredViewAsType(source, R.id.signupLinkBtn, "field 'signupLinkBtn'", TextView.class);
    target.selectedLanguage = Utils.findRequiredViewAsType(source, R.id.selectedLanguage, "field 'selectedLanguage'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    UserMobileNumber target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mobileNumber = null;
    target.submit = null;
    target.signupLinkBtn = null;
    target.selectedLanguage = null;
  }
}
