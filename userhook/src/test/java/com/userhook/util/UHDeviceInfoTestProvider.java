package com.userhook.util;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

public class UHDeviceInfoTestProvider implements UHDeviceInfoProvider {

    private String osVersion;
    private String device;
    private String locale;
    private String appVersion;
    private long timezoneOffset;

    @Override
    public String getOsVersion() {
        return osVersion;
    }

    @Override
    public String getDevice() {
        return device;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public String getAppVersion() {
        return appVersion;
    }

    @Override
    public long getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setTimezoneOffset(long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
}
