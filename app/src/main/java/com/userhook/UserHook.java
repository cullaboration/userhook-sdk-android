/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class UserHook {

    static Context applicationContext;
    static String appId;
    static String apiKey;

    static boolean hasNewFeedback = false;
    protected static UHFeedbackListener feedbackListener;

    static UHActivityLifecycle activityLifecycle;

    public static final String UH_API_URL = "https://api.userhook.com";
    public static final String UH_HOST_URL = "https://formhost.userhook.com";
    public static final String UH_URL_SCHEMA = "uh://";
    public static final String UH_PROTOCOL = "userhook";
    public static final int UH_API_VERSION = 1;
    public static final String UH_SDK_VERSION = "0.9";

    public static final String UH_CUSTOM_FIELDS = "customFields";


    private static final String UH_HOOK_POINT_DISPLAY_ACTION = "display";
    private static final String UH_HOOK_POINT_INTERACT_ACTION = "interact";

    protected static UHHookPointActionListener actionListener;


    protected static int customPromptLayout = 0;

    public static void initialize(Application application, String userHookAppId, String userHookApiKey, boolean fetchHookpointsOnSessionStart) {

        applicationContext = application;
        appId = userHookAppId;
        apiKey = userHookApiKey;

        // add the activity lifecycle listener
        activityLifecycle = new UHActivityLifecycle(fetchHookpointsOnSessionStart);
        application.registerActivityLifecycleCallbacks(activityLifecycle);

    }


    public static void setHookPointActionListener(UHHookPointActionListener listener) {
        actionListener = listener;
    }

    public static void actionReceived(Activity activity, Map<String, Object> payload) {
        if (actionListener != null) {
            actionListener.onAction(activity, payload);
        }
    }

    public static void updateSessionData(Map<String,Object> data, UHSuccessListener listener) {
        UHOperation operation = new UHOperation();
        operation.updateSessionData(data, listener);
    }

    public static void updateCustomFields(Map<String,Object> data, UHSuccessListener listener) {

        UHOperation operation = new UHOperation();
        Map<String,Object> customFieldData = new HashMap<>();
        for (String key : data.keySet()) {
            customFieldData.put("custom_fields."+key, data.get(key));
        }
        operation.updateSessionData(customFieldData, listener);
    }

    public static void updatePurchasedItem(String sku, Number price, UHSuccessListener listener) {

        UHOperation operation = new UHOperation();
        Map<String,Object> data = new HashMap<>();
        data.put("purchases", sku);
        data.put("purchases_amount", price);
        operation.updateSessionData(data, listener);
    }

    public static int getCustomPromptLayout() {
        return customPromptLayout;
    }

    public static void setCustomPromptLayout(int customPromptLayoutId) {
        customPromptLayout = customPromptLayoutId;
    }

    public static void setFeedbackListener(UHFeedbackListener listener) {
        feedbackListener = listener;
    }

    public static boolean hasNewFeedback() {
        return hasNewFeedback;
    }

    public static void setHasNewFeedback(boolean value) {
        hasNewFeedback = value;
        if (value && feedbackListener != null) {
            feedbackListener.onNewFeedback(activityLifecycle.getCurrentActivity());
        }
    }

    public static void fetchHookPoint(UHHookPointFetchListener listener) {

        UHOperation operation = new UHOperation();
        operation.fetchHookpoint(listener);

    }

    public static void fetchPageNames(UHOperation.UHArrayListener listener) {
        UHOperation operation = new UHOperation();
        operation.fetchPageNames(listener);
    }

    public static void trackHookPointDisplay(UHHookPoint hookPoint) {

        UHOperation operation = new UHOperation();
        operation.trackHookpointAction(hookPoint, UH_HOOK_POINT_DISPLAY_ACTION);
    }

    public static void trackHookPointInteraction(UHHookPoint hookPoint) {

        UHOperation operation = new UHOperation();
        operation.trackHookpointAction(hookPoint, UH_HOOK_POINT_INTERACT_ACTION);
    }


    public static void markAsRated() {
        // mark that the user has "rated" this app
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("rated",true);
        UserHook.updateSessionData(params, null);
    }

    public static int getResourceId(String name, String type) {
        int id = applicationContext.getResources().getIdentifier(name, type, applicationContext.getPackageName());
        return id;
    }

    public interface UHHookPointActionListener {

        void onAction (Activity activity, Map<String,Object> payload);
    }


    public interface UHHookPointFetchListener {

        void onSuccess(UHHookPoint hookPoint);

        void onError();
    }


    public interface UHSuccessListener {
        void onSuccess();
    }

    public interface UHFeedbackListener {
        void onNewFeedback(Activity activity);
    }

}
