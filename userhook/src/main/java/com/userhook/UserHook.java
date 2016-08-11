/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.ViewGroup;

import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMeta;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.util.UHActivityLifecycle;
import com.userhook.util.UHJsonUtils;
import com.userhook.util.UHOperation;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.view.UHMessageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserHook {

    public static final String TAG = "uh";

    static Context applicationContext;
    static String appId;
    static String apiKey;

    static boolean hasNewFeedback = false;
    protected static UHFeedbackListener feedbackListener;
    protected static UHPushMessageListener pushMessageListener;

    static UHActivityLifecycle activityLifecycle;

    public static final String UH_API_URL = "https://api.userhook.com";
    public static final String UH_HOST_URL = "https://formhost.userhook.com";

    public static final String UH_URL_SCHEMA = "uh://";
    public static final int UH_API_VERSION = 1;
    public static final String UH_SDK_VERSION = "1.2.1";

    public static final String UH_CUSTOM_FIELDS = "customFields";

    public static final String UH_PUSH_DATA = "uh_push_data";
    public static final String UH_PUSH_PAYLOAD = "uh_push_payload";
    public static final String UH_PUSH_TRACKED = "uh_push_tracked";


    private static final String UH_HOOK_POINT_DISPLAY_ACTION = "display";
    private static final String UH_HOOK_POINT_INTERACT_ACTION = "interact";

    protected static UHPayloadListener payloadListener;

    // resource id of icon to use for push notification
    protected static int pushNotificationIcon;


    protected static int customPromptLayout = 0;

    // user to determine if push message is from User Hook
    private static final String PUSH_SOURCE_PARAM = "source";
    private static final String PUSH_SOURCE_VALUE = "userhook";


    // application settings for feedback page
    private static String feedbackScreenTitle = "Feedback";
    private static Map<String,String> feedbackCustomFields;

    public static void initialize(Application application, String userHookAppId, String userHookApiKey, boolean fetchHookpointsOnSessionStart) {

        applicationContext = application;
        appId = userHookAppId;
        apiKey = userHookApiKey;

        // add the activity lifecycle listener
        activityLifecycle = new UHActivityLifecycle(fetchHookpointsOnSessionStart);
        application.registerActivityLifecycleCallbacks(activityLifecycle);

    }


    public static void setPayloadListener(UHPayloadListener listener) {
        payloadListener = listener;
    }

    public static void actionReceived(Activity activity, Map<String, Object> payload) {
        if (payloadListener != null && payload != null) {
            payloadListener.onAction(activity, payload);
        }
    }

    public static void updateSessionData(Map<String, Object> data, UHSuccessListener listener) {
        UHOperation operation = new UHOperation();
        operation.updateSessionData(data, listener);
    }

    public static void updateCustomFields(Map<String, Object> data, UHSuccessListener listener) {

        UHOperation operation = new UHOperation();
        Map<String, Object> customFieldData = new HashMap<>();
        for (String key : data.keySet()) {
            customFieldData.put("custom_fields." + key, data.get(key));
        }
        operation.updateSessionData(customFieldData, listener);
    }

    public static void updatePurchasedItem(String sku, Number price, UHSuccessListener listener) {

        UHOperation operation = new UHOperation();
        Map<String, Object> data = new HashMap<>();
        data.put("purchases", sku);
        data.put("purchases_amount", price);
        operation.updateSessionData(data, listener);
    }

    public static String getAppId() {
        return appId;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static int getCustomPromptLayout() {
        return customPromptLayout;
    }

    public static void setCustomPromptLayout(int customPromptLayoutId) {
        customPromptLayout = customPromptLayoutId;
    }

    public static void setPushNotificationIcon(int pushNotificationIconId) {
        pushNotificationIcon = pushNotificationIconId;
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("rated", true);
        UserHook.updateSessionData(params, null);
    }

    public static void registerPushToken(String token) {
        UHOperation operation = new UHOperation();
        operation.registerPushToken(token, 1);
    }

    public static void trackPushOpen(Map<String,String> data) {
        UHOperation operation = new UHOperation();
        operation.trackPushOpen(data);
    }

    public static void setPushMessageListener(UHPushMessageListener listener) {
        pushMessageListener = listener;
    }

    public static UHActivityLifecycle getActivityLifecycle() {
        return activityLifecycle;
    }

    /**
     * Checks a push notification to see if it came from User Hook or another service
     *
     * @param data
     * @return boolean if push message originated from User Hook
     */
    public static boolean isPushFromUserHook(Map<String,String> data) {
        return data != null && data.containsKey(PUSH_SOURCE_PARAM) && data.get(PUSH_SOURCE_PARAM).equals(PUSH_SOURCE_VALUE);
    }

    public static void handlePushPayload(Activity activity, String payloadString) {

        try {
            JSONObject json = new JSONObject(payloadString);
            Map<String,Object> payload = UHJsonUtils.toMap(json);

            if (payloadListener != null) {
                payloadListener.onAction(activity, payload);
            }
        }
        catch (JSONException je) {
            Log.e(UserHook.TAG, "error handling push payload", je);
        }

    }

    public static Notification handlePushMessage(Map<String,String> data) {

        String message = data.get("message");
        String title = "";

        if (data.containsKey("title") && data.get("title") != null) {
            title = data.get("title");
        } else {
            title = applicationContext.getApplicationInfo().loadLabel(applicationContext.getPackageManager()).toString();
        }


        Map<String, Object> payload = new HashMap<>();
        if (data.containsKey("payload")) {
            try {
                JSONObject json = new JSONObject(data.get("payload"));
                payload = UHJsonUtils.toMap(json);

                // check if this is a feedback reply
                if (json.has("new_feedback") && json.getBoolean("new_feedback")) {
                    UserHook.setHasNewFeedback(true);
                } else {
                    UserHook.setHasNewFeedback(false);
                }


            }
            catch (JSONException e) {
                Log.e("uh","error parsing push notification payload");
            }
        }



        // message received
        Intent intent;

        if (pushMessageListener != null) {
            intent = pushMessageListener.onPushMessage(payload);
        } else {
            // default to opening the main activity
            intent = applicationContext.getPackageManager().getLaunchIntentForPackage(applicationContext.getPackageName());
        }

        // convert data to a try Map<String,String> since it will come in as an ArrayMap
        if (data instanceof ArrayMap) {
            HashMap<String,String> hashMap = new HashMap<>();
            for (String key : data.keySet()) {
                hashMap.put(key, data.get(key));
            }
            data = hashMap;
        }

        intent.putExtra(UserHook.UH_PUSH_DATA, (Serializable)data);
        intent.putExtra(UserHook.UH_PUSH_TRACKED, false);
        if (payload.size() > 0) {
            intent.putExtra(UserHook.UH_PUSH_PAYLOAD, data.get("payload"));
        }


        //PendingIntent.FLAG_UPDATE_CURRENT is required to pass along our Intent Extras
        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            ApplicationInfo appInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);

            int pushIcon = appInfo.icon;
            // check for a custom push icon
            if (pushNotificationIcon > 0) {
                pushIcon = pushNotificationIcon;
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                    .setSmallIcon(pushIcon)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            // use default sound
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

            return notificationBuilder.build();

        } catch (Exception e) {
            Log.e("uh", "error create push notification", e);
            return null;
        }

    }


    public static int getResourceId(String name, String type) {
        return applicationContext.getResources().getIdentifier(name, type, applicationContext.getPackageName());
    }

    public static String getString(int id) {
        return applicationContext.getResources().getString(id);
    }


    public static void rateThisApp() {

        startActivityToRate();

        // tell User Hook that this user has rated this app
        UserHook.markAsRated();
    }

    private static void startActivityToRate() {
        Uri uri = Uri.parse("market://details?id=" + applicationContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if (goToMarket.resolveActivity(applicationContext.getPackageManager()) != null) {
            activityLifecycle.getCurrentActivity().startActivity(goToMarket);
            return;
        }

        activityLifecycle.getCurrentActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.getPackageName())));
    }

    public static void setFeedbackScreenTitle(String title){
        feedbackScreenTitle = title;
    }

    public static void setFeedbackCustomFields(Map<String,String> customFields) {
        feedbackCustomFields = customFields;
    }

    public static void showFeedback() {

        Intent intent = new Intent(activityLifecycle.getCurrentActivity(), UHHostedPageActivity.class);
        intent.putExtra(UHHostedPageActivity.TYPE_FEEDBACK, feedbackScreenTitle);

        if (feedbackCustomFields != null && feedbackCustomFields.size() > 0) {
            Bundle bundle = new Bundle();
            for (String key : feedbackCustomFields.keySet()) {
                bundle.putString(key, feedbackCustomFields.get(key));
            }
            intent.putExtra(UH_CUSTOM_FIELDS, bundle);
        }

        activityLifecycle.getCurrentActivity().startActivity(intent);
    }

    public static void showSurvey(String surveyId, String surveyTitle, UHHookPoint hookPoint) {

        Intent intent = new Intent(activityLifecycle.getCurrentActivity(), UHHostedPageActivity.class);

        intent.putExtra(UHHostedPageActivity.TYPE_SURVEY, surveyId);
        intent.putExtra(UHHostedPageActivity.SURVEY_TITLE, surveyTitle);
        if (hookPoint != null) {
            intent.putExtra(UHHostedPageActivity.HOOKPOINT_ID, hookPoint.getId());
        }

        activityLifecycle.getCurrentActivity().startActivity(intent);
    }

    public static void displayPrompt(String message, UHMessageMetaButton button1, UHMessageMetaButton button2) {

        UHMessageMeta meta = new UHMessageMeta();
        meta.setBody(message);

        if (button1 != null && button2 != null) {
            meta.setDisplayType(UHMessageMeta.TYPE_TWO_BUTTONS);
        }
        else if (button1 != null) {
            meta.setDisplayType(UHMessageMeta.TYPE_ONE_BUTTON);
        }
        else {
            meta.setDisplayType(UHMessageMeta.TYPE_NO_BUTTONS);
        }

        meta.setButton1(button1);
        meta.setButton2(button2);

        // add view to screen
        UHMessageView view = new UHMessageView(activityLifecycle.getCurrentActivity(), meta);
        ViewGroup rootView = (ViewGroup) activityLifecycle.getCurrentActivity().findViewById(android.R.id.content);
        rootView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        view.showDialog();
    }

    public static void showRatingPrompt(String message, String postiveButtonTitle, String negativeButtonTitle) {

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(postiveButtonTitle);
        button1.setClick(UHMessageMeta.CLICK_RATE);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeButtonTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);

        displayPrompt(message, button1, button2);

        UserHook.markAsRated();
    }

    public static void showFeedbackPrompt(String message, String postiveButtonTitle, String negativeButtonTitle) {

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(postiveButtonTitle);
        button1.setClick(UHMessageMeta.CLICK_FEEDBACK);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeButtonTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);

        displayPrompt(message, button1, button2);

        UserHook.markAsRated();
    }

    public interface UHPayloadListener {

        void onAction(Activity activity, Map<String, Object> payload);
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

    public interface UHPushMessageListener {
        Intent onPushMessage(Map<String, Object> payload);
    }

}
