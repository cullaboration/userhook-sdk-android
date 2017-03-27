/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.userhook.util;

import android.os.AsyncTask;
import android.util.Log;

import com.userhook.UserHook;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class UHAsyncTask extends AsyncTask<String, Void, String> {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    protected String queryString;

    protected UHAsyncTaskListener listener;
    protected String method = METHOD_GET;

    protected byte[] attachment;
    protected String attachmentFileExtension;
    protected String attachmentFieldName;
    protected String attachmentMimeType;

    protected Map<String, Object> params;

    protected UHAsyncConnectionBuilder connectionBuilder;

    public UHAsyncTask(Map<String, Object> params, byte[] attachment, String attachmentFieldName, String attachmentFileExtension, String attachmentMimeType, UHAsyncTaskListener listener) {

        this.params = params;
        this.listener = listener;
        this.method = METHOD_POST;
        this.attachment = attachment;
        this.attachmentFieldName = attachmentFieldName;
        this.attachmentFileExtension = attachmentFileExtension;
        this.attachmentMimeType = attachmentMimeType;

        this.connectionBuilder = new UHAsyncDefaultConnectionBuilder();
    }


    public UHAsyncTask(Map<String, Object> params, UHAsyncTaskListener listener) {
        this.queryString = getDataFromParams(params);
        this.listener = listener;


        this.connectionBuilder = new UHAsyncDefaultConnectionBuilder();
    }

    public UHAsyncTask(String queryString, UHAsyncTaskListener listener) {
        this.queryString = queryString;
        this.listener = listener;


        this.connectionBuilder = new UHAsyncDefaultConnectionBuilder();
    }

    protected String doInBackground(String... urls) {

        HttpURLConnection conn;
        int responseCode = -1;

        try {

            String finalUrl = UserHook.UH_API_URL;

            if (urls.length > 0) {
                finalUrl = urls[0];
            }

            // append query string
            if (method.equals(METHOD_GET) && queryString != null) {
                finalUrl += "?" + queryString;
            }

            conn = connectionBuilder.createConnection(finalUrl);

            // add User Hook specific headers
            conn = addUserHookHeaders(conn);

            conn.setRequestMethod(method);

            if (method.equalsIgnoreCase(METHOD_POST) && attachment != null) {

                // do file upload
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String boundary = "****UserHook" + System.currentTimeMillis()+"****";

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream writer = new DataOutputStream(conn.getOutputStream());

                // add form parameters
                if (params != null) {
                    for (String name : params.keySet()) {
                        addMultipartField(writer, name, (String) params.get(name), boundary);
                    }
                }

                // add attachment
                addMultipartFile(writer, boundary);
                writer.writeBytes("--" + boundary + "--\r\n");


                writer.flush();
                writer.close();


            } else if (method.equalsIgnoreCase(METHOD_POST) && queryString != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", Integer.toString(queryString.getBytes().length));

                byte[] outputInBytes = queryString.getBytes("utf-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                os.close();
            }


            responseCode = conn.getResponseCode();
            InputStream is = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            conn.disconnect();

            return responseStrBuilder.toString();

        } catch (Exception e) {
            Log.e(UserHook.TAG, "error in userhook async request: response code: " + responseCode, e);
            return null;
        }

    }

    protected HttpURLConnection addUserHookHeaders(HttpURLConnection conn) {

        conn.setRequestProperty(UHOperation.UH_APP_ID_HEADER_NAME, UHInternal.getInstance().getAppId());
        conn.setRequestProperty(UHOperation.UH_APP_KEY_HEADER_NAME, UHInternal.getInstance().getApiKey());
        conn.setRequestProperty(UHOperation.UH_SDK_HEADER_NAME, UHOperation.UH_SDK_HEADER_PREFIX + UserHook.UH_SDK_VERSION);

        // add user header values if available
        if (UHInternal.getInstance().getUser().getUserId() != null) {
            conn.setRequestProperty(UHOperation.UH_USER_ID_HEADER_NAME, UHInternal.getInstance().getUser().getUserId());
        }
        if (UHInternal.getInstance().getUser().getUserKey() != null) {
            conn.setRequestProperty(UHOperation.UH_USER_KEY_HEADER_NAME, UHInternal.getInstance().getUser().getUserKey());
        }

        return conn;
    }

    /**
     * add form field value to multipart request
     *
     * @param writer
     * @param name
     * @param value
     * @param boundary
     * @throws Exception
     */
    protected void addMultipartField(DataOutputStream writer, String name, String value, String boundary) throws Exception {

        writer.writeBytes("--" + boundary+"\r\n");
        writer.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
        writer.writeBytes("\r\n");
        writer.writeBytes("\r\n");
        writer.writeBytes(value);
        writer.writeBytes("\r\n");
        writer.flush();

    }

    /**
     * add file to multipart requeset
     *
     * @param writer
     * @param boundary
     * @throws Exception
     */
    protected void addMultipartFile(DataOutputStream writer, String boundary) throws Exception {

        writer.writeBytes("--" + boundary+"\r\n");
        writer.writeBytes("Content-Disposition: form-data; name=\"" + attachmentFieldName + "\";filename=\"" + attachmentFieldName+"." + attachmentFileExtension+"\"");
        writer.writeBytes("\r\n");
        writer.writeBytes("Content-Type: " + attachmentMimeType);
        writer.writeBytes("\r\n");
        writer.writeBytes("\r\n");
        writer.write(attachment);
        writer.writeBytes("\r\n");
        writer.flush();

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

                str += encodedKey + "=" + encodedValue;
            }
        } catch (Exception e) {
            Log.e(UserHook.TAG, "error prepping data for request", e);
        }

        return str;
    }


    public void onPostExecute(String result) {

        if (listener != null) {

            listener.onSuccess(result);
        }

    }


    // allow overriding of connection builder for better testing
    protected void setUHAsyncConnectionBuilder(UHAsyncConnectionBuilder builder) {
        this.connectionBuilder = builder;
    }

    public interface UHAsyncTaskListener {
        void onSuccess(String result);
    }


    public interface UHAsyncConnectionBuilder {
        HttpURLConnection createConnection(String url);
    }

    protected class UHAsyncDefaultConnectionBuilder implements UHAsyncConnectionBuilder {

        public HttpURLConnection createConnection(String url) {
            try {
                URL u = new URL(url);

                return (HttpURLConnection) u.openConnection();

            }
            catch (Exception me) {
                Log.e(UserHook.TAG, "error creating url", me);
                return null;
            }
        }
    }

}

