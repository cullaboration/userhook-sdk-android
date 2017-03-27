/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.userhook.UserHook;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public interface UHDeviceInfoProvider {

    public String getOsVersion();

    public String getDevice();
    public String getLocale();

    public String getAppVersion();

    public long getTimezoneOffset();

}
