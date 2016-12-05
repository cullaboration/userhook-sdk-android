/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.cullaboration.userhookdemo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.userhook.UserHook;

import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {

    public void onCreate() {
        super.onCreate();

        /*
        The demo application assumes that you have defined and entry for userhook_application_id and
        userhook_application_key inside of your strings.xml file. You may hard code your keys directly
        in the code below or use a different method to secure your keys.
         */
            UserHook.initialize(this, getString(R.string.userhook_application_id), getString(R.string.userhook_application_key), true);
            UserHook.setPayloadListener(new UserHook.UHPayloadListener() {
                @Override
                public void onAction(Activity activity, Map<String, Object> payload) {

                    String str = "";
                    for (String key : payload.keySet()) {
                        if (str.equals("")) {
                            str += ", ";
                        }
                        str = key + " = " + payload.get(key);
                        Log.i("action", key + " = " + payload.get(key));
                    }

                    Toast.makeText(activity, "the app responded to action: " + str, Toast.LENGTH_LONG).show();


                }
            });

            // set settings for the feedback screen
            UserHook.setFeedbackScreenTitle("Feedback");
            Map<String, String> customFields = new HashMap<>();
            customFields.put("username", "user123");
            UserHook.setFeedbackCustomFields(customFields);

            UserHook.setFeedbackListener(new UserHook.UHFeedbackListener() {
                @Override
                public void onNewFeedback(final Activity activity) {

                    if (activity != null) {

                        UserHook.showFeedbackPrompt("You have a new response to your recently submitted feedback. Do you want to read it now?", "Later", "Read Now");

                    }
                }
            });

            // set custom notification icon
            UserHook.setPushNotificationIcon(R.drawable.notification);


        }
    }
