/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;

import java.util.HashMap;
import java.util.Map;

public class UHActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private int activeActivities = 0;
    private long sessionStartTime, backgroundTime;

    private static final int UH_TIME_BETWEEN_SESSIONS_IN_SECONDS = 600; // 10 minutes

    private boolean fetchHookpointsOnSessionStart = false;

    private Activity currentActivity;

    public UHActivityLifecycle() {

    }

    public UHActivityLifecycle(boolean fetchHookpointsOnSessionStart) {
        this.fetchHookpointsOnSessionStart = fetchHookpointsOnSessionStart;
    }


    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    public void onActivityDestroyed(Activity activity) {

    }

    public void onActivityPaused(Activity activity) {



    }

    public void onActivityResumed(Activity activity) {

        // track the activity that is in the foreground
        currentActivity = activity;

        // track push message opens
        Intent intent = activity.getIntent();
        if (intent != null  && intent.hasExtra(UserHook.UH_PUSH_DATA) && intent.hasExtra(UserHook.UH_PUSH_TRACKED)
                && !intent.getBooleanExtra(UserHook.UH_PUSH_TRACKED, false)) {

            // handle push payload
            if (intent.hasExtra(UserHook.UH_PUSH_PAYLOAD)) {
                String payload = intent.getStringExtra(UserHook.UH_PUSH_PAYLOAD);
                UserHook.handlePushPayload(activity, payload);
            }

            // handle new feedback if needed
            if (intent.hasExtra(UserHook.UH_PUSH_FEEDBACK)) {
                UserHook.setHasNewFeedback(true);
                intent.removeExtra(UserHook.UH_PUSH_FEEDBACK);
            }

            // track open
            UserHook.trackPushOpen((Map<String,String>)intent.getSerializableExtra(UserHook.UH_PUSH_DATA));
            intent.removeExtra(UserHook.UH_PUSH_TRACKED);
        }

    }

    public void onActivitySaveInstanceState(Activity activity,
                                            Bundle outState) {

    }

    public void onActivityStarted(Activity activity) {

        // mark that an activity has started
        activeActivities++;
        if (activeActivities < 0) {
            activeActivities = 0;
        }

        // if only one activity is started, then this means the app is just came to the foreground
        if (activeActivities == 1) {
            // session started

            sessionStartTime = System.currentTimeMillis();
            Log.i(UserHook.TAG,"session started");

            if ((System.currentTimeMillis() - backgroundTime) / 1000 > UH_TIME_BETWEEN_SESSIONS_IN_SECONDS) {
                // mark this as a new session
                createSession(activity);
            }

        }
    }

    public void onActivityStopped(Activity activity) {

        activeActivities--;

        if (activeActivities < 0) {
            activeActivities = 0;
        }

        // if no activities are active, then the app must be in the background
        if (activeActivities == 0) {
            // session stopped

            long sessionLength = (System.currentTimeMillis() - sessionStartTime) / 1000;

            Log.i(UserHook.TAG,"session stopped: " + sessionLength);
            if (sessionLength > 0) {
                // send session data to server
                Map<String, Object> data = new HashMap<String,Object>();
                data.put("session_time", sessionLength+"");

                UHOperation operation = new UHOperation();
                operation.updateSessionData(data, null);

            }



            backgroundTime = System.currentTimeMillis();

        }


    }

    /**
     * is the app in the foreground
     *
     * @return if app is currently in the foreground
     */
    public boolean isForeground() {
        return activeActivities > 0;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private void createSession(final Activity activity) {
        UHOperation operation = new UHOperation();

        if (fetchHookpointsOnSessionStart) {
            // fetch hookpoints once the session has been created
            operation.createSession(new UserHook.UHSuccessListener() {
                @Override
                public void onSuccess() {
                    UserHook.fetchHookPoint(new UserHook.UHHookPointFetchListener() {
                        @Override
                        public void onSuccess(UHHookPoint hookPoint) {
                            if (hookPoint != null) {
                                hookPoint.execute(activity);
                            }
                        }

                        @Override
                        public void onError() {
                            Log.e(UserHook.TAG,"error fetching hookpoints");
                        }
                    });
                }
            });
        }
        else {
            operation.createSession(null);
        }
    }

}
