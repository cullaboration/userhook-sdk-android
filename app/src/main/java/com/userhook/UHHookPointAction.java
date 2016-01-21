/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

public class UHHookPointAction extends UHHookPoint {

    protected Map<String, Object> payload;

    protected UHHookPointAction(Map<String, Object> data) {
        super(data);

        Map<String, Object> meta = (Map<String, Object>) data.get("meta");

        if (meta != null) {
            if (meta.containsKey("payload")) {
                String payloadString = (String) meta.get("payload");
                try {

                    JSONObject payloadJson = new JSONObject(payloadString);
                    payload = UHJsonUtils.toMap(payloadJson);

                } catch (Exception e) {
                    Log.e("userhook", "error converting payload to map", e);
                }

            }


        }

    }

    public void execute(final Activity activity) {

        UserHook.actionReceived(activity, payload);
        UserHook.trackHookPointInteraction(this);

    }
}
