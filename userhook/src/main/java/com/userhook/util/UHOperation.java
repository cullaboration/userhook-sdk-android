/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHPage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class UHOperation {


    public static final String UH_APP_ID_HEADER_NAME = "X-USERHOOK-APP-ID";
    public static final String UH_APP_KEY_HEADER_NAME = "X-USERHOOK-APP-KEY";
    public static final String UH_USER_ID_HEADER_NAME = "X-USERHOOK-USER-ID";
    public static final String UH_USER_KEY_HEADER_NAME = "X-USERHOOK-USER-KEY";


    private static final String UH_PATH_SESSION = "/session";
    private static final String UH_PATH_HOOK_POINT_FETCH = "/hookpoint/next";
    private static final String UH_PATH_HOOK_POINT_TRACK = "/hookpoint/track";
    private static final String UH_PATH_PAGES = "/page";
    private static final String UH_PATH_PUSH_REGISTER = "/push/register";
    private static final String UH_PATH_PUSH_OPEN = "/push/open";
    private static final String UH_PATH_MESSAGE_TEMPLATES = "/message/templates";

    private static boolean fetchingHookpoints = false;

    public void createSession(UserHook.UHSuccessListener listener) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        String date = format.format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("sessions", "1");
        data.put("last_launch", date);

        updateSessionData(data, listener);

        // prefetch message templates
        fetchMessageTemplates();
    }


    public void updateSessionData(Map<String, Object> data, final UserHook.UHSuccessListener listener) {

        if (data == null) {
            data = new HashMap<>();
        }

        if (UHUser.getUserId() != null) {
            data.put("user", UHUser.getUserId());
        }

        data.put("sdk", UserHook.UH_SDK_VERSION);
        data.put("os", "android");
        data.put("os_version", UHDeviceInfo.getOsVersion());
        data.put("device", UHDeviceInfo.getDevice());
        data.put("locale", UHDeviceInfo.getLocale());
        data.put("app_version", UHDeviceInfo.getAppVersion());
        data.put("timezone_offset", UHDeviceInfo.getTimezoneOffset());


        UHPostAsyncTask task = new UHPostAsyncTask(data, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    if (string == null || string.isEmpty()) {
                        Log.e(UserHook.TAG,"update session server response was null");
                        return;
                    }

                    JSONObject json = new JSONObject(string);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        JSONObject data = json.getJSONObject("data");

                        if (data.has("user")) {
                            String user = data.getString("user");
                            UHUser.setUserId(user);
                        }

                        if (data.has("key")) {
                            String key = data.getString("key");
                            UHUser.setUserKey(key);
                        }

                        if (data.has("new_feedback") && data.getBoolean("new_feedback")) {
                            UserHook.setHasNewFeedback(true);
                        } else {
                            UserHook.setHasNewFeedback(false);
                        }

                        if (listener != null) {
                            listener.onSuccess();
                        }
                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error");
                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error updating session data", e);
                }

            }
        });


        task.execute(UserHook.UH_API_URL + UH_PATH_SESSION);

    }

    public void fetchPageNames(final UHArrayListener listener) {


        UHAsyncTask task = new UHAsyncTask(new HashMap<String, Object>(), new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String string) {

                try {

                    if (string == null || string.isEmpty()) {
                        Log.e(UserHook.TAG,"fetch page names server response was null");
                        return;
                    }

                    JSONObject json = new JSONObject(string);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        JSONArray data = json.getJSONArray("data");

                        List<UHPage> pages = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            pages.add(new UHPage(item));

                        }

                        if (listener != null) {
                            listener.onSuccess(pages);
                        }

                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error for page name fetch");
                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error fetching page names", e);
                }

            }
        });


        task.execute(UserHook.UH_API_URL + UH_PATH_PAGES);

    }


    public void fetchMessageTemplates() {


        UHAsyncTask task = new UHAsyncTask(new HashMap<String, Object>(), new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String string) {

                if (string == null || string.isEmpty()) {
                    Log.e(UserHook.TAG,"fetch message templates server response was null");
                    return;
                }

                try {

                    JSONObject json = new JSONObject(string);

                    if (json != null && json.has("templates")) {

                        JSONObject data = json.getJSONObject("templates");

                        Iterator<String> iter = data.keys();
                        while (iter.hasNext()) {
                            String name = iter.next();
                            String template = data.getString(name);

                            UHMessageTemplate.getInstance().addToCache(name, template);

                        }



                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error for page name fetch");
                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error fetching page names", e);
                }

            }
        });


        task.execute(UserHook.UH_HOST_URL + UH_PATH_MESSAGE_TEMPLATES);

    }

    public void fetchHookpoint(final UserHook.UHHookPointFetchListener listener) {

        if (UHUser.getUserId() == null) {
            Log.e(UserHook.TAG, "cannot fetch hookpoint, user id is null");
            return;
        }

        // only allow one fetch at a time
        if (fetchingHookpoints) {
            return;
        }

        fetchingHookpoints = true;

        Map<String, Object> params = new HashMap<>();
        params.put("user", UHUser.getUserId());

        UHAsyncTask task = new UHAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                fetchingHookpoints = false;

                if (result == null || result.isEmpty()) {
                    Log.e(UserHook.TAG,"fetch hook points server response was null");
                    return;
                }

                try {

                    JSONObject json = new JSONObject(result);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        JSONObject data = json.getJSONObject("data");

                        UHHookPoint hookPoint = null;

                        if (data.has("hookpoint") && !data.isNull("hookpoint")) {
                            JSONObject hookpointData = data.getJSONObject("hookpoint");

                            hookPoint = UHHookPoint.createWithData(hookpointData);

                        }

                        if (listener != null) {
                            listener.onSuccess(hookPoint);
                        }

                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error for fetch hookpoint");

                        if (listener != null) {
                            listener.onError();
                        }
                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error fetching hookpoint", e);

                    if (listener != null) {
                        listener.onError();
                    }
                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_HOOK_POINT_FETCH);

    }

    public void trackHookpointAction(UHHookPoint hookPoint, final String action) {

        Map<String, Object> params = new HashMap<>();
        params.put("user", UHUser.getUserId());
        params.put("hookpoint", hookPoint.getId());
        params.put("action", action);

        if (UHUser.getUserId() == null) {
            Log.e(UserHook.TAG, "cannot track hookpoint, user id is null");
            return;
        }


        UHPostAsyncTask task = new UHPostAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                try {

                    if (result == null || result.isEmpty()) {
                        Log.e(UserHook.TAG,"track hook point action server response was null");
                        return;
                    }

                    JSONObject json = new JSONObject(result);

                    if (json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        Log.i(UserHook.TAG, "hookpoint tracked: " + action);


                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error for track hookpoint");

                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error tracking hookpoint", e);

                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_HOOK_POINT_TRACK);


    }


    public void registerPushToken(final String deviceToken, int retryCount) {

        Map<String, Object> params = new HashMap<>();
        if (UHUser.getUserId() != null) {
            params.put("user", UHUser.getUserId());
        }
        else {

            // we need a userId to register for push messages
            if (retryCount < 2) {

                final int newRetryCount = retryCount++;

                // wait 5 seconds and then try to register
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registerPushToken(deviceToken, newRetryCount);
                    }
                }, 5000);

            }

            return;
        }

        params.put("os", "android");
        params.put("sdk", UserHook.UH_SDK_VERSION);
        params.put("token", deviceToken);
        params.put("timezone_offset", UHDeviceInfo.getTimezoneOffset());

        if (UHUser.getUserId() == null) {
            Log.e(UserHook.TAG, "cannot register push token if user is null");
            return;
        }


        UHPostAsyncTask task = new UHPostAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                if (result == null) {
                    Log.e(UserHook.TAG, "error registering push token, response was null");
                    return;
                }

                try {

                    JSONObject json = new JSONObject(result);

                    if (json.has("status") && json.getString("status").equalsIgnoreCase("success")
                            && json.getJSONObject("data") != null && json.getJSONObject("data").getBoolean("registered")) {

                        Log.i(UserHook.TAG, "push token registered");

                    } else {
                        Log.e(UserHook.TAG, "userhook response status was error for register push token");

                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error registering push token", e);

                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_PUSH_REGISTER);


    }

    public void trackPushOpen(Map<String,String> data) {

        Map<String, Object> params = new HashMap<>();
        if (UHUser.getUserId() != null) {
            params.put("user", UHUser.getUserId());
        }
        params.put("os", "android");
        params.put("sdk", UserHook.UH_SDK_VERSION);

        String payload = UHJsonUtils.toJSON(data).toString();
        params.put("payload", payload);

        if (UHUser.getUserId() == null) {
            Log.e(UserHook.TAG, "cannot track push token if user is null");
            return;
        }


        UHPostAsyncTask task = new UHPostAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                try {

                    if (result == null || result.isEmpty()) {
                        Log.e(UserHook.TAG,"track push open server response was null");
                        return;
                    }

                    JSONObject json = new JSONObject(result);

                    if (json.has("status") && json.getString("status").equalsIgnoreCase("success")
                            && json.getJSONObject("data") != null && json.getJSONObject("data").getBoolean("tracked")) {

                        Log.i(UserHook.TAG, "push open tracked");

                    } else {
                        Log.e(UserHook.TAG, "user hook response status was error for track push open");

                    }

                } catch (Exception e) {
                    Log.e(UserHook.TAG, "error tracking push open", e);

                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_PUSH_OPEN);


    }


    public interface UHArrayListener<T> {
        void onSuccess(List<T> items);
    }


}
