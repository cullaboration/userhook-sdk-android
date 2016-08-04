/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.hookpoint;

import android.app.Activity;
import android.util.Log;

import com.userhook.UserHook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UHHookPoint {

    protected String id;
    protected String name;
    protected String type;

    public static final String TYPE_ACTION = "action";
    public static final String TYPE_SURVEY = "survey";
    public static final String TYPE_MESSAGE = "message";

    public static final String TYPE_RATING_PROMPT = "rating prompt";
    public static final String TYPE_ACTION_PROMPT = "action prompt";

    public UHHookPoint(String hookpointId) {
        id = hookpointId;
    }

    protected UHHookPoint(JSONObject json) {

        try {

            id = json.getString("id");
            name = json.getString("name");
            type = json.getString("type");


        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error parsing hook point json", je);
        }
    }


    public void execute(final Activity activity) {
        // the child objects handle the logic for the specific hookpoint type
    }

    public String getId() {
        return id;
    }

    public static UHHookPoint createWithData(JSONObject json) {

        UHHookPoint object = null;

        try {


            String type = json.getString("type");


            if (type.equalsIgnoreCase(TYPE_ACTION)) {
                object = new UHHookPointAction(json);
            } else if (type.equalsIgnoreCase(TYPE_SURVEY)) {
                object = new UHHookPointSurvey(json);
            } else if (type.equalsIgnoreCase(TYPE_RATING_PROMPT)) {
                object = new UHHookPointRatingPrompt(json);
            } else if (type.equalsIgnoreCase(TYPE_ACTION_PROMPT)) {
                object = new UHHookPointActionPrompt(json);
            } else if (type.equalsIgnoreCase(TYPE_MESSAGE)) {
                object = new UHHookPointMessage(json);
            } else {
                object = new UHHookPoint(json);
            }

        }
        catch (Exception e) {
            Log.e(UserHook.TAG, "error parsing hook point json", e);
        }


        return object;
    }


}
