/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.userhook.UserHook;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class UHDeviceInfo implements UHDeviceInfoProvider {

    private Context context;

    public UHDeviceInfo(Context context) {
        this.context = context;
    }

    public String getOsVersion() {

        return Build.VERSION.RELEASE +"";
    }

    public String getDevice() {
        return Build.MANUFACTURER +" " + Build.MODEL;
    }

    public String getLocale() {
        return context.getResources().getConfiguration().locale.toString();
    }

    public String getAppVersion() {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (Exception e) {
            Log.e(UserHook.TAG, "could not load version number info.");

            return null;
        }
    }

    public long getTimezoneOffset() {

        Calendar cal = Calendar.getInstance();
        TimeZone timezone = cal.getTimeZone();
        int offset = timezone.getRawOffset();

        return TimeUnit.SECONDS.convert(offset, TimeUnit.MILLISECONDS);

    }


}
