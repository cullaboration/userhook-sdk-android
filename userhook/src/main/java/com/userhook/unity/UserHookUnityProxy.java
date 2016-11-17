/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.unity;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.model.UHPage;
import com.userhook.util.UHJsonUtils;
import com.userhook.util.UHOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserHookUnityProxy {

    public static void init() {

        UserHook.setPayloadListener(new UserHook.UHPayloadListener() {
            @Override
            public void onAction(Activity activity, Map<String, Object> payload) {

                String payloadJsonString = "";
                try {
                    if (payload != null) {
                        JSONObject jsonObject = new JSONObject();
                        for (String key : payload.keySet()) {
                            jsonObject.put(key, payload.get(key));
                        }

                        payloadJsonString = jsonObject.toString();
                    }
                }
                catch (JSONException je) {
                    Log.e(UserHook.TAG, "error converting payload to json", je);
                }

                UnityPlayer.UnitySendMessage("UserHook","handlePayload", payloadJsonString);
            }
        });

    }

    public static void showFeedback() {
        UserHook.showFeedback();
    }

    public static void setFeedbackScreenTitle(String title) {
        UserHook.setFeedbackScreenTitle(title);
    }

    public static void showFeedbackPrompt(final String message, final String positiveTitle, final String negativeTitle) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                UserHook.showFeedbackPrompt(message, positiveTitle, negativeTitle);
            }
        });

    }

    public static void rateThisApp() {
        UserHook.rateThisApp();
    }

    public static void showRatingPrompt(final String message, final String positiveTitle, final String negativeTitle) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                UserHook.showRatingPrompt(message, positiveTitle, negativeTitle);
            }
        });
    }

    public static void fetchHookPoints() {

        UserHook.fetchHookPoint(new UserHook.UHHookPointFetchListener() {

            @Override
            public void onSuccess(UHHookPoint hookPoint) {
                if (hookPoint != null) {
                    hookPoint.execute(UnityPlayer.currentActivity);
                }
            }

            @Override
            public void onError() {
                Log.e(UserHook.TAG, "error fetching hookpoints");
            }
        });
    }

    public static void setFeedbackCustomFields(String fields) {

        try {

            JSONObject json = new JSONObject(fields);

            if (json != null) {
                Map<String,String> customFields = toMap(json);

                UserHook.setFeedbackCustomFields(customFields);

            }

        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error setting feedback custom fields", je);
        }
    }

    public static void fetchPageNames() {

        UserHook.fetchPageNames(new UHOperation.UHArrayListener<UHPage>() {
            @Override
            public void onSuccess(List<UHPage> items) {

                try {

                    JSONArray pages = new JSONArray();

                    for (UHPage page : items) {

                        JSONObject json = new JSONObject();
                        json.put("slug", page.getSlug());
                        json.put("name", page.getName());
                        pages.put(json);

                    }

                    String jsonString = pages.toString();

                    UnityPlayer.UnitySendMessage("UserHook","handleFetchedPageNames", jsonString);
                }
                catch (JSONException je) {
                    Log.e(UserHook.TAG,"error fetching page names", je);
                }
            }
        });

    }

    public static void displayStaticPage(final String slug, final String title) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                UserHook.displayStaticPage(slug, title);
            }
        });
    }

    public static void displayPrompt(final String message, final String button1, final String button2) {

        UHMessageMetaButton btn1 = null;
        UHMessageMetaButton btn2 = null;

        try {
            if (button1 != null) {
                JSONObject json1 = new JSONObject(button1);
                btn1 = UHMessageMetaButton.fromJSON(json1);

                if (json1.has("onClickGameObject") && json1.has("onClickFunction")) {
                    btn1.setOnClickListener(new UHMessageMetaButton.OnClickListener() {
                        @Override
                        public void onClick() {
                            UnityPlayer.UnitySendMessage("UserHook","handleResponse", button1);
                        }
                    });
                }
            }

            if (button2 != null) {

                JSONObject json2 = new JSONObject(button2);
                btn2 = UHMessageMetaButton.fromJSON(json2);

                if (json2.has("onClickGameObject") && json2.has("onClickFunction")) {
                    btn2.setOnClickListener(new UHMessageMetaButton.OnClickListener() {
                        @Override
                        public void onClick() {
                            UnityPlayer.UnitySendMessage("UserHook","handleResponse", button2);
                        }
                    });
                }

            }

            final UHMessageMetaButton finalBtn1 = btn1;
            final UHMessageMetaButton finalBtn2 = btn2;


            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    UserHook.displayPrompt(message, finalBtn1, finalBtn2);
                }
            });



        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error displaying prompt with buttons", je);
        }
    }

    public static void updateCustomFields(String fields, final String handler) {

        if (fields != null && !fields.isEmpty()) {

            try {
                JSONObject json = new JSONObject(fields);

                Map<String,Object> fieldsMap = UHJsonUtils.toMap(json);

                UserHook.updateCustomFields(fieldsMap, new UserHook.UHSuccessListener() {
                    @Override
                    public void onSuccess() {

                        UnityPlayer.UnitySendMessage("UserHook","handleResponse", handler);
                    }
                });
            }
            catch (JSONException je) {
                Log.e(UserHook.TAG, "error updating custom fields", je);
            }
        }
    }

    public static void updatePurchasedItem(String sku, Double price, final String handler) {

        if (sku != null && !sku.isEmpty()) {

            UserHook.updatePurchasedItem(sku, price, new UserHook.UHSuccessListener() {
                @Override
                public void onSuccess() {
                    UnityPlayer.UnitySendMessage("UserHook","handleResponse", handler);
                }
            });
        }
    }


    protected static Map<String,String> toMap(JSONObject object) throws JSONException{

        Map<String, String> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, object.getString(key));
        }
        return map;
    }

}
