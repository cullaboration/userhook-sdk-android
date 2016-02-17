/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.push;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

public class UHInstanceIDListenerService extends InstanceIDListenerService {


    @Override
    public void onTokenRefresh() {

        Intent intent = new Intent(this, UHRegistrationIntentService.class);
        startService(intent);
    }
}
