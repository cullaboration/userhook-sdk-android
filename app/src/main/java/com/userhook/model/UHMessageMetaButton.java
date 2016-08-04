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

public class UHMessageMetaButton {

    protected String title;
    protected String click;
    protected String uri;
    protected String survey;
    protected String survey_title;
    protected String payload;
    protected UHMessageMetaImage image;

    protected OnClickListener clickListener;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public String getSurvey_title() {
        return survey_title;
    }

    public void setSurvey_title(String survey_title) {
        this.survey_title = survey_title;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public UHMessageMetaImage getImage() {
        return image;
    }

    public void setImage(UHMessageMetaImage image) {
        this.image = image;
    }

    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public OnClickListener getOnClickListener() {
        return clickListener;
    }

    public JSONObject toJSON() {

        JSONObject json = new JSONObject();

        try {

            if (title != null) {
                json.put("title", title);
            }

            if (click != null) {
                json.put("click", click);
            }

            if (survey != null) {
                json.put("survey", survey);
            }

            if (survey_title != null) {
                json.put("survey_title", survey_title);
            }

            if (payload != null) {
                json.put("payload", payload);
            }

            if (uri != null) {
                json.put("uri", uri);
            }

            if (image != null) {
                json.put("image", image.toJSON());
            }
        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error creating meta button json", je);
        }

        return json;
    }

    public static UHMessageMetaButton fromJSON(JSONObject json) {

        UHMessageMetaButton button = new UHMessageMetaButton();

        try {

            String[] fields = {"title","click","uri","survey","survey_title","payload"};
            for (String field : fields) {
                if (json.has(field)) {
                    Field f = UHMessageMetaButton.class.getDeclaredField(field);
                    f.set(button, json.getString(field));
                }
            }

            if (json.has("image")) {
                UHMessageMetaImage image = UHMessageMetaImage.fromJSON(json.getJSONObject("image"));
                button.image = image;
            }

        }
        catch (Exception e) {
            Log.e(UserHook.TAG, "error parsing message meta button json",e);
        }

        return button;
    }

    public interface OnClickListener {
        void onClick();
    }
}
