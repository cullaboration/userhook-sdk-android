package com.userhook.util;

import org.mockito.Mockito;

/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

public class UHOperationTestFactory implements UHOperation.Factory {

    protected UHOperation operation;

    public UHOperationTestFactory(UHOperation operation) {
        this.operation = operation;
    }

    public UHOperation build() {
        return operation;
    }

}
