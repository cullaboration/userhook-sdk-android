/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.push;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.userhook.UserHook;

public class UHRegistrationIntentService extends IntentService {

    private static final String TAG = "UHRegIntentService";

    public UHRegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            int senderId = UserHook.getResourceId("gcm_defaultSenderId","string");
            String token = instanceID.getToken(getString(senderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // send token to User Hook servers
            UserHook.registerPushToken(token);
            Log.i("uh", "gcm token = " + token);
        }
        catch (Exception e) {
            Log.e("uh", "Failed to complete token refresh", e);
        }
    }
}
