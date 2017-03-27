/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.util;

import java.net.HttpURLConnection;

public class UHAsyncTestConnectionBuilder implements UHAsyncTask.UHAsyncConnectionBuilder {

    protected HttpURLConnection connection;

    public UHAsyncTestConnectionBuilder(HttpURLConnection conn) {
        this.connection = conn;
    }

    public HttpURLConnection createConnection(String url) {
        return this.connection;
    }
}
