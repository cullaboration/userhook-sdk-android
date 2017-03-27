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

public class UHMessageMeta {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_TWO_BUTTONS = "twobuttons";
    public static final String TYPE_ONE_BUTTON = "onebutton";
    public static final String TYPE_NO_BUTTONS = "nobuttons";

    public static final String CLICK_CLOSE = "close";
    public static final String CLICK_RATE = "rate";
    public static final String CLICK_FEEDBACK = "feedback";
    public static final String CLICK_URI = "uri";
    public static final String CLICK_ACTION = "action";
    public static final String CLICK_SURVEY = "survey";


    protected String displayType;
    protected String body;
    protected UHMessageMetaButton button1;
    protected UHMessageMetaButton button2;

    // used by nps prompts
    protected String feedback_body;
    protected Boolean feedback = false;
    protected String least;
    protected String most;

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UHMessageMetaButton getButton1() {
        return button1;
    }

    public void setButton1(UHMessageMetaButton button1) {
        this.button1 = button1;
    }

    public UHMessageMetaButton getButton2() {
        return button2;
    }

    public void setButton2(UHMessageMetaButton button2) {
        this.button2 = button2;
    }

    public String getFeedbackBody() {
        return feedback_body;
    }

    public Boolean getFeedback() {
        return feedback;
    }

    public String getLeast() {
        return least;
    }

    public String getMost() {
        return most;
    }

    public static UHMessageMeta fromJSON(JSONObject json) {

        UHMessageMeta meta = new UHMessageMeta();

        try {

            // string fields
            String[] fields = {"displayType", "body", "most","least","feedback_body"};
            for (String field : fields) {
                if (json.has(field)) {
                    Field f = UHMessageMeta.class.getDeclaredField(field);
                    f.set(meta, json.getString(field));
                }
            }

            String[] boolFields = {"feedback"};
            for (String field : boolFields) {
                if (json.has(field)) {
                    Field f = UHMessageMeta.class.getDeclaredField(field);
                    f.set(meta, json.getBoolean(field));
                }
            }

            if (json.has("button1")) {
                meta.button1 = UHMessageMetaButton.fromJSON(json.getJSONObject("button1"));
            }

            if (json.has("button2")) {
                meta.button2 = UHMessageMetaButton.fromJSON(json.getJSONObject("button2"));
            }

        } catch (JSONException e) {
            Log.e(UserHook.TAG, "error parsing message meta json", e);
        } catch (NoSuchFieldException nsf) {
            Log.e(UserHook.TAG, "error setting properties of message meta", nsf);
        } catch (IllegalAccessException ia) {
            Log.e(UserHook.TAG, "error accessing properties of message meta", ia);
        }

        return meta;
    }

    public void setValueByKey(String key, String value) {

        if (key == null) {
            return;
        }

        try {

            Field f = UHMessageMeta.class.getDeclaredField(key);
            f.set(this, value);

        }
        catch (Exception e) {
            Log.e(UserHook.TAG, "error setting meta value by key", e);
        }

    }

    public String toJSONString() {

        JSONObject json = new JSONObject();
        try {
            if (displayType != null) {
                json.put("displayType", displayType);
            }

            if (body != null) {
                json.put("body", body);
            }

            if (button1 != null) {
                json.put("button1", button1.toJSON());
            }

            if (button2 != null) {
                json.put("button2", button2.toJSON());
            }

            if (feedback_body != null) {
                json.put("feedback_body", feedback);
                json.put("feedback", feedback);
            }

            if (least != null) {
                json.put("least", least);
            }

            if (most != null) {
                json.put("most", most);
            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error creating json from meta", je);
        }


        return json.toString();
    }
}
