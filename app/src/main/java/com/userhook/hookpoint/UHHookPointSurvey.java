/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.hookpoint;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.userhook.util.UHJsonUtils;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.UserHook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class UHHookPointSurvey extends UHHookPoint {

    protected String surveyId;
    protected String publicTitle;


    protected UHHookPointSurvey(JSONObject json) {
        super(json);

        try {

            if (json.has("meta")) {
                JSONObject meta = json.getJSONObject("meta");

                if (meta != null && meta.has("survey")) {
                    surveyId = meta.getString("survey");
                }
                if (meta != null && meta.has("publicTitle")) {
                    publicTitle = meta.getString("publicTitle");
                }
            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error parsing action hook point json", je);
        }


    }

    public void execute(final Activity activity) {


        if (surveyId != null) {

            UserHook.showSurvey(surveyId, publicTitle, this);

            UserHook.trackHookPointDisplay(this);


        }

    }

}
