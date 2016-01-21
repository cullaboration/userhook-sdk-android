/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;


public class UHHostedPageActivity extends AppCompatActivity {

    public static final String TYPE_PAGE = "page";
    public static final String TYPE_FEEDBACK = "feedback";
    public static final String TYPE_SURVEY = "survey";

    public static final String SURVEY_TITLE = "surveyTitle";
    public static final String HOOKPOINT_ID = "hookpointId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(UserHook.getResourceId("uh_hostedpage_activity", "layout"));

        String title = "";
        String url = "";

        UHPage page = null;

        if (getIntent().hasExtra(TYPE_PAGE)) {
            page = (UHPage) getIntent().getSerializableExtra(TYPE_PAGE);
            title = page.getName();
            url = UserHook.UH_HOST_URL + "/page/" + page.getSlug();
        } else if (getIntent().hasExtra(TYPE_FEEDBACK)) {
            title = getIntent().getStringExtra(TYPE_FEEDBACK);
            url = UserHook.UH_HOST_URL + "/feedback/";
        } else if (getIntent().hasExtra(TYPE_SURVEY) && getIntent().hasExtra(SURVEY_TITLE) && getIntent().hasExtra(HOOKPOINT_ID)) {
            title = getIntent().getStringExtra(SURVEY_TITLE);

            String surveyId = getIntent().getStringExtra(TYPE_SURVEY);
            String hookpointId = getIntent().getStringExtra(HOOKPOINT_ID);
            url = UserHook.UH_HOST_URL +"/survey/"+surveyId+"?hp="+hookpointId;
        }

        // add custom fields to query string
        if (getIntent().hasExtra(UserHook.UH_CUSTOM_FIELDS)) {
            Bundle customFields = getIntent().getBundleExtra(UserHook.UH_CUSTOM_FIELDS);


            Map<String, Object> params = new HashMap<>();
            int i =0 ;
            for (String key : customFields.keySet()) {
                params.put("custom_fields["+i+"][name]", key);
                params.put("custom_fields["+i+"][value]", customFields.get(key));
                i++;
            }

            String queryString = UHAsyncTask.getDataFromParams(params);

            url += "?" + queryString;
        }



        Toolbar toolbar = (Toolbar) findViewById(UserHook.getResourceId("toolbar","id"));
        if (toolbar != null) {

            toolbar.setTitle(title);

            setSupportActionBar(toolbar);

            // override left icon
            toolbar.setNavigationIcon(UserHook.getResourceId("abc_ic_ab_back_mtrl_am_alpha","drawable"));

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

        }



        WebView webView = (WebView) findViewById(UserHook.getResourceId("webView","id"));
        webView.getSettings().setJavaScriptEnabled(true);
        UHWebViewClient webClient = new UHWebViewClient();
        webView.setWebViewClient(webClient);


        webView.loadUrl(url, webClient.createUserHookHeaders());

    }


    private class UHWebViewClient extends WebViewClient {

        public void onPageFinished(WebView view, String url) {
            if (url.startsWith(UserHook.UH_HOST_URL)) {
                // inject javascript to convert POST forms into GET
                String js = "javascript:$('FORM').attr('method','GET');";
                view.loadUrl(js);
            }
        }


        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            if (url.startsWith(UserHook.UH_HOST_URL)) {

                // convert get url to post url and parameters
                String queryString = "";
                String domainUrl = url;
                if (url.contains("?")) {
                    queryString = url.substring(url.indexOf("?") + 1, url.length());
                    domainUrl = url.substring(0, url.indexOf("?"));
                }

                // post the request
                UHPostAsyncTask task = new UHPostAsyncTask(queryString, new UHAsyncTask.UHAsyncTaskListener() {
                    @Override
                    public void onSuccess(String html) {

                        view.loadDataWithBaseURL(UserHook.UH_HOST_URL, html, "text/html", "utf-8", url);

                    }
                });
                task.execute(domainUrl);

                // stop execution of the web load, let the async task do the actual loading
                return true;
            }
            else if (url.startsWith(UserHook.UH_URL_SCHEMA)) {
                // callback from webview to close view
                if (url.startsWith(UserHook.UH_URL_SCHEMA+"close")) {
                    finish();
                    return true;
                }
                else if (url.startsWith(UserHook.UH_URL_SCHEMA+"trackInteractionAndClose")) {

                    // track the user interaction for this hookpoint and then close the view
                    String hookpointId = url.substring((UserHook.UH_URL_SCHEMA+"trackInteractionAndClose/").length());
                    if (!hookpointId.equalsIgnoreCase("/")) {
                        UHHookPoint hookPoint = new UHHookPoint(hookpointId);
                        UserHook.trackHookPointInteraction(hookPoint);
                    }


                    finish();
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        public Map<String, String> createUserHookHeaders() {
            Map<String, String> headers = new HashMap<>();

            headers.put(UHOperation.UH_APP_ID_HEADER_NAME, UserHook.appId);
            headers.put(UHOperation.UH_APP_KEY_HEADER_NAME, UserHook.apiKey);

            // add user header values if available
            if (UHUser.getUserId() != null) {
                headers.put(UHOperation.UH_USER_ID_HEADER_NAME, UHUser.getUserId());
            }
            if (UHUser.getUserKey() != null) {
                headers.put(UHOperation.UH_USER_KEY_HEADER_NAME, UHUser.getUserKey());
            }

            return headers;
        }
    }
}
