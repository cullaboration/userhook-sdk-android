/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.model;

import com.userhook.BuildConfig;
import com.userhook.UHBaseTest;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHMessageMetaTest extends UHBaseTest {

    @Test
    public void parseJson() throws Exception {

        JSONObject json = new JSONObject("{\"feedback_body\":\"feedback body text\",\"feedback\":\"true\",\"button2\":{\"title\":\"Close\"},\"button1\":{\"title\":\"Submit\"},\"most\":\"Extremely likely\",\"least\":\"Not at all likely\",\"body\":\"body text\"}");

        UHMessageMeta meta = UHMessageMeta.fromJSON(json);

        assertEquals(meta.getMost(),"Extremely likely");
        assertEquals(meta.getLeast(),"Not at all likely");
        assertEquals(meta.getBody(),"body text");
        assertEquals(meta.getFeedbackBody(),"feedback body text");
        assertEquals(meta.getFeedback(),true);
        assertEquals(meta.getButton1().getTitle(),"Submit");
        assertEquals(meta.getButton2().getTitle(),"Close");


    }

    @Test
    public void setValueByKey() {

        UHMessageMeta meta = new UHMessageMeta();
        meta.setDisplayType("first");

        assertEquals(meta.getDisplayType(), "first");

        meta.setValueByKey("displayType", "second");
        assertEquals(meta.getDisplayType(), "second");
    }
}
