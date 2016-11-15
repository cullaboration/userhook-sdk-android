/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import com.userhook.hookpoint.UHHookPoint;
import com.userhook.hookpoint.UHHookPointMessage;

import org.json.JSONObject;

import static org.junit.Assert.*;

import org.junit.Test;

public class UHHookPointTest {

    @Test
    public void createMessageHookPoint() throws Exception {

        String jsonString = "{\"type\":\"message\"}";

        JSONObject json = new JSONObject();

        UHHookPoint hookPoint = UHHookPoint.createWithData(json);

        assertEquals(hookPoint.getClass(), UHHookPointMessage.class);
    }
}
