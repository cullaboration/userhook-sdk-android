/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import java.util.Map;

public class UHPostAsyncTask extends UHAsyncTask {

    public UHPostAsyncTask(Map<String, Object> params, UHAsyncTaskListener listener) {
        super(params, listener);
        this.method = "POST";
    }

    public UHPostAsyncTask(String queryString, UHAsyncTaskListener listener) {
        super(queryString, listener);
        this.method = "POST";
    }
}
