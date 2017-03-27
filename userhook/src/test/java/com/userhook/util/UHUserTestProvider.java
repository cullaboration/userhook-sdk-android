package com.userhook.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.userhook.UserHook;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

public class UHUserTestProvider implements UHUserProvider {

    protected String userId;
    protected String userKey;

    public String getUserId() {
        return userId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void clear() {

    }
}
