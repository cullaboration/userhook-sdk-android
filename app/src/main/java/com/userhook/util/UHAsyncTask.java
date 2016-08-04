/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.util;

import android.os.AsyncTask;
import android.util.Log;

import com.userhook.UserHook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class UHAsyncTask extends AsyncTask<String, Void, String> {

    protected String queryString;

    protected UHAsyncTaskListener listener;
    protected String method = "GET";



    public UHAsyncTask(Map<String, Object> params, UHAsyncTaskListener listener) {
        this.queryString = getDataFromParams(params);
        this.listener = listener;
    }

    public UHAsyncTask(String queryString, UHAsyncTaskListener listener) {
        this.queryString = queryString;
        this.listener = listener;
    }

    protected String doInBackground(String... urls) {
        try {


            String finalUrl = UserHook.UH_API_URL;

            if (urls.length > 0) {
                finalUrl = urls[0];
            }

            if (method.equalsIgnoreCase("GET") && queryString != null) {
                finalUrl += "?" + queryString;
            }

            URL url = new URL(finalUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(method);
            conn.setRequestProperty(UHOperation.UH_APP_ID_HEADER_NAME, UserHook.getAppId());
            conn.setRequestProperty(UHOperation.UH_APP_KEY_HEADER_NAME, UserHook.getApiKey());

            // add user header values if available
            if (UHUser.getUserId() != null) {
                conn.setRequestProperty(UHOperation.UH_USER_ID_HEADER_NAME, UHUser.getUserId());
            }
            if (UHUser.getUserKey() != null) {
                conn.setRequestProperty(UHOperation.UH_USER_KEY_HEADER_NAME, UHUser.getUserKey());
            }


            if (method.equalsIgnoreCase("POST") && queryString != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", "" + Integer.toString(queryString.getBytes().length));

                byte[] outputInBytes = queryString.getBytes("utf-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                os.close();
            }



            InputStream is = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);


            return responseStrBuilder.toString();

        } catch (Exception e) {
            Log.e(UserHook.TAG, "error in userhook async request", e);
            return null;
        }

    }

    public static String getDataFromParams(Map<String, Object> params) {
        String str = "";

        if (params == null || params.size() == 0) {
            return "";
        }

        try {
            for (String key : params.keySet()) {

                if (!str.equalsIgnoreCase("")) {
                    str += "&";
                }

                String encodedKey = URLEncoder.encode(key, "utf-8");
                String encodedValue = URLEncoder.encode(params.get(key).toString(), "utf-8");

                str += encodedKey+"="+encodedValue;
            }
        }
        catch (Exception e) {
            Log.e(UserHook.TAG,"error prepping data for request", e);
        }

        return str;
    }


    public void onPostExecute(String result) {

        if (listener != null) {

                listener.onSuccess(result);
        }

    }

    public interface UHAsyncTaskListener {
        void onSuccess(String result);
    }


}

