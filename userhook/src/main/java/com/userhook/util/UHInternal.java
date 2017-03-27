package com.userhook.util;

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

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMeta;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.model.UHPage;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.view.UHMessageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

public class UHInternal {


    private static UHInternal instance;

    private String appId;
    private String apiKey;

    private boolean hasNewFeedback = false;
    private UserHook.UHFeedbackListener feedbackListener;
    private UserHook.UHPushMessageListener pushMessageListener;

    private UHActivityLifecycle activityLifecycle;


    private UserHook.UHPayloadListener payloadListener;

    // resource id of icon to use for push notification
    private int pushNotificationIcon;


    // application settings for feedback page
    private String feedbackScreenTitle = "Feedback";
    private Map<String, String> feedbackCustomFields;

    private UHUserProvider userProvider;
    private UHDeviceInfoProvider deviceInfoProvider;


    private UHOperation.Factory operationFactory = new UHOperation.Factory() {
        @Override
        public UHOperation build() {
            return new UHOperation();
        }
    };

    private UHMessageView.Factory messageViewFactory = new UHMessageView.Factory() {
        @Override
        public UHMessageView createMessageView(Context context, UHMessageMeta meta) {
            return new UHMessageView(context, meta);
        }
    };


    private UHInternal() {

    }

    public static UHInternal getInstance() {
        if (instance == null) {
            instance = new UHInternal();
        }

        return instance;
    }

    /**
     * only used for unit testing
     *
     * @param uh
     */
    public static void setInstance(UHInternal uh) {
        instance = uh;
    }

