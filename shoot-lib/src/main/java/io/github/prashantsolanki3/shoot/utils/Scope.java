package io.github.prashantsolanki3.shoot.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.github.prashantsolanki3.shoot.Shoot.APP_INSTALL;
import static io.github.prashantsolanki3.shoot.Shoot.APP_VERSION;

@Retention(RetentionPolicy.SOURCE)
@IntDef({APP_INSTALL, APP_VERSION})
public @interface Scope{}