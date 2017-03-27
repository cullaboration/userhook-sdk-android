package com.userhook.util;

import com.userhook.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHJsonUtilsTest {

    @Test
    public void testToList() throws Exception {

        JSONArray array = new JSONArray("['one','two','three']");
        List list = UHJsonUtils.toList(array);
        List expected = new ArrayList();
        expected.add("one");
        expected.add("two");
        expected.add("three");

        assertEquals(list, expected);
    }

    @Test
    public void testToMap() throws Exception {

        JSONObject json = new JSONObject("{'one':'two', 'three':'four', 'five':['six','seven']}");

        Map<String,Object> map = UHJsonUtils.toMap(json);

        List expected = new ArrayList();
        expected.add("six");
        expected.add("seven");

        assertTrue(map.containsKey("one"));
        assertEquals(map.get("one"), "two");
        assertEquals(map.get("three"), "four");
        assertTrue(map.get("five") instanceof  List);
        assertEquals(map.get("five"), expected);
    }

    @Test
    public void toJsonMap() throws Exception {

        Map<String,String> map = new HashMap();
        map.put("one","two");
        map.put("three","four");

        JSONObject json = UHJsonUtils.toJSON(map);

        assertEquals(json.getString("one"), "two");
        assertEquals(json.getString("three"), "four");
    }
}
