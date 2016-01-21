/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.cullaboration.userhookdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.userhook.UHHostedPageActivity;
import com.userhook.UHPromptView;
import com.userhook.UserHook;

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
        UserHook.setHookPointActionListener(new UserHook.UHHookPointActionListener() {
            @Override
            public void onAction(Activity activity, Map<String, Object> payload) {
                String action = (String) payload.get("action");
                Log.i("action", "action = " + action);

                Toast.makeText(activity, "the app responded to action: " + action, Toast.LENGTH_LONG).show();


            }
        });

        UserHook.setFeedbackListener(new UserHook.UHFeedbackListener() {
            @Override
            public void onNewFeedback(final Activity activity) {

                if (activity != null) {

                    final UHPromptView dialog = new UHPromptView(activity);
                    dialog.getLabel().setText("You have a new response to your recently submitted feedback. Do you want to read it now?");
                    dialog.getNegativeButton().setText("Later");
                    dialog.getPositiveButton().setText("Read Now");
                    dialog.getPositiveButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(activity, UHHostedPageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // you can attach additional data to the feedback thread
                            // Bundle customFields = new Bundle();
                            // customFields.putString("User Id", "123");
                            // intent.putExtra(UserHook.UH_CUSTOM_FIELDS, customFields);

                            intent.putExtra(UHHostedPageActivity.TYPE_FEEDBACK, "Feedback");
                            startActivity(intent);


                            dialog.hideDialog();
                        }
                    });

                    dialog.getNegativeButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.hideDialog();
                        }
                    });


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
                            rootView.addView(dialog);
                            dialog.showDialog();

                        }
                    });

                }
            }
        });

    }
}