    public void setUserProvider(UHUserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public void setDeviceInfoProvider(UHDeviceInfoProvider deviceInfoProvider) {
        this.deviceInfoProvider = deviceInfoProvider;
    }

    public void setOperationFactory(UHOperation.Factory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public void setMessageViewFactory(UHMessageView.Factory messageViewFactory) {
        this.messageViewFactory = messageViewFactory;
    }

    public void initialize(Application application, String userHookAppId, String userHookApiKey, boolean fetchHookpointsOnSessionStart) {


        appId = userHookAppId;
        apiKey = userHookApiKey;

        // add the activity lifecycle listener
        activityLifecycle = new UHActivityLifecycle(fetchHookpointsOnSessionStart);
        if (application != null) {
            application.registerActivityLifecycleCallbacks(instance.activityLifecycle);
        }

        // attach a default user provider
        userProvider = new UHUser(application);

        // attach a default device info provider
        deviceInfoProvider = new UHDeviceInfo(application);

        Log.d(UserHook.TAG, "user hook initialized for app: " + userHookApiKey);

    }

    public void setActivityLifecycle(UHActivityLifecycle activityLifecycle) {
        this.activityLifecycle = activityLifecycle;
    }

    public UHUserProvider getUser() {
        return userProvider;
    }

    public UHDeviceInfoProvider getDevice() {
        return deviceInfoProvider;
    }

    // helper method useful for testing
    public void setUHUserProvider(UHUserProvider user) {
        userProvider = user;
    }

    public void setUHDeviceProvider(UHDeviceInfoProvider device) {
        deviceInfoProvider = device;
    }

    public void setPayloadListener(UserHook.UHPayloadListener listener) {
        payloadListener = listener;
    }

    public void actionReceived(Activity activity, Map<String, Object> payload) {
        if (payloadListener != null && payload != null) {
            payloadListener.onAction(activity, payload);
        }
    }

    public void updateSessionData(Map<String, Object> data, UserHook.UHSuccessListener listener) {
        UHOperation operation = operationFactory.build();
        operation.updateSessionData(data, listener);
    }

    public void updateCustomFields(Map<String, Object> data, UserHook.UHSuccessListener listener) {

        UHOperation operation = operationFactory.build();

        Map<String, Object> customFieldData = new HashMap<>();
        for (String key : data.keySet()) {
            customFieldData.put("custom_fields." + key, data.get(key));
        }
        operation.updateSessionData(customFieldData, listener);
    }

    public void updatePurchasedItem(String sku, Number price, UserHook.UHSuccessListener listener) {

        UHOperation operation = instance.operationFactory.build();

        Map<String, Object> data = new HashMap<>();
        data.put("purchases", sku);
        data.put("purchases_amount", price);
        operation.updateSessionData(data, listener);
    }

    public String getAppId() {
        return instance.appId;
    }

    public String getApiKey() {
        return instance.apiKey;
    }

    public void setPushNotificationIcon(int pushNotificationIconId) {
        pushNotificationIcon = pushNotificationIconId;
    }

    public void setFeedbackListener(UserHook.UHFeedbackListener listener) {
        feedbackListener = listener;
    }

    public  boolean hasNewFeedback() {
        return hasNewFeedback;
    }

    public void setHasNewFeedback(boolean value) {
        hasNewFeedback = value;

        if (value && feedbackListener != null && activityLifecycle.isForeground()) {
            feedbackListener.onNewFeedback(instance.activityLifecycle.getCurrentActivity());
        }
    }

    public void fetchHookPoint(String event, UserHook.UHHookPointFetchListener listener) {

        UHOperation operation = instance.operationFactory.build();
        operation.fetchHookpoint(event, listener);

    }

    public void fetchPageNames(UHOperation.UHArrayListener listener) {
        UHOperation operation = instance.operationFactory.build();
        operation.fetchPageNames(listener);
    }

    public void trackHookPointDisplay(UHHookPoint hookPoint) {

        UHOperation operation = instance.operationFactory.build();
        operation.trackHookpointAction(hookPoint, UserHook.UH_HOOK_POINT_DISPLAY_ACTION);
    }

    public void trackHookPointInteraction(UHHookPoint hookPoint) {

        UHOperation operation = instance.operationFactory.build();
        operation.trackHookpointAction(hookPoint, UserHook.UH_HOOK_POINT_INTERACT_ACTION);
    }


    public void markAsRated() {
        // mark that the user has "rated" this app
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("rated", true);
        UserHook.updateSessionData(params, null);
    }

    public void registerPushToken(String token) {
        UHOperation operation = instance.operationFactory.build();
        operation.registerPushToken(token, 1);
    }

    public void trackPushOpen(Map<String, String> data) {
        UHOperation operation = instance.operationFactory.build();
        operation.trackPushOpen(data);
    }

    public void setPushMessageListener(UserHook.UHPushMessageListener listener) {
        pushMessageListener = listener;
    }

    public UHActivityLifecycle getActivityLifecycle() {
        return activityLifecycle;
    }


    public void handlePushPayload(Activity activity, String payloadString) {

        try {
            JSONObject json = new JSONObject(payloadString);
            Map<String, Object> payload = UHJsonUtils.toMap(json);

            actionReceived(activity, payload);

        } catch (JSONException je) {
            Log.e(UserHook.TAG, "error handling push payload", je);
        }

    }

    public Notification handlePushMessage(Context context, Map<String, String> data) {

        String message = data.get("message");
        String title = "";

        if (data.containsKey("title") && data.get("title") != null) {
            title = data.get("title");
        } else {
            title = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        }


        boolean hasNewFeedback = false;

        Map<String, Object> payload = new HashMap<>();
        if (data.containsKey("payload")) {
            try {
                JSONObject json = new JSONObject(data.get("payload"));
                payload = UHJsonUtils.toMap(json);

                // check if this is a feedback reply
                if (json.has("new_feedback") && json.getBoolean("new_feedback")) {
                    UserHook.setHasNewFeedback(true);
                    hasNewFeedback = true;
                } else {
                    UserHook.setHasNewFeedback(false);
                }


            } catch (JSONException e) {
                Log.e(UserHook.TAG, "error parsing push notification payload");
            }
        }


        // message received
        Intent intent;

        if (pushMessageListener != null) {
            intent = pushMessageListener.onPushMessage(payload);
        } else {
            // default to opening the main activity
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        }

        // convert data to a try Map<String,String> since it will come in as an ArrayMap
        if (data instanceof ArrayMap) {
            HashMap<String, String> hashMap = new HashMap<>();
            for (String key : data.keySet()) {
                hashMap.put(key, data.get(key));
            }
            data = hashMap;
        }

        intent.putExtra(UserHook.UH_PUSH_DATA, (Serializable) data);
        intent.putExtra(UserHook.UH_PUSH_TRACKED, false);
        intent.putExtra(UserHook.UH_PUSH_FEEDBACK, hasNewFeedback);
        if (payload.size() > 0) {
            intent.putExtra(UserHook.UH_PUSH_PAYLOAD, data.get("payload"));
        }


        //PendingIntent.FLAG_UPDATE_CURRENT is required to pass along our Intent Extras
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            int pushIcon = appInfo.icon;
            // check for a custom push icon
            if (pushNotificationIcon > 0) {
                pushIcon = pushNotificationIcon;
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(pushIcon)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            // use default sound
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

            return notificationBuilder.build();

        } catch (Exception e) {
            Log.e(UserHook.TAG, "error create push notification", e);
            return null;
        }

    }


    public int getResourceId(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public String getString(Context context, int id) {
        return context.getResources().getString(id);
    }


    public void rateThisApp() {

        startActivityToRate();

        // tell User Hook that this user has rated this app
        markAsRated();
    }

    private void startActivityToRate() {

        Context context = instance.activityLifecycle.getCurrentActivity();

        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        if (goToMarket.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(goToMarket);
            return;
        }

        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
    }

    public void setFeedbackScreenTitle(String title) {
        feedbackScreenTitle = title;
    }

    public void setFeedbackCustomFields(Map<String, String> customFields) {
        feedbackCustomFields = customFields;
    }

    public void showFeedback() {

        Intent intent = new Intent(instance.activityLifecycle.getCurrentActivity(), UHHostedPageActivity.class);
        intent.putExtra(UHHostedPageActivity.TYPE_FEEDBACK, feedbackScreenTitle);

        if (feedbackCustomFields != null && feedbackCustomFields.size() > 0) {
            Bundle bundle = new Bundle();
            for (String key : instance.feedbackCustomFields.keySet()) {
                bundle.putString(key, instance.feedbackCustomFields.get(key));
            }
            intent.putExtra(UserHook.UH_CUSTOM_FIELDS, bundle);
        }

        activityLifecycle.getCurrentActivity().startActivity(intent);
    }

    public void showSurvey(String surveyId, String surveyTitle, UHHookPoint hookPoint) {

        Intent intent = new Intent(instance.activityLifecycle.getCurrentActivity(), UHHostedPageActivity.class);

        intent.putExtra(UHHostedPageActivity.TYPE_SURVEY, surveyId);
        intent.putExtra(UHHostedPageActivity.SURVEY_TITLE, surveyTitle);
        if (hookPoint != null) {
            intent.putExtra(UHHostedPageActivity.HOOKPOINT_ID, hookPoint.getId());
        }

        activityLifecycle.getCurrentActivity().startActivity(intent);
    }

    public void displayPrompt(String message, UHMessageMetaButton button1, UHMessageMetaButton button2) {

        final UHMessageMeta meta = new UHMessageMeta();
        meta.setBody(message);

        if (button1 != null && button2 != null) {
            meta.setDisplayType(UHMessageMeta.TYPE_TWO_BUTTONS);
        } else if (button1 != null) {
            meta.setDisplayType(UHMessageMeta.TYPE_ONE_BUTTON);
        } else {
            meta.setDisplayType(UHMessageMeta.TYPE_NO_BUTTONS);
        }

        meta.setButton1(button1);
        meta.setButton2(button2);

        // add view to screen
        if (UHMessageView.canDisplay() && instance.activityLifecycle.getCurrentActivity() != null) {

            activityLifecycle.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UHMessageView view = instance.messageViewFactory.createMessageView(activityLifecycle.getCurrentActivity(), meta);

                    ViewGroup rootView = (ViewGroup) activityLifecycle.getCurrentActivity().findViewById(android.R.id.content);
                    rootView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    view.showDialog();
                }
            });

        }
    }


    public void displayStaticPage(String slug, String title) {

        UHPage page = new UHPage();
        page.setSlug(slug);
        page.setName(title);

        Intent intent = new Intent(instance.activityLifecycle.getCurrentActivity(), UHHostedPageActivity.class);
        intent.putExtra(UHHostedPageActivity.TYPE_PAGE, page);
        activityLifecycle.getCurrentActivity().startActivity(intent);

    }
}
