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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UHMessageMetaButton button = (UHMessageMetaButton) o;

        if (title != null ? !title.equals(button.title) : button.title != null) return false;
        if (click != null ? !click.equals(button.click) : button.click != null) return false;
        if (uri != null ? !uri.equals(button.uri) : button.uri != null) return false;
        if (survey != null ? !survey.equals(button.survey) : button.survey != null) return false;
        if (survey_title != null ? !survey_title.equals(button.survey_title) : button.survey_title != null)
            return false;
        if (payload != null ? !payload.equals(button.payload) : button.payload != null)
            return false;
        return image != null ? image.equals(button.image) : button.image == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (click != null ? click.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (survey != null ? survey.hashCode() : 0);
        result = 31 * result + (survey_title != null ? survey_title.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    public interface OnClickListener {
        void onClick();
    }
}
