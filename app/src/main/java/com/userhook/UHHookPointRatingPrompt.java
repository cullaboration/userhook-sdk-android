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
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class UHHookPointRatingPrompt extends UHHookPoint {

    protected String negativeButtonLabel;
    protected String positiveButtonLabel;
    protected String promptMessage;

    protected UHHookPointRatingPrompt(Map<String, Object> data) {
        super(data);

        Map<String,Object> meta = (Map<String,Object>)data.get("meta");

        if (meta != null) {
            if (meta.containsKey("negativeButtonLabel")) {
                negativeButtonLabel = (String)meta.get("negativeButtonLabel");
            }

            if (meta.containsKey("positiveButtonLabel")) {
                positiveButtonLabel = (String)meta.get("positiveButtonLabel");
            }

            if (meta.containsKey("promptMessage")) {
                promptMessage = (String)meta.get("promptMessage");
            }

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

                ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
                rootView.addView(promptView);
                promptView.showDialog();

                UserHook.trackHookPointDisplay(hookPoint);
            }
        });

    }

}
