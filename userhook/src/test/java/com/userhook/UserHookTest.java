package com.userhook;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMeta;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.model.UHPage;
import com.userhook.util.UHActivityLifecycle;
import com.userhook.util.UHInternal;
import com.userhook.util.UHOperation;
import com.userhook.util.UHOperationTestFactory;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.view.UHMessageView;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowPackageManager;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserHookTest extends UHBaseTest {



    @Test
    public void isPushFromUserHook() {

        Map<String,String> trueData = new HashMap<>();
        trueData.put(UserHook.PUSH_SOURCE_PARAM, UserHook.PUSH_SOURCE_VALUE);

        Map<String,String> falseData = new HashMap<>();
        falseData.put("one","two");

        assertTrue(UserHook.isPushFromUserHook(trueData));
        assertFalse(UserHook.isPushFromUserHook(falseData));
    }



    @Test
    public void showRatingPrompt() {

        String message = "prompt message";
        String positivetitle = "yes";
        String negativeTitle = "no";

        UHInternal uhInternal = mock(UHInternal.class);
        UHInternal.setInstance(uhInternal);

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(positivetitle);
        button1.setClick(UHMessageMeta.CLICK_RATE);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);


        doNothing().when(uhInternal).displayPrompt(message, button1, button2);

        // run logic
        UserHook.showRatingPrompt(message, positivetitle, negativeTitle);

        // verify
        verify(uhInternal).displayPrompt(message, button1, button2);

        // must clear our custom instance or future tests may fail
        UHInternal.setInstance(null);

    }


    @Test
    public void showFeedbackPrompt() {

        String message = "prompt message";
        String positivetitle = "yes";
        String negativeTitle = "no";

        UHInternal uhInternal = mock(UHInternal.class);
        UHInternal.setInstance(uhInternal);

        UHMessageMetaButton button1 = new UHMessageMetaButton();
        button1.setTitle(positivetitle);
        button1.setClick(UHMessageMeta.CLICK_FEEDBACK);

        UHMessageMetaButton button2 = new UHMessageMetaButton();
        button2.setTitle(negativeTitle);
        button2.setClick(UHMessageMeta.CLICK_CLOSE);


        // run logic
        UserHook.showFeedbackPrompt(message, positivetitle, negativeTitle);

        // verify
        verify(uhInternal).displayPrompt(message, button1, button2);

        // must clear our custom instance or future tests may fail
        UHInternal.setInstance(null);

    }
}
