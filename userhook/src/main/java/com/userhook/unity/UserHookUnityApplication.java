/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.unity;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.userhook.UserHook;

public class UserHookUnityApplication extends Application {

    public void onCreate() {
        super.onCreate();

        setupUserHook();

    }

    public void setupUserHook() {

        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
            String appId = bundle.getString("userhookAppId", "");
            String appKey = bundle.getString("userhookAppKey", "");

            if (!appId.isEmpty() && !appKey.isEmpty()) {
                UserHook.initialize(this, appId, appKey, false);
            }
            else {
                Log.e("uh","app id and app key must not be null");
            }

        } catch (PackageManager.NameNotFoundException e)
        {
            Log.e("uh", "error initializing User Hook. Missing app id and app key");
        }

    }
}
