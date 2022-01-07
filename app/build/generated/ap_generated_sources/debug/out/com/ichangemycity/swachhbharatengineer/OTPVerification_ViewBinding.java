// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OTPVerification_ViewBinding implements Unbinder {
  private OTPVerification target;

  @UiThread
  public OTPVerification_ViewBinding(OTPVerification target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public OTPVerification_ViewBinding(OTPVerification target, View source) {
    this.target = target;

    target.otpEditText = Utils.findRequiredViewAsType(source, R.id.enterotp, "field 'otpEditText'", EditText.class);
    target.resendCode = Utils.findRequiredViewAsType(source, R.id.resendCode, "field 'resendCode'", TextView.class);
    target.done = Utils.findRequiredViewAsType(source, R.id.done, "field 'done'", Button.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.back, "field 'back'", ImageView.class);
    target.parentLayout = Utils.findRequiredViewAsType(source, R.id.parentLayout, "field 'parentLayout'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OTPVerification target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.otpEditText = null;
    target.resendCode = null;
    target.done = null;
    target.back = null;
    target.parentLayout = null;
  }
}
