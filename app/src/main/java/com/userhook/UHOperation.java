/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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


    public void createSession(UserHook.UHSuccessListener listener) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        String date = format.format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("sessions", "1");
        data.put("last_launch", date);

        updateSessionData(data, listener);

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


        UHPostAsyncTask task = new UHPostAsyncTask(data, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String string) {

                try {
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
                    }
                    else {
                        Log.e("userhook","userhook response status was error");
                    }

                } catch (Exception e) {
                    Log.e("userhook", "error updating session data", e);
                }

            }
        });


        task.execute(UserHook.UH_API_URL + UH_PATH_SESSION);

    }

    public void fetchPageNames(final UHArrayListener listener) {


        UHAsyncTask task = new UHAsyncTask(new HashMap<String,Object>(), new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String string) {

                try {

                    JSONObject json = new JSONObject(string);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        JSONArray data = json.getJSONArray("data");

                        List<UHPage> pages = new ArrayList<>();

                        for (int i=0;i<data.length();i++) {
                            JSONObject item = data.getJSONObject(i);

                            String slug = item.getString("slug");
                            String name = item.getString("name");

                            pages.add(new UHPage(slug, name));

                        }

                        if (listener != null) {
                            listener.onSuccess(pages);
                        }

                    }
                    else {
                        Log.e("userhook","userhook response status was error for page name fetch");
                    }

                } catch (Exception e) {
                    Log.e("userhook", "error fetching page names", e);
                }

            }
        });


        task.execute(UserHook.UH_API_URL + UH_PATH_PAGES);

    }

    public void fetchHookpoint(final UserHook.UHHookPointFetchListener listener) {

        if (UHUser.getUserId() == null) {
            Log.e("userhook","cannot fetch hookpoint, user id is null");
            return;
        }

        Map<String,Object> params = new HashMap<>();
        params.put("user", UHUser.getUserId());

        UHAsyncTask task = new UHAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject json = new JSONObject(result);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        JSONObject data = json.getJSONObject("data");

                        UHHookPoint hookPoint = null;

                        if (data.has("hookpoint") && !data.isNull("hookpoint")) {
                            JSONObject hookpointData = data.getJSONObject("hookpoint");
                            Map<String, Object> mapData = UHJsonUtils.toMap(hookpointData);

                            hookPoint = UHHookPoint.createWithData(mapData);

                        }

                        if (listener != null) {
                            listener.onSuccess(hookPoint);
                        }

                    }
                    else {
                        Log.e("userhook","userhook response status was error for fetch hookpoint");

                        if (listener != null) {
                            listener.onError();
                        }
                    }

                } catch (Exception e) {
                    Log.e("userhook", "error fetching hookpoint", e);

                    if (listener!=null) {
                        listener.onError();
                    }
                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_HOOK_POINT_FETCH);

    }

    public void trackHookpointAction(UHHookPoint hookPoint, final String action) {

        Map<String,Object> params = new HashMap<>();
        params.put("user", UHUser.getUserId());
        params.put("hookpoint", hookPoint.getId());
        params.put("action", action);

        if (UHUser.getUserId() == null) {
            Log.e("userhook","cannot track hookpoint, user id is null");
            return;
        }


        UHPostAsyncTask task = new UHPostAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject json = new JSONObject(result);

                    if (json != null && json.has("status") && json.getString("status").equalsIgnoreCase("success")) {

                        Log.e("uh","hookpoint tracked: " + action);


                    }
                    else {
                        Log.e("userhook","userhook response status was error for track hookpoint");

                    }

                } catch (Exception e) {
                    Log.e("userhook", "error tracking hookpoint", e);

                }

            }
        });

        task.execute(UserHook.UH_API_URL + UH_PATH_HOOK_POINT_TRACK);


    }



    public interface UHArrayListener<T> {
        void onSuccess(List<T> items);
    }


}
