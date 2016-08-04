/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.model;

import android.util.Log;

import com.userhook.UserHook;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class UHMessageMetaImage {

    protected String url;
    protected Integer height;
    protected Integer width;

    public String getUrl() {
        return url;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {

            if (url != null) {
                json.put("url", url);
            }

            if (height != null) {
                json.put("height", height);
            }

            if (width != null) {
                json.put("width", width);
            }
        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error creating meta image json", je);
        }

        return json;
    }

    public static UHMessageMetaImage fromJSON(JSONObject json) {

        UHMessageMetaImage image = new UHMessageMetaImage();

        try {

            if (json.has("url")) {
                image.url = json.getString("url");
            }

            if (json.has("height")) {
                image.height = json.getInt("height");
            }

            if (json.has("width")) {
                image.width = json.getInt("width");
            }

        }
        catch (Exception e) {
            Log.e(UserHook.TAG, "error parsing message meta image json",e);
        }

        return image;
    }
}
