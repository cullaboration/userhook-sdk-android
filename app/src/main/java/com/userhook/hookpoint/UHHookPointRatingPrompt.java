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
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.userhook.util.UHJsonUtils;
import com.userhook.view.UHPromptView;
import com.userhook.UserHook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

@Deprecated
public class UHHookPointRatingPrompt extends UHHookPoint {

    protected String negativeButtonLabel;
    protected String positiveButtonLabel;
    protected String promptMessage;

    protected UHHookPointRatingPrompt(JSONObject json) {
        super(json);

        try {

            if (json.has("meta")) {
                JSONObject meta = json.getJSONObject("meta");

                if (meta != null) {

                    if (meta.has("negativeButtonLabel")) {
                        negativeButtonLabel = meta.getString("negativeButtonLabel");
                    }
                    if (meta.has("positiveButtonLabel")) {
                        positiveButtonLabel = meta.getString("positiveButtonLabel");
                    }
                    if (meta.has("promptMessage")) {
                        promptMessage = meta.getString("promptMessage");
                    }
                }
            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error parsing action hook point json", je);
        }


    }

    public void execute(final Activity activity) {

        final UHPromptView promptView = new UHPromptView(activity);
        promptView.getLabel().setText(promptMessage);
        promptView.getPositiveButton().setText(positiveButtonLabel);
        promptView.getNegativeButton().setText(negativeButtonLabel);

        final UHHookPoint hookPoint = this;

        promptView.getPositiveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open the review page on google play
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                activity.startActivity(intent);


                UserHook.trackHookPointInteraction(hookPoint);

                // mark that the user has "rated" this app
                UserHook.markAsRated();

                promptView.hideDialog();

            }
        });

        promptView.getNegativeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptView.hideDialog();
            }
        });


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // check for current activity so we have the top most activity in case another activity
                // has started since the hookpoints were loaded
                Activity currentActivity = UserHook.getActivityLifecycle().getCurrentActivity();
                ViewGroup rootView = (ViewGroup) currentActivity.findViewById(android.R.id.content);
                rootView.addView(promptView);
                promptView.showDialog();

                UserHook.trackHookPointDisplay(hookPoint);
            }
        });

    }

}
