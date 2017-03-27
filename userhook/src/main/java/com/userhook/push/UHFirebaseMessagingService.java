/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.userhook.UserHook;

public class UHFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // check if this message originated from User Hook or another push provider
        if (UserHook.isPushFromUserHook(remoteMessage.getData())) {

            Notification notification = UserHook.handlePushMessage(getApplicationContext(), remoteMessage.getData());

            if (notification != null) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);
            }
        }
        else {
            // push is from a different push provider
            // use the appropriate logic from that push provider
            // to handle push message
        }

    }

}
