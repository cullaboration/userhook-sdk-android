/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.hookpoint;

import com.userhook.BuildConfig;
import com.userhook.UHBaseTest;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.hookpoint.UHHookPointAction;
import com.userhook.hookpoint.UHHookPointMessage;
import com.userhook.hookpoint.UHHookPointSurvey;

import org.json.JSONObject;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHHookPointTest extends UHBaseTest {

    @Test
    public void createMessageHookPoint() throws Exception {

        String jsonString = "{\"type\":\"message\"}";

        JSONObject json = new JSONObject(jsonString);

        UHHookPoint hookPoint = UHHookPoint.createWithData(json);

        assertEquals(hookPoint.getClass(), UHHookPointMessage.class);
    }

    @Test
    public void createSurveyHookPoint() throws Exception {

        String jsonString = "{\"type\":\"survey\"}";

        JSONObject json = new JSONObject(jsonString);

        UHHookPoint hookPoint = UHHookPoint.createWithData(json);

        assertEquals(hookPoint.getClass(), UHHookPointSurvey.class);
    }

    @Test
    public void createActionHookPoint() throws Exception {

        String jsonString = "{\"type\":\"action\"}";

        JSONObject json = new JSONObject(jsonString);

        UHHookPoint hookPoint = UHHookPoint.createWithData(json);

        assertEquals(hookPoint.getClass(), UHHookPointAction.class);
    }
}
