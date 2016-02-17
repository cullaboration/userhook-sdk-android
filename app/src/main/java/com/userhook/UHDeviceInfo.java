/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class UHDeviceInfo {

    public static String getOsVersion() {

        return Build.VERSION.RELEASE +"";
    }

    public static String getDevice() {
        return Build.MANUFACTURER +" " + Build.MODEL;
    }

    public static String getLocale() {
        return UserHook.applicationContext.getResources().getConfiguration().locale.toString();
    }

    public static String getAppVersion() {

        try {
            PackageInfo packageInfo = UserHook.applicationContext.getPackageManager().getPackageInfo(UserHook.applicationContext.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (Exception e) {
            Log.e("userhook", "could not load version number info.");

            return null;
        }
    }

    public static long getTimezoneOffset() {

        Calendar cal = Calendar.getInstance();
        TimeZone timezone = cal.getTimeZone();
        int offset = timezone.getRawOffset();

        return TimeUnit.SECONDS.convert(offset, TimeUnit.MILLISECONDS);

    }


}
