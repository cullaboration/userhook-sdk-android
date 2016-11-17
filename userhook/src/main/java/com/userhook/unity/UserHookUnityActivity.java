/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.unity;

import android.content.Intent;

import com.unity3d.player.UnityPlayerActivity;


public class UserHookUnityActivity extends UnityPlayerActivity {

    /*
    needed to ensure push message clicks are tracked correctly
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }
}
