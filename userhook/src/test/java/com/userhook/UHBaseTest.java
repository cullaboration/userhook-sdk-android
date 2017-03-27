package com.userhook;

import com.userhook.util.UHDeviceInfoTestProvider;
import com.userhook.util.UHInternal;
import com.userhook.util.UHUserTestProvider;

import org.junit.Before;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

public class UHBaseTest {

    @Before
    public void before() {


        UHUserTestProvider user = new UHUserTestProvider();
        user.setUserId("user123");
        user.setUserKey("userkey123");
        UHInternal.getInstance().setUHUserProvider(user);

        UHDeviceInfoTestProvider device = new UHDeviceInfoTestProvider();
        device.setOsVersion("1.2.3");
        device.setAppVersion("0.1");
        device.setLocale("en-us-test");
        device.setDevice("test device");
        device.setTimezoneOffset(-100);

        UHInternal.getInstance().setUHDeviceProvider(device);

    }

}
