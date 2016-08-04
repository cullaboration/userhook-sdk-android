/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPointMessage;
import com.userhook.model.UHMessageMeta;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.util.UHAsyncTask;
import com.userhook.util.UHJsonUtils;
import com.userhook.util.UHMessageTemplate;
import com.userhook.util.UHPostAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class UHMessageView extends RelativeLayout {

    protected View overlay;
    protected View contentView;

    protected UHHookPointMessage hookpoint;
    protected UHMessageMeta meta;

    protected boolean contentLoaded;
    protected boolean showAfterLoad;

    protected int dialogWidth = 280; // in dp

    public static final String UH_MESSAGE_PATH = "/message";

    public UHMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UHMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UHMessageView(Context context) {
        super(context);

        init(context);
    }

    public UHMessageView(Context context, UHHookPointMessage hookpoint) {
        super(context);
        init(context);

        this.hookpoint = hookpoint;
        this.meta = hookpoint.getMeta();

        Map<String, Object> params = new HashMap<>();
        params.put("id", hookpoint.getId());

        loadMessage(params);
    }

    public UHMessageView(Context context, UHMessageMeta meta) {
        super(context);
        init(context);

        this.meta = meta;

        Map<String, Object> params = new HashMap<>();
        params.put("meta", meta.toJSONString());

        loadMessage(params);
    }

    private void init(Context context) {


        if (!isInEditMode()) {
            overlay = new LinearLayout(context);
            // set a transparent black background
            overlay.setBackgroundColor(Color.parseColor("#99000000"));
            overlay.setVisibility(GONE);

            addView(overlay, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            overlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialog();
                }
            });

        }

    }

    public void showDialog() {

        if (contentLoaded) {

            int overlayInId = UserHook.getResourceId("uh_overlay_in", "anim");
            int dialogInId = UserHook.getResourceId("uh_dialog_in", "anim");

            overlay.setVisibility(VISIBLE);
            overlay.startAnimation(AnimationUtils.loadAnimation(getContext(), overlayInId));
            if (contentView != null) {
                contentView.startAnimation(AnimationUtils.loadAnimation(getContext(), dialogInId));
            }
        } else {
            showAfterLoad = true;
        }
    }

    public void hideDialog() {


        int overlayOutId = UserHook.getResourceId("uh_overlay_out", "anim");
        int dialogOutId = UserHook.getResourceId("uh_dialog_out", "anim");

        if (contentView != null) {
            contentView.startAnimation(AnimationUtils.loadAnimation(getContext(), dialogOutId));
        }

        Animation a = AnimationUtils.loadAnimation(getContext(), overlayOutId);

        final View thisView = this;
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getRootView().post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup) getParent()).removeView(thisView);
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        overlay.startAnimation(a);

    }


    protected void loadMessage(Map<String, Object> params) {

        if (meta.getDisplayType().equals(UHMessageMeta.TYPE_IMAGE)) {

            if (meta.getButton1() != null && meta.getButton1().getImage() != null && meta.getButton1().getImage().getUrl() != null) {

                AsyncTask task = new AsyncTask<Object, Void, Drawable>() {


                    @Override
                    protected Drawable doInBackground(Object... params) {
                        Drawable drawable = null;

                        try {

                            URL url = new URL((String) params[0]);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            InputStream is = conn.getInputStream();

                            drawable = Drawable.createFromStream(is, "src");

                            int height = drawable.getIntrinsicHeight();
                            int width = drawable.getIntrinsicWidth();


                            drawable.setBounds(0, 0, width, height);

                        } catch (Exception e) {
                            Log.e(UserHook.TAG, "error download message image", e);
                        }


                        return drawable;
                    }

                    @Override
                    protected void onPostExecute(Drawable result) {

                        if (result != null) {

                            // size image to fit inside the view
                            int screenHeight = getResources().getDisplayMetrics().heightPixels;
                            int screenWidth = getResources().getDisplayMetrics().widthPixels;

                            int heightGutter = 40;
                            int widthGutter = 40;

                            int screenSpaceHeight = screenHeight - heightGutter * 2;
                            int screenSpaceWidth = screenWidth - widthGutter * 2;

                            float height = result.getIntrinsicHeight();
                            float width = result.getIntrinsicWidth();
                            float aspect = height / width;

                            if (height > screenSpaceHeight) {
                                height = screenHeight;
                                width = height / aspect;
                            }

                            if (width > screenSpaceWidth) {
                                width = screenSpaceWidth;
                                height = width * aspect;
                            }

                            ImageView imageView = new ImageView(getContext());
                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            imageView.setImageDrawable(result);

                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) width, (int) height);
                            layoutParams.addRule(CENTER_IN_PARENT);
                            addView(imageView, layoutParams);

                            // add click handler to image
                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (meta.getButton1() != null) {
                                        clickedButton(meta.getButton1());
                                    }
                                }
                            });

                            contentLoaded = true;

                            if (showAfterLoad) {
                                showDialog();
                            }

                        }
                    }
                };

                task.execute(meta.getButton1().getImage().getUrl());


            }

        } else if (UHMessageTemplate.getInstance().hasTemplate(meta.getDisplayType())) {

            String html = UHMessageTemplate.getInstance().renderTemplate(meta);

            loadWebViewContent(html);


            if (showAfterLoad) {
                showDialog();
            }

        } else {

            UHPostAsyncTask asyncTask = new UHPostAsyncTask(params, new UHAsyncTask.UHAsyncTaskListener() {
                @Override
                public void onSuccess(String result) {

                    if (result != null) {

                        loadWebViewContent(result);

                    }

                    if (showAfterLoad) {
                        showDialog();
                    }
                }
            });

            asyncTask.execute(UserHook.UH_HOST_URL + UH_MESSAGE_PATH);

        }

    }

    protected void loadWebViewContent(String html) {

        WebView webView = new WebView(getContext());
        webView.setWebViewClient(new MessageWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        // find dialog width in pixels
        final float scale = getResources().getDisplayMetrics().density;
        int width = (int) (dialogWidth * scale);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(webView, layoutParams);

        webView.loadData(html, "text/html", "utf-8");
        contentView = webView;

        contentLoaded = true;
    }

    protected void clickedButton(UHMessageMetaButton button) {

        if (button == null) {
            return;
        } else if (button.getOnClickListener() != null) {
            button.getOnClickListener().onClick();
        } else if (button.getClick().equals(UHMessageMeta.CLICK_RATE)) {
            UserHook.rateThisApp();
        } else if (button.getClick().equals(UHMessageMeta.CLICK_FEEDBACK)) {
            UserHook.showFeedback();
        } else if (button.getClick().equals(UHMessageMeta.CLICK_SURVEY)) {
            UserHook.showSurvey(button.getSurvey(), button.getSurvey_title(), hookpoint);
        } else if (button.getClick().equals(UHMessageMeta.CLICK_ACTION)) {

            if (button.getPayload() != null) {

                try {

                    JSONObject payloadJson = new JSONObject(button.getPayload());
                    Map<String, Object> payload = UHJsonUtils.toMap(payloadJson);

                    UserHook.actionReceived(UserHook.getActivityLifecycle().getCurrentActivity(), payload);

                } catch (JSONException je) {
                    Log.e(UserHook.TAG, "error parsing hook point payload json", je);
                }

            }
        } else if (button.getClick().equals(UHMessageMeta.CLICK_URI)) {

            if (button.getUri() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(button.getUri()));
                UserHook.getActivityLifecycle().getCurrentActivity().startActivity(intent);
            }
        }

        if (hookpoint != null) {
            UserHook.trackHookPointInteraction(hookpoint);
        }

        hideDialog();

    }

    private class MessageWebViewClient extends WebViewClient {


        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(UserHook.UH_URL_SCHEMA)) {

                Uri uri = Uri.parse(url);

                if (uri.getHost().equals("close")) {
                    hideDialog();
                }

                UHMessageMetaButton button = null;

                if (uri.getHost().equals("button1")) {
                    button = meta.getButton1();
                } else if (uri.getHost().equals("button2")) {
                    button = meta.getButton2();
                }

                clickedButton(button);

                return true;
            }

            return false;
        }
    }

}
