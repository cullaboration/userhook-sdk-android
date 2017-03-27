/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook.model;

import android.util.Log;

import com.userhook.UserHook;

import org.json.JSONObject;

import java.io.Serializable;

public class UHPage implements Serializable {

    private String slug;
    private String name;

    public UHPage() {

    }

    public UHPage(JSONObject json) {

        try {
            if (json.has("slug")) {
                slug = json.getString("slug");
                name = json.getString("name");
            }
        }
        catch (Exception e) {
            Log.e(UserHook.TAG,"error parsing page json", e);
        }

    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }


    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UHPage uhPage = (UHPage) o;

        if (slug != null ? !slug.equals(uhPage.slug) : uhPage.slug != null) return false;
        return name != null ? name.equals(uhPage.name) : uhPage.name == null;

    }

    @Override
    public int hashCode() {
        int result = slug != null ? slug.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
