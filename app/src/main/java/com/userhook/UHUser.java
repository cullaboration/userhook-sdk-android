/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.content.Context;
import android.content.SharedPreferences;

public class UHUser {

    private static final String UH_USER_ID = "UH_userid";
    private static final String UH_USER_KEY = "UH_userkey";

    private static final String UH_GROUP = "group";

    public static String getUserId() {
        SharedPreferences prefs = UserHook.applicationContext.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        return prefs.getString(UH_USER_ID, null);
    }

    public static String getUserKey() {
        SharedPreferences prefs = UserHook.applicationContext.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        return prefs.getString(UH_USER_KEY, null);
    }

    public static void setUserId(String userId) {
        SharedPreferences prefs = UserHook.applicationContext.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UH_USER_ID, userId);
        editor.apply();
    }

    public static void setUserKey(String userKey) {
        SharedPreferences prefs = UserHook.applicationContext.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UH_USER_KEY, userKey);
        editor.apply();
    }

    public static void clear() {
        SharedPreferences prefs = UserHook.applicationContext.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
