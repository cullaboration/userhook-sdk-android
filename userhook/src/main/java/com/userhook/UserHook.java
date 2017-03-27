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
import com.userhook.model.UHPage;
import com.userhook.util.UHActivityLifecycle;
import com.userhook.util.UHDeviceInfo;
import com.userhook.util.UHDeviceInfoProvider;
import com.userhook.util.UHInternal;
import com.userhook.util.UHJsonUtils;
import com.userhook.util.UHOperation;
import com.userhook.util.UHUser;
import com.userhook.util.UHUserProvider;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.view.UHMessageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserHook {

    public static final String TAG = "uh";

    public static final String UH_API_URL = "https://api.userhook.com";
    public static final String UH_HOST_URL = "https://formhost.userhook.com";

    public static final String UH_URL_SCHEMA = "uh://";
    public static final int UH_API_VERSION = 1;
    public static final String UH_SDK_VERSION = "2.0.0";

    public static final String UH_CUSTOM_FIELDS = "customFields";

    public static final String UH_PUSH_DATA = "uh_push_data";
    public static final String UH_PUSH_PAYLOAD = "uh_push_payload";
    public static final String UH_PUSH_TRACKED = "uh_push_tracked";
    public static final String UH_PUSH_FEEDBACK = "uh_push_feedback";


    public static final String UH_HOOK_POINT_DISPLAY_ACTION = "display";
    public static final String UH_HOOK_POINT_INTERACT_ACTION = "interact";

    // user to determine if push message is from User Hook
    public static final String PUSH_SOURCE_PARAM = "source";
    public static final String PUSH_SOURCE_VALUE = "userhook";


    // default width of pop-up message views
    public static int dialogWidth = 300; // in dp



    public static void initialize(Application application, String userHookAppId, String userHookApiKey, boolean fetchHookpointsOnSessionStart) {

        UHInternal.getInstance().initialize(application, userHookAppId, userHookApiKey, fetchHookpointsOnSessionStart);

    }

    public static void setPayloadListener(UHPayloadListener listener) {
        UHInternal.getInstance().setPayloadListener(listener);
    }


    public static void updateSessionData(Map<String, Object> data, UHSuccessListener listener) {

        UHInternal.getInstance().updateSessionData(data, listener);
    }

    public static void updateCustomFields(Map<String, Object> data, UHSuccessListener listener) {

        UHInternal.getInstance().updateCustomFields(data, listener);

    }

    public static void updatePurchasedItem(String sku, Number price, UHSuccessListener listener) {

        UHInternal.getInstance().updatePurchasedItem(sku, price, listener);

    }

    public static String getAppId() {
        return UHInternal.getInstance().getAppId();
    }

    public static String getApiKey() {
        return UHInternal.getInstance().getApiKey();
    }

    public static void setPushNotificationIcon(int pushNotificationIconId) {

        UHInternal.getInstance().setPushNotificationIcon(pushNotificationIconId);
    }

    public static void setFeedbackListener(UHFeedbackListener listener) {
        UHInternal.getInstance().setFeedbackListener(listener);
    }

    public static boolean hasNewFeedback() {
        return UHInternal.getInstance().hasNewFeedback();
    }

    public static void setHasNewFeedback(boolean value) {

        UHInternal.getInstance().setHasNewFeedback(value);

    }

    public static void fetchHookPoint(String event, UHHookPointFetchListener listener) {

        UHInternal.getInstance().fetchHookPoint(event, listener);

    }

    public static void fetchPageNames(UHOperation.UHArrayListener listener) {

        UHInternal.getInstance().fetchPageNames(listener);
    }


    public static void markAsRated() {
        // mark that the user has "rated" this app
        UHInternal.getInstance().markAsRated();
    }

    public static void setPushMessageListener(UHPushMessageListener listener) {
        UHInternal.getInstance().setPushMessageListener(listener);
    }

    public static UHActivityLifecycle getActivityLifecycle() {
        return UHInternal.getInstance().getActivityLifecycle();
    }

    /**
     * Checks a push notification to see if it came from User Hook or another service
     *
     * @param data
     * @return boolean if push message originated from User Hook
     */
    public static boolean isPushFromUserHook(Map<String, String> data) {
        return data != null && data.containsKey(PUSH_SOURCE_PARAM) && data.get(PUSH_SOURCE_PARAM).equals(PUSH_SOURCE_VALUE);
    }

    public static Notification handlePushMessage(Context context, Map<String, String> data) {

        return UHInternal.getInstance().handlePushMessage(context, data);
    }


    public static void rateThisApp() {

        UHInternal.getInstance().rateThisApp();
    }



    public static void setFeedbackScreenTitle(String title) {

        UHInternal.getInstance().setFeedbackScreenTitle(title);
    }

    public static void setFeedbackCustomFields(Map<String, String> customFields) {

        UHInternal.getInstance().setFeedbackCustomFields(customFields);
    }

    public static void showFeedback() {

        UHInternal.getInstance().showFeedback();

    }

    public static void showSurvey(String surveyId, String surveyTitle, UHHookPoint hookPoint) {

        UHInternal.getInstance().showSurvey(surveyId, surveyTitle, hookPoint);

    }

    public static void displayPrompt(String message, UHMessageMetaButton button1, UHMessageMetaButton button2) {

        UHInternal.getInstance().displayPrompt(message, button1, button2);

    }


    public static void showRatingPrompt(String message, String postiveButtonTitle, String negativeButtonTitle) {

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(postiveButtonTitle);
        button1.setClick(UHMessageMeta.CLICK_RATE);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeButtonTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);

        displayPrompt(message, button1, button2);

    }

    public static void showFeedbackPrompt(String message, String postiveButtonTitle, String negativeButtonTitle) {

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(postiveButtonTitle);
        button1.setClick(UHMessageMeta.CLICK_FEEDBACK);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeButtonTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);

        displayPrompt(message, button1, button2);

    }

    public static void displayStaticPage(String slug, String title) {

        UHInternal.getInstance().displayStaticPage(slug, title);

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
