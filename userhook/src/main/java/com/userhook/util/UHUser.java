/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.userhook.UserHook;

public class UHUser implements UHUserProvider {

    private static final String UH_USER_ID = "UH_userid";
    private static final String UH_USER_KEY = "UH_userkey";

    private static final String UH_GROUP = "group";

    private Context context;

    public UHUser(Context context) {
        this.context = context;
    }

    public String getUserId() {
        SharedPreferences prefs = context.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        return prefs.getString(UH_USER_ID, null);
    }

    public String getUserKey() {
        SharedPreferences prefs = context.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        return prefs.getString(UH_USER_KEY, null);
    }

    public void setUserId(String userId) {
        SharedPreferences prefs = context.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UH_USER_ID, userId);
        editor.apply();
    }

    public void setUserKey(String userKey) {
        SharedPreferences prefs = context.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UH_USER_KEY, userKey);
        editor.apply();
    }

    public void clear() {
        SharedPreferences prefs = context.getSharedPreferences(UH_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
