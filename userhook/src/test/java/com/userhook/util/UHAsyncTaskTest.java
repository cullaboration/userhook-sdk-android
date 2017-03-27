/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.util;

import com.userhook.BuildConfig;
import com.userhook.UHBaseTest;
import com.userhook.UserHook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UHAsyncTaskTest extends UHBaseTest {

    @Before
    public void before() {

        UHInternal.getInstance().initialize(null, "app123","key123",false);

        UHUserTestProvider user = new UHUserTestProvider();
        user.setUserId("user123");
        user.setUserKey("userkey123");
        UHInternal.getInstance().setUHUserProvider(user);
    }

    @Test
    public void testDataToString() {

        Map<String, Object> params = new HashMap<>();
        params.put("one", "two");

        assertEquals(UHAsyncTask.getDataFromParams(params), "one=two");

        params.put("three", "four and five");
        assertEquals(UHAsyncTask.getDataFromParams(params), "one=two&three=four+and+five");

    }

    @Test
    public void testAddHeaders() throws Exception {


        HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);

        UHAsyncTask task = new UHAsyncTask(new HashMap<String,Object>(), null);

        task.addUserHookHeaders(conn);

        verify(conn).setRequestProperty(UHOperation.UH_APP_ID_HEADER_NAME, "app123");
        verify(conn).setRequestProperty(UHOperation.UH_APP_KEY_HEADER_NAME, "key123");
        verify(conn).setRequestProperty(UHOperation.UH_SDK_HEADER_NAME, UHOperation.UH_SDK_HEADER_PREFIX + UserHook.UH_SDK_VERSION);
        verify(conn).setRequestProperty(UHOperation.UH_USER_ID_HEADER_NAME, "user123");
        verify(conn).setRequestProperty(UHOperation.UH_USER_KEY_HEADER_NAME, "userkey123");

    }

    @Test
    public void testGetRequest() throws Exception {

        HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);

        UHAsyncTask.UHAsyncConnectionBuilder connectionBuilder = new UHAsyncTestConnectionBuilder(conn);

        String serverResponse = "response from server";
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream(serverResponse.getBytes()));


        UHAsyncTask task = new UHAsyncTask(null, null, null, null, null, null);
        task.setUHAsyncConnectionBuilder(connectionBuilder);


        task.execute("http://test.com");


        Robolectric.flushBackgroundThreadScheduler();

        assertEquals(task.get(), serverResponse);


    }

    @Test
    public void testPostRequest() throws Exception {

        HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);

        OutputStream os = new ByteArrayOutputStream();
        when(conn.getOutputStream()).thenReturn(os);

        UHAsyncTask.UHAsyncConnectionBuilder connectionBuilder = new UHAsyncTestConnectionBuilder(conn);

        String serverResponse = "response from server";
        when(conn.getInputStream()).thenReturn(new ByteArrayInputStream(serverResponse.getBytes()));


        String queryString="one=two";

        UHAsyncTask task = new UHPostAsyncTask(queryString, null);
        task.setUHAsyncConnectionBuilder(connectionBuilder);


        task.execute("http://test.com");


        Robolectric.flushBackgroundThreadScheduler();

        // post data headers
        verify(conn).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        verify(conn).setRequestProperty("Content-Length", Integer.toString(queryString.getBytes().length));


        assertEquals(task.get(), serverResponse);

    }

}
