package com.userhook.util;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.userhook.BuildConfig;
import com.userhook.UHBaseTest;
import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMeta;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.model.UHPage;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.view.UHMessageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPackageManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHInternalTest extends UHBaseTest {


    @Test
    public void updateSessionData() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        Map<String,Object> data = new HashMap<>();
        data.put("one","two");

        UserHook.UHSuccessListener listener = new UserHook.UHSuccessListener() {
            @Override
            public void onSuccess() {

            }
        };

        UHInternal.getInstance().updateSessionData(data, listener);

        verify(operation).updateSessionData(data, listener);


    }

    @Test
    public void updateCustomData() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        Map<String,Object> data = new HashMap<>();
        data.put("one","two");

        Map<String,Object> expectedData = new HashMap<>();
        expectedData.put("custom_fields.one","two");

        UserHook.UHSuccessListener listener = new UserHook.UHSuccessListener() {
            @Override
            public void onSuccess() {

            }
        };


        UHInternal.getInstance().updateCustomFields(data, listener);

        verify(operation).updateSessionData(expectedData, listener);


    }


    @Test
    public void updatePurchasedItem() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        UserHook.UHSuccessListener listener = new UserHook.UHSuccessListener() {
            @Override
            public void onSuccess() {

            }
        };

        String sku = "sku123";
        Number price = 1.5;

        Map<String,Object> expectedData = new HashMap<>();
        expectedData.put("purchases",sku);
        expectedData.put("purchases_amount", price);

        UHInternal.getInstance().updatePurchasedItem(sku, price, listener);

        verify(operation).updateSessionData(expectedData, listener);


    }

    @Test
    public void fetchHookpoint() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        UserHook.UHHookPointFetchListener listener = new UserHook.UHHookPointFetchListener() {
            @Override
            public void onSuccess(UHHookPoint hookPoint) {

            }

            @Override
            public void onError() {

            }
        };

        String event = "eventName";


        UHInternal.getInstance().fetchHookPoint(event, listener);

        verify(operation).fetchHookpoint(event, listener);


    }

    @Test
    public void fetchPagenames() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        UHOperation.UHArrayListener<UHPage> listener = new UHOperation.UHArrayListener<UHPage>() {
            @Override
            public void onSuccess(List<UHPage> items) {

            }
        };


        UHInternal.getInstance().fetchPageNames(listener);

        verify(operation).fetchPageNames(listener);


    }

    @Test
    public void trackHookpointDisplay() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        UHHookPoint hookPoint = new UHHookPoint("point123");

        UHInternal.getInstance().trackHookPointDisplay(hookPoint);

        verify(operation).trackHookpointAction(hookPoint, UserHook.UH_HOOK_POINT_DISPLAY_ACTION);


    }

    @Test
    public void trackHookpointInteraction() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        UHHookPoint hookPoint = new UHHookPoint("point123");

        UHInternal.getInstance().trackHookPointInteraction(hookPoint);

        verify(operation).trackHookpointAction(hookPoint, UserHook.UH_HOOK_POINT_INTERACT_ACTION);


    }

    @Test
    public void markAsRated() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        Map<String,Object> data = new HashMap<>();
        data.put("rated",true);

        UHInternal.getInstance().markAsRated();

        verify(operation).updateSessionData(data, null);


    }

    @Test
    public void registerPushToken() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        Map<String,Object> data = new HashMap<>();
        data.put("rated",true);

        String token = "token123";

        UHInternal.getInstance().registerPushToken(token);

        verify(operation).registerPushToken(token, 1);


    }

    @Test
    public void trackPushOpen() {

        UHOperation operation = mock(UHOperation.class);

        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));

        Map<String,String> data = new HashMap<>();
        data.put("one","two");

        UHInternal.getInstance().trackPushOpen(data);

        verify(operation).trackPushOpen(data);


    }



    @Test
    public void handlePushPayload() {

        UserHook.UHPayloadListener listener = mock(UserHook.UHPayloadListener.class);

        String payloadString = "{\"one\":\"two\"}";
        Map<String,Object> payloadData = new HashMap<>();
        payloadData.put("one","two");

        UHInternal.getInstance().setPayloadListener(listener);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        UHInternal.getInstance().handlePushPayload(activity, payloadString);

        verify(listener).onAction(activity, payloadData);
    }

    @Test
    public void actionReceived() {

        UserHook.UHPayloadListener listener = mock(UserHook.UHPayloadListener.class);

        Map<String,Object> payloadData = new HashMap<>();
        payloadData.put("one","two");

        UHInternal.getInstance().setPayloadListener(listener);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        UHInternal.getInstance().actionReceived(activity, payloadData);

        verify(listener).onAction(activity, payloadData);
    }

    @Test
    public void newFeedbackInBackground() {

        UHInternal.getInstance().setHasNewFeedback(true);

        assertTrue(UHInternal.getInstance().hasNewFeedback());
    }

    @Test
    public void newFeedbackInForeground() {

        UserHook.UHFeedbackListener listener = mock(UserHook.UHFeedbackListener.class);

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        UHInternal.getInstance().setFeedbackListener(listener);

        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        when(lifecycle.isForeground()).thenReturn(true);

        UHInternal.getInstance().setHasNewFeedback(true);

        assertTrue(UHInternal.getInstance().hasNewFeedback());

        verify(listener).onNewFeedback(activity);
    }

    @Test
    public void showFeedback() {

        Activity activity = mock(Activity.class);

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);

        Map<String,String> customFields = new HashMap<>();
        customFields.put("one","two");
        UHInternal.getInstance().setFeedbackCustomFields(customFields);

        Bundle customFieldsBundle = new Bundle();
        customFieldsBundle.putString("one","two");

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        doNothing().when(activity).startActivity(intent.capture());

        UHInternal.getInstance().showFeedback();

        assertEquals(intent.getValue().getComponent().getClassName(), UHHostedPageActivity.class.getCanonicalName());
        assertEquals(intent.getValue().getStringExtra(UHHostedPageActivity.TYPE_FEEDBACK), "Feedback");
        assertEquals(intent.getValue().getBundleExtra(UserHook.UH_CUSTOM_FIELDS).size(), customFieldsBundle.size());
        assertEquals(intent.getValue().getBundleExtra(UserHook.UH_CUSTOM_FIELDS).keySet(), customFieldsBundle.keySet());

    }

    @Test
    public void showSurvey() {

        Activity activity = mock(Activity.class);

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);

        String surveyId = "123";
        String surveyTitle = "survey title";
        UHHookPoint hookPoint = new UHHookPoint("hookpoint123");


        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        doNothing().when(activity).startActivity(intent.capture());

        UHInternal.getInstance().showSurvey(surveyId, surveyTitle, hookPoint);

        assertEquals(intent.getValue().getComponent().getClassName(), UHHostedPageActivity.class.getCanonicalName());
        assertEquals(intent.getValue().getStringExtra(UHHostedPageActivity.TYPE_SURVEY), surveyId);
        assertEquals(intent.getValue().getStringExtra(UHHostedPageActivity.SURVEY_TITLE), surveyTitle);
        assertEquals(intent.getValue().getStringExtra(UHHostedPageActivity.HOOKPOINT_ID), hookPoint.getId());

    }

    @Test
    public void showStaticPage() {

        Activity activity = mock(Activity.class);

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);

        String slug = "123";
        String title = "page title";

        UHPage page = new UHPage();
        page.setSlug(slug);
        page.setName(title);


        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        doNothing().when(activity).startActivity(intent.capture());

        UHInternal.getInstance().displayStaticPage(slug, title);

        assertEquals(intent.getValue().getComponent().getClassName(), UHHostedPageActivity.class.getCanonicalName());
        assertEquals(intent.getValue().getSerializableExtra(UHHostedPageActivity.TYPE_PAGE), page);

    }

    @Test
    public void startActivityToRateNoPlayStorePresent() {

        String packageName = "com.test";

        String storeUrl = "http://play.google.com/store/apps/details?id=" + packageName;

        Activity activity = mock(Activity.class);

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        PackageManager packageManager = mock(PackageManager.class);

        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        when(activity.getPackageName()).thenReturn(packageName);
        when(activity.getPackageManager()).thenReturn(packageManager);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);
        doNothing().when(activity).startActivity(intent.capture());

        // update session data
        UHOperation operation = mock(UHOperation.class);
        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));
        Map<String,Object> data = new HashMap<>();
        data.put("rated",true);


        UHInternal.getInstance().rateThisApp();


        verify(operation).updateSessionData(data, null);
        assertEquals(intent.getValue().getAction(), Intent.ACTION_VIEW);
        assertEquals(intent.getValue().getData(), Uri.parse(storeUrl));




    }

    @Test
    public void startActivityToRatePlayStorePresent() {

        String packageName = "com.test";

        String storeUrl = "market://details?id=" + packageName;

        Activity activity = spy(Robolectric.buildActivity(Activity.class).get());

        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        ShadowPackageManager packageManager = shadowOf(RuntimeEnvironment.application.getPackageManager());


        ResolveInfo info = new ResolveInfo();
        info.isDefault = true;

        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.packageName = "com.play";
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = appInfo;
        info.activityInfo.name = "Test";

        Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl));
        packageManager.addResolveInfoForIntent(storeIntent, info);


        UHInternal.getInstance().setActivityLifecycle(lifecycle);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        when(activity.getPackageName()).thenReturn(packageName);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);
        doNothing().when(activity).startActivity(intent.capture());

        // update session data
        UHOperation operation = mock(UHOperation.class);
        UHInternal.getInstance().setOperationFactory(new UHOperationTestFactory(operation));
        Map<String,Object> data = new HashMap<>();
        data.put("rated",true);


        UHInternal.getInstance().rateThisApp();


        verify(operation).updateSessionData(data, null);
        assertEquals(intent.getValue().getAction(), Intent.ACTION_VIEW);
        assertEquals(intent.getValue().getData(), Uri.parse(storeUrl));

    }

    @Test
    public void handleNotification() {


        Context context = RuntimeEnvironment.application;

        Map<String,String> data = new HashMap<>();
        data.put("title","push title");
        data.put("message","push message");
        String payloadJson = "{\"one\":\"two\", \"new_feedback\": true}";
        data.put("payload", payloadJson);

        Map<String,Object> payloadData = new HashMap<>();
        payloadData.put("one","two");
        payloadData.put("new_feedback", true);

        Intent intent = mock(Intent.class);

        UserHook.UHPushMessageListener pushListener = mock(UserHook.UHPushMessageListener.class);
        UHInternal.getInstance().setPushMessageListener(pushListener);

        when(pushListener.onPushMessage(payloadData)).thenReturn(intent);

        // handle message
        Notification notification = UHInternal.getInstance().handlePushMessage(context, data);

        // verify
        verify(intent).putExtra(UserHook.UH_PUSH_DATA, (Serializable)data);
        verify(intent).putExtra(UserHook.UH_PUSH_TRACKED, false);
        verify(intent).putExtra(UserHook.UH_PUSH_FEEDBACK, true);
        verify(intent).putExtra(UserHook.UH_PUSH_PAYLOAD, payloadJson);

        assertTrue(UHInternal.getInstance().hasNewFeedback());



    }

    @Test
    public void displayPrompt2Buttons() {

        String message = "prompt message";
        UHMessageMetaButton button1 = new UHMessageMetaButton();
        UHMessageMetaButton button2 = new UHMessageMetaButton();

        Activity activity = Robolectric.buildActivity(Activity.class).get();
        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);

        UHMessageView.Factory mockFactory = mock(UHMessageView.Factory.class);
        UHInternal.getInstance().setMessageViewFactory(mockFactory);

        ArgumentCaptor<UHMessageMeta> metaCapture = ArgumentCaptor.forClass(UHMessageMeta.class);

        UHMessageView messageView = mock(UHMessageView.class);

        when(mockFactory.createMessageView(any(Activity.class), metaCapture.capture())).thenReturn(messageView);

        // run logic
        UHInternal.getInstance().displayPrompt(message, button1, button2);

        // since view will be run on main ui thread
        Robolectric.flushForegroundThreadScheduler();

        // verify
        assertEquals(metaCapture.getValue().getDisplayType(), UHMessageMeta.TYPE_TWO_BUTTONS);
        assertEquals(metaCapture.getValue().getButton1(), button1);
        assertEquals(metaCapture.getValue().getButton2(), button2);
        verify(messageView).showDialog();

    }

    @Test
    public void displayPrompt1Button() {

        String message = "prompt message";
        UHMessageMetaButton button1 = new UHMessageMetaButton();

        Activity activity = Robolectric.buildActivity(Activity.class).get();
        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);

        UHMessageView.Factory mockFactory = mock(UHMessageView.Factory.class);
        UHInternal.getInstance().setMessageViewFactory(mockFactory);

        ArgumentCaptor<UHMessageMeta> metaCapture = ArgumentCaptor.forClass(UHMessageMeta.class);

        UHMessageView messageView = mock(UHMessageView.class);

        when(mockFactory.createMessageView(any(Activity.class), metaCapture.capture())).thenReturn(messageView);

        // run logic
        UHInternal.getInstance().displayPrompt(message, button1, null);

        // since view will be run on main ui thread
        Robolectric.flushForegroundThreadScheduler();

        // verify
        assertEquals(metaCapture.getValue().getDisplayType(), UHMessageMeta.TYPE_ONE_BUTTON);
        assertEquals(metaCapture.getValue().getButton1(), button1);
        assertNull(metaCapture.getValue().getButton2());
        verify(messageView).showDialog();

    }

    @Test
    public void displayPromptNoButton() {

        String message = "prompt message";

        Activity activity = Robolectric.buildActivity(Activity.class).get();
        UHActivityLifecycle lifecycle = mock(UHActivityLifecycle.class);
        when(lifecycle.getCurrentActivity()).thenReturn(activity);
        UHInternal.getInstance().setActivityLifecycle(lifecycle);

        UHMessageView.Factory mockFactory = mock(UHMessageView.Factory.class);
        UHInternal.getInstance().setMessageViewFactory(mockFactory);

        ArgumentCaptor<UHMessageMeta> metaCapture = ArgumentCaptor.forClass(UHMessageMeta.class);

        UHMessageView messageView = mock(UHMessageView.class);

        when(mockFactory.createMessageView(any(Activity.class), metaCapture.capture())).thenReturn(messageView);

        // run logic
        UHInternal.getInstance().displayPrompt(message, null, null);

        // since view will be run on main ui thread
        Robolectric.flushForegroundThreadScheduler();

        // verify
        assertEquals(metaCapture.getValue().getDisplayType(), UHMessageMeta.TYPE_NO_BUTTONS);
        assertNull(metaCapture.getValue().getButton1());
        assertNull(metaCapture.getValue().getButton2());
        verify(messageView).showDialog();

    }

}
