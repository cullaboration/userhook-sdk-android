/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import com.userhook.model.UHMessageMeta;

import java.util.HashMap;
import java.util.Map;

public class UHMessageTemplate {

    protected Map<String,String> cache;

    protected static UHMessageTemplate instance;

    private UHMessageTemplate() {
        cache = new HashMap<>();
    }

    public static UHMessageTemplate getInstance() {
        if (instance == null) {
            instance = new UHMessageTemplate();
        }

        return instance;
    }

    public void addToCache(String key, String value) {
        cache.put(key, value);
    }

    public boolean hasTemplate(String name) {
        return cache.containsKey(name);
    }

    public String renderTemplate(UHMessageMeta meta) {

        String html = cache.get(meta.getDisplayType());

        if (meta.getButton1() != null) {
            html = html.replaceAll("<!-- button1 -->", meta.getButton1().getTitle());

            if (meta.getButton1().getImage() != null) {
                html = html.replaceAll("<!-- image -->", meta.getButton1().getImage().getUrl());
            }
        }

        if (meta.getButton2() != null) {
            html = html.replaceAll("<!-- button2 -->", meta.getButton2().getTitle());
        }

        html = html.replaceAll("<!-- body -->", meta.getBody());

        return html;
    }

}
