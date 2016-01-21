/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.app.Activity;
import android.content.Intent;

import java.util.Map;


public class UHHookPointSurvey extends UHHookPoint {

    protected String surveyId;
    protected String publicTitle;


    protected UHHookPointSurvey(Map<String, Object> data) {
        super(data);

        Map<String,Object> meta = (Map<String,Object>)data.get("meta");

        if (meta != null) {
            if (meta.containsKey("survey")) {
                surveyId = (String)meta.get("survey");
            }

            if (meta.containsKey("publicTitle")) {
                publicTitle = (String)meta.get("publicTitle");
            }

        }

    }

    public void execute(final Activity activity) {


        if (surveyId != null) {

            Intent intent = new Intent(activity, UHHostedPageActivity.class);

            intent.putExtra(UHHostedPageActivity.TYPE_SURVEY, surveyId);
            intent.putExtra(UHHostedPageActivity.SURVEY_TITLE, publicTitle);
            intent.putExtra(UHHostedPageActivity.HOOKPOINT_ID, getId());

            activity.startActivity(intent);

            UserHook.trackHookPointDisplay(this);


        }

    }

}
