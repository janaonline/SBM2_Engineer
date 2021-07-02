// Generated code from Butter Knife. Do not modify!
package com.ichangemycity.swachhbharatengineer;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.mukesh.OtpView;
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

    target.otp = Utils.findOptionalViewAsType(source, R.id.otp, "field 'otp'", OtpView.class);
    target.resendCode = Utils.findOptionalViewAsType(source, R.id.resendCode, "field 'resendCode'", TextView.class);
    target.done = Utils.findOptionalViewAsType(source, R.id.done, "field 'done'", Button.class);
    target.enterotp = Utils.findOptionalViewAsType(source, R.id.enterotp, "field 'enterotp'", TextView.class);
    target.parentLayout = Utils.findOptionalViewAsType(source, R.id.parentLayout, "field 'parentLayout'", RelativeLayout.class);
    target.toolbar = Utils.findOptionalViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OTPVerification target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.otp = null;
    target.resendCode = null;
    target.done = null;
    target.enterotp = null;
    target.parentLayout = null;
    target.toolbar = null;
  }
}
