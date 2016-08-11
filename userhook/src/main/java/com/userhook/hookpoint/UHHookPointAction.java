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
import com.userhook.util.UHJsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UHHookPointAction extends UHHookPoint {

    protected Map<String, Object> payload;

    protected UHHookPointAction(JSONObject json) {
        super(json);

        try {

            if (json.has("meta")) {
                JSONObject meta = json.getJSONObject("meta");

                if (meta != null && meta.has("payload")) {
                    String payloadString = (String) meta.get("payload");
                    JSONObject payloadJson = new JSONObject(payloadString);
                    payload = UHJsonUtils.toMap(payloadJson);
                }
            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error parsing action hook point json", je);
        }

    }

    public void execute(final Activity activity) {

        UserHook.actionReceived(activity, payload);
        UserHook.trackHookPointInteraction(this);

    }
}
