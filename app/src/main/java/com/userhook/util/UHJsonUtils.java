/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UHJsonUtils {


    public static Map<String,Object> toMap(JSONObject object) throws JSONException{

        Map<String, Object> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static JSONObject toJSON(Bundle object) {

        JSONObject jsonObject = new JSONObject();

        for (String key : object.keySet()) {
            try {
                jsonObject.put(key, object.get(key));
            }
            catch (JSONException je) {}
        }
        return jsonObject;
    }

    public static Map<String,Object> bundleToMap(Bundle object) {

        try {
            JSONObject json = toJSON(object);

            if (json != null) {
                return toMap(json);
            }
        }
        catch (JSONException je) {}

        return null;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }
}
