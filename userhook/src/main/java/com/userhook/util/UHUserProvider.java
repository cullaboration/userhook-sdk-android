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

public interface UHUserProvider {

    public String getUserId();

    public String getUserKey();

    public void setUserId(String userId);

    public void setUserKey(String userKey);

    public void clear();
}
