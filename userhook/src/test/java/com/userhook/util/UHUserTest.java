package com.userhook.util;

import com.userhook.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import static org.junit.Assert.*;
/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHUserTest {


    @Test
    public void testUserId() {

        UHUser user = new UHUser(RuntimeEnvironment.application);

        user.setUserId("user123");

        assertEquals(user.getUserId(),"user123");

    }

    @Test
    public void testUserKey() {
        UHUser user = new UHUser(RuntimeEnvironment.application);

        user.setUserKey("key123");

        assertEquals(user.getUserKey(), "key123");
    }
}
