/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.hookpoint;

import com.userhook.model.UHMessageMeta;

import org.json.JSONObject;

public class UHHookPointNPS extends UHHookPointMessage {

    public static final String DISPLAY_TYPE = "nps";
    public static final String DISPLAY_TYPE_FEEDBACK = "nps_feedback";

    protected UHHookPointNPS(JSONObject json) {
        super(json);


        if (meta == null) {
            meta = new UHMessageMeta();
        }

        meta.setDisplayType(DISPLAY_TYPE);
    }
}
