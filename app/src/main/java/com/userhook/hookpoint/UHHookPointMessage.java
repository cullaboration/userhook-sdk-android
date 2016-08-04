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
import android.view.ViewGroup;

import com.userhook.UserHook;
import com.userhook.model.UHMessageMeta;
import com.userhook.util.UHJsonUtils;
import com.userhook.view.UHMessageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UHHookPointMessage extends UHHookPoint {

    protected UHMessageMeta meta;


    protected UHHookPointMessage(JSONObject json) {
        super(json);

        try {

            if (json.has("meta")) {
                meta = UHMessageMeta.fromJSON(json.getJSONObject("meta"));
            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error parsing action hook point json", je);
        }

    }

    public UHMessageMeta getMeta() {
        return meta;
    }

    public void execute(final Activity activity) {


        final UHHookPoint hookPoint = this;

        final UHMessageView messageView = new UHMessageView(activity, this);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // check for current activity so we have the top most activity in case another activity
                // has started since the hook points were loaded
                Activity currentActivity = UserHook.getActivityLifecycle().getCurrentActivity();
                ViewGroup rootView = (ViewGroup) currentActivity.findViewById(android.R.id.content);
                rootView.addView(messageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                messageView.showDialog();

                UserHook.trackHookPointDisplay(hookPoint);
            }
        });

    }
}
