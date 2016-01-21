/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook;

import android.app.Activity;

import java.util.Map;

public class UHHookPoint {

    protected String id;
    protected String name;
    protected String type;
    protected String applicationName;

    public static final String TYPE_ACTION = "action";
    public static final String TYPE_SURVEY = "survey";
    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_RATING_PROMPT = "rating prompt";
    public static final String TYPE_ACTION_PROMPT = "action prompt";

    public UHHookPoint(String hookpointId) {
        id = hookpointId;
    }

    protected UHHookPoint(Map<String, Object> data) {


        id = (String) data.get("id");
        name = (String) data.get("name");
        type = (String) data.get("type");

        if (data.containsKey("application")) {
            applicationName = (String) data.get("applicatoinName");
        }

    }


    public void execute(final Activity activity) {
        // the child objects handle the logic for the specific hookpoint type
    }

    public String getId() {
        return id;
    }

    public static UHHookPoint createWithData(Map<String, Object> data) {

        UHHookPoint object = null;


        String type = (String) data.get("type");
        if (type.equalsIgnoreCase(TYPE_ACTION)) {
            object = new UHHookPointAction(data);
        } else if (type.equalsIgnoreCase(TYPE_SURVEY)) {
            object = new UHHookPointSurvey(data);
        } else if (type.equalsIgnoreCase(TYPE_RATING_PROMPT)) {
            object = new UHHookPointRatingPrompt(data);
        } else if (type.equalsIgnoreCase(TYPE_ACTION_PROMPT)) {
            object = new UHHookPointActionPrompt(data);
        } else if (type.equalsIgnoreCase(TYPE_MESSAGE)) {
            object = new UHHookPointMessage(data);
        } else {
            object = new UHHookPoint(data);
        }


        return object;
    }


}
