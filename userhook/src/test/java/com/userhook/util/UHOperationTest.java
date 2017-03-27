package com.userhook.util;

import com.userhook.BuildConfig;
import com.userhook.UHBaseTest;
import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.hookpoint.UHHookPointSurvey;
import com.userhook.model.UHPage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHOperationTest extends UHBaseTest {



    @Test
    public void testHandleUpdateSession() {

        UserHook.UHSuccessListener listener = Mockito.mock(UserHook.UHSuccessListener.class);

        String responseString = "{\"status\":\"success\", \"data\":{\"user\":\"user456\",\"key\":\"userkey456\", \"new_feedback\":true}}";

        UHOperation operation = new UHOperation();

        operation.handleUpdateSession(responseString, listener);

        assertEquals(UHInternal.getInstance().getUser().getUserId(), "user456");
        assertEquals(UHInternal.getInstance().getUser().getUserKey(), "userkey456");
        assertTrue(UserHook.hasNewFeedback());

        verify(listener, times(1)).onSuccess();

    }

    @Test
    public void testUpdateSession() {


        UHOperation operation = spy(UHOperation.class);


        Map<String,Object> params = new HashMap<>();
        params.put("one","two");
        params.put("three","four");

        UHPostAsyncTask task = Mockito.mock(UHPostAsyncTask.class);
        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        when(operation.createPostRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);


        // call operation
        operation.updateSessionData(params, null);


        // verify results

        assertEquals(data.getValue().get("sdk"), UserHook.UH_SDK_VERSION);
        assertEquals(data.getValue().get("os"), "android");
        assertEquals(data.getValue().get("os_version"), "1.2.3");
        assertEquals(data.getValue().get("device"), "test device");
        assertEquals(data.getValue().get("locale"), "en-us-test");
        assertEquals(data.getValue().get("app_version"), "0.1");
        assertEquals(data.getValue().get("timezone_offset"), new Long(-100));
        assertEquals(data.getValue().get("user"),"user123");
        assertFalse(data.getValue().containsKey("session"));

        verify(task).execute(UserHook.UH_API_URL + "/session");

    }


    @Test
    public void testCreateSession() {


        UHOperation operation = spy(UHOperation.class);

        UHPostAsyncTask task = Mockito.mock(UHPostAsyncTask.class);
        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        when(operation.createPostRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);
        doNothing().when(operation).fetchMessageTemplates();

        // call operation
        operation.createSession(null);


        // verify results

        assertEquals(data.getValue().get("sdk"), UserHook.UH_SDK_VERSION);
        assertEquals(data.getValue().get("os"), "android");
        assertEquals(data.getValue().get("os_version"), "1.2.3");
        assertEquals(data.getValue().get("device"), "test device");
        assertEquals(data.getValue().get("locale"), "en-us-test");
        assertEquals(data.getValue().get("app_version"), "0.1");
        assertEquals(data.getValue().get("timezone_offset"), new Long(-100));
        assertEquals(data.getValue().get("user"),"user123");
        assertEquals(data.getValue().get("sessions"),"1");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH);
        String date = (String)data.getValue().get("last_launch");
        try {
            assertTrue(format.parse(date) instanceof  Date);
        }
        catch (ParseException pe) {
            // if the date is invalid the exception will not be null and this will fail
            assertTrue(pe != null);
        }


        verify(task).execute(UserHook.UH_API_URL + "/session");
        verify(operation, times(1)).fetchMessageTemplates();

    }

    @Test
    public void testHandleFetchPageNames() {

        UHOperation.UHArrayListener listener = Mockito.mock(UHOperation.UHArrayListener.class);

        String responseString = "{\"status\":\"success\", \"data\":[{\"name\":\"First\", \"slug\":\"first\"}, {\"name\":\"Second\",\"slug\":\"second\"}]}";

        List<UHPage> expectedPages = new ArrayList<>();
        UHPage page1 = new UHPage();
        page1.setName("First");
        page1.setSlug("first");
        expectedPages.add(page1);

        UHPage page2 = new UHPage();
        page2.setName("Second");
        page2.setSlug("second");
        expectedPages.add(page2);

        UHOperation operation = new UHOperation();

        operation.handleFetchPageNames(responseString, listener);

        ArgumentCaptor<List<UHPage>> pages = ArgumentCaptor.forClass(List.class);

        verify(listener).onSuccess(pages.capture());

        assertEquals(pages.getValue(), expectedPages);

    }

    @Test
    public void testFetchPageNames() {

        UHOperation operation = spy(UHOperation.class);


        UHAsyncTask task = Mockito.mock(UHAsyncTask.class);

        when(operation.createGetRequest(any(Map.class), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);


        // call operation
        operation.fetchPageNames(null);


        // verify results
        verify(task).execute(UserHook.UH_API_URL + "/page");
    }

    @Test
    public void testFetchMessageTemplates() {

        UHOperation operation = spy(UHOperation.class);


        UHAsyncTask task = Mockito.mock(UHAsyncTask.class);

        when(operation.createGetRequest(any(Map.class), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);


        // call operation
        operation.fetchMessageTemplates();


        // verify results
        verify(task).execute(UserHook.UH_HOST_URL + "/message/templates");
    }

    @Test
    public void testHandleMessageTemplates() {

        String responseString = "{\"status\":\"success\", \"templates\":{\"one\":\"first template\",\"second\":\"second template\"}}";

        UHOperation operation = new UHOperation();

        operation.handleMessageTemplates(responseString);

        assertTrue(UHMessageTemplate.getInstance().hasTemplate("one"));
        assertTrue(UHMessageTemplate.getInstance().hasTemplate("second"));
    }

    @Test
    public void testHandleFetchHookpoint() {

        String responseString = "{\"status\":\"success\", \"data\":{\"hookpoint\": {\"id\":\"hookpoint123\",\"type\":\"survey\",\"name\":\"test survey\"}}}";

        UserHook.UHHookPointFetchListener listener = mock(UserHook.UHHookPointFetchListener.class);

        UHOperation operation = new UHOperation();


        operation.handleFetchHookpoint(responseString, listener);



        ArgumentCaptor<UHHookPoint> hookpoint = ArgumentCaptor.forClass(UHHookPoint.class);

        verify(listener).onSuccess(hookpoint.capture());
        assertTrue(hookpoint.getValue() instanceof UHHookPointSurvey);

    }

    @Test
    public void testFetchHookpoint() {

        UHOperation operation = spy(UHOperation.class);

        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        UHAsyncTask task = Mockito.mock(UHAsyncTask.class);

        when(operation.createGetRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);


        String event = "launch";

        // call operation
        operation.fetchHookpoint(event, null);

        // verify results
        verify(task).execute(UserHook.UH_API_URL + "/hookpoint/next");
        assertEquals(data.getValue().get("user"), UHInternal.getInstance().getUser().getUserId());
        assertEquals(data.getValue().get("event"), event);


    }

    @Test
    public void testTrackHookpointAction() {

        String action = "view";

        UHHookPoint hookPoint = new UHHookPoint("hookpoint123");

        UHOperation operation = spy(UHOperation.class);

        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        UHPostAsyncTask task = Mockito.mock(UHPostAsyncTask.class);

        when(operation.createPostRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);


        String event = "launch";

        // call operation
        operation.trackHookpointAction(hookPoint, action);

        // verify results
        verify(task).execute(UserHook.UH_API_URL + "/hookpoint/track");
        assertEquals(data.getValue().get("user"), UHInternal.getInstance().getUser().getUserId());
        assertEquals(data.getValue().get("action"), action);
        assertEquals(data.getValue().get("hookpoint"), "hookpoint123");

    }

    @Test
    public void testRegisterPushToken() {

        UHOperation operation = spy(UHOperation.class);

        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        UHPostAsyncTask task = Mockito.mock(UHPostAsyncTask.class);

        when(operation.createPostRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);

        String pushToken = "token123";
        int retryCount = 0;

        operation.registerPushToken(pushToken, retryCount);

        // verify results
        verify(task).execute(UserHook.UH_API_URL + "/push/register");
        assertEquals(data.getValue().get("user"), UHInternal.getInstance().getUser().getUserId());
        assertEquals(data.getValue().get("token"), pushToken);
        assertEquals(data.getValue().get("timezone_offset"), UHInternal.getInstance().getDevice().getTimezoneOffset());
        assertEquals(data.getValue().get("sdk"), UserHook.UH_SDK_VERSION);

    }


    @Test
    public void testTrackPushOpen() {

        UHOperation operation = spy(UHOperation.class);

        ArgumentCaptor<Map> data = ArgumentCaptor.forClass(Map.class);

        UHPostAsyncTask task = Mockito.mock(UHPostAsyncTask.class);

        when(operation.createPostRequest(data.capture(), any(UHAsyncTask.UHAsyncTaskListener.class))).thenReturn(task);

        Map<String,String> payload = new HashMap<>();
        payload.put("one","two");

        operation.trackPushOpen(payload);

        // verify results
        verify(task).execute(UserHook.UH_API_URL + "/push/open");
        assertEquals(data.getValue().get("user"), UHInternal.getInstance().getUser().getUserId());
        assertEquals(data.getValue().get("payload"), "{\"one\":\"two\"}");
        assertEquals(data.getValue().get("os"), "android");
        assertEquals(data.getValue().get("sdk"), UserHook.UH_SDK_VERSION);

    }
}
