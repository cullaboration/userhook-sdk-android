/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import java.io.Serializable;

public class UHPage implements Serializable {

    private String slug;
    private String name;

    public UHPage(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }


    public String getName() {
        return name;
    }

}
