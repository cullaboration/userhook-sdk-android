/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHPage;
import com.userhook.util.UHAsyncTask;
import com.userhook.util.UHInternal;
import com.userhook.util.UHOperation;
import com.userhook.util.UHPostAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UHHostedPageActivity extends AppCompatActivity {

    public static final String TYPE_PAGE = "page";
    public static final String TYPE_FEEDBACK = "feedback";
    public static final String TYPE_SURVEY = "survey";

    public static final String SURVEY_TITLE = "surveyTitle";
    public static final String HOOKPOINT_ID = "hookpointId";

    protected WebView webView;

    private final int PICK_IMAGE_REQUEST = 1002;

    // reference to selected image to be uploaded with a webpage form
    private byte[] attachment;

    // reference to attachment form field name
    private String attachmentFieldName;

    private String attachmentFileExtension;
    private String attachmentMimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(UHInternal.getInstance().getResourceId(getApplicationContext(), "uh_hostedpage_activity", "layout"));

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



        Toolbar toolbar = (Toolbar) findViewById(UHInternal.getInstance().getResourceId(getApplicationContext(), "toolbar","id"));
        if (toolbar != null) {

            toolbar.setTitle(title);

            setSupportActionBar(toolbar);

            // make home button show as back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

        }



        webView = (WebView) findViewById(UHInternal.getInstance().getResourceId(getApplicationContext(), "webView","id"));
        webView.getSettings().setJavaScriptEnabled(true);
        UHWebViewClient webClient = new UHWebViewClient();
        webView.setWebViewClient(webClient);


        webView.loadUrl(url, webClient.createUserHookHeaders());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // temporarily store attachment

            Uri uri = data.getData();

            try {
                Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // shrink image
                int maxWidth = 600;
                int maxHeight = 800;

                float aspect = (float)originalImage.getWidth() / (float)originalImage.getHeight();
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                if (width > height && width > maxWidth) {
                    // horizontal image
                    width = maxWidth;
                    height = (int)(maxWidth / aspect);
                }
                else if (height > width && height > maxHeight) {
                    // vertical image
                    height = maxHeight;
                    width = (int)(maxHeight * aspect);
                }
                else if (height > maxHeight) {
                    height = maxHeight;
                    width = height;
                }
                else if (width > maxWidth) {
                    width = maxWidth;
                    height = width;
                }


                Bitmap scaledImage = Bitmap.createScaledBitmap(originalImage, width, height, true);

                // convert to jpeg
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                attachment = bos.toByteArray();

                originalImage.recycle();
                scaledImage.recycle();

                attachmentFileExtension = "jpg";
                attachmentMimeType = "image/jpeg";

                // send message to web view
                webView.loadUrl("javascript:markUploadAttached();");

            } catch (IOException e) {
                Log.e(UserHook.TAG, "error picking image", e);
            }
        }
    }

    private class UHWebViewClient extends WebViewClient {

        public void onPageFinished(WebView view, String url) {
            if (url.startsWith(UserHook.UH_HOST_URL)) {

                // inject javascript to convert POST forms into GET
                // this is needed because the shouldOverrideUrlLoading does not intercept POST's
                String js = "javascript:$('FORM').attr('method','GET');";
                view.loadUrl(js);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)  {
            String url = request.getUrl().toString();

            return shouldOverrideUrl(view, url);
        }



        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            return shouldOverrideUrl(view, url);

        }


        private boolean shouldOverrideUrl(final WebView view, final String url) {

            Uri uri = Uri.parse(url);

            if (url.startsWith(UserHook.UH_HOST_URL)) {

                // get request url without a querystring
                String domainUrl = uri.toString();
                if (domainUrl.indexOf("?") > 0) {
                    domainUrl = domainUrl.substring(0, domainUrl.indexOf("?"));
                }

                // convert query parameters to map
                Map<String,Object> queryParams = new HashMap<>();
                for (String name : uri.getQueryParameterNames()) {
                    queryParams.put(name, uri.getQueryParameter(name));
                }

                UHAsyncTask task = null;
                UHAsyncTask.UHAsyncTaskListener listener = new UHAsyncTask.UHAsyncTaskListener() {
                    @Override
                    public void onSuccess(String html) {
                        view.loadDataWithBaseURL(UserHook.UH_HOST_URL, html, "text/html", "utf-8", url);

                        // reset attachment
                        attachment = null;
                        attachmentFileExtension = null;
                        attachmentFieldName = null;
                    }
                };

                if (attachment != null) {
                    // create multipart request
                    task = new UHAsyncTask(queryParams, attachment, attachmentFieldName, attachmentFileExtension, attachmentMimeType, listener);
                }
                else {
                    // create normal post request
                    task = new UHPostAsyncTask(queryParams, listener);
                }
                task.execute(domainUrl);

                // stop execution of the web load, let the async task do the actual loading
                return true;
            }
            else if (url.startsWith(UserHook.UH_URL_SCHEMA)) {
                // callback from webview to close view
                if (uri.getHost().equalsIgnoreCase("host")) {
                    finish();
                    return true;
                }
                else if (uri.getHost().equalsIgnoreCase("trackInteractionAndClose")) {

                    // track the user interaction for this hookpoint and then close the view
                    String hookpointId = url.substring((UserHook.UH_URL_SCHEMA+"trackInteractionAndClose/").length());
                    if (!hookpointId.equalsIgnoreCase("/")) {
                        UHHookPoint hookPoint = new UHHookPoint(hookpointId);
                        UHInternal.getInstance().trackHookPointInteraction(hookPoint);
                    }


                    finish();
                    return true;
                }
                else if (uri.getHost().equalsIgnoreCase("imagepicker")) {

                    // store form name for this attachment
                    attachmentFieldName = uri.getPath();
                    if (attachmentFieldName.indexOf("/") == 0) {
                        attachmentFieldName = attachmentFieldName.substring(1);
                    }

                    // open image picker
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                    return true;
                }
                else if (uri.getHost().equalsIgnoreCase("imagepicker_reset")) {

                    // remove reference to the attached file
                    attachment = null;

                    // reset file picker on webpage
                    view.loadUrl("javascript:resetUpload();");

                    return true;
                }
            }

            return false;
        }

        private Map<String, String> createUserHookHeaders() {
            Map<String, String> headers = new HashMap<>();

            headers.put(UHOperation.UH_APP_ID_HEADER_NAME, UserHook.getAppId());
            headers.put(UHOperation.UH_APP_KEY_HEADER_NAME, UserHook.getApiKey());
            headers.put(UHOperation.UH_SDK_HEADER_NAME, UHOperation.UH_SDK_HEADER_PREFIX + UserHook.UH_SDK_VERSION);

            // add user header values if available
            if (UHInternal.getInstance().getUser().getUserId() != null) {
                headers.put(UHOperation.UH_USER_ID_HEADER_NAME, UHInternal.getInstance().getUser().getUserId());
            }
            if (UHInternal.getInstance().getUser().getUserKey() != null) {
                headers.put(UHOperation.UH_USER_KEY_HEADER_NAME, UHInternal.getInstance().getUser().getUserKey());
            }

            return headers;
        }
    }
}
