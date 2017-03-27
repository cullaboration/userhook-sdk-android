/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */


package com.cullaboration.userhookdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.userhook.UserHook;

public class PurchaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Purchase Item");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button btnItem1 = (Button) findViewById(R.id.btnItem1);
        Button btnItem2 = (Button) findViewById(R.id.btnItem2);

        btnItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProduct("item1", 0.99);
            }
        });

        btnItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProduct("item2", 1.99);
            }
        });

    }


    protected void buyProduct(final String sku, final Number price) {


        UserHook.updatePurchasedItem(sku, price, new UserHook.UHSuccessListener() {
            @Override
            public void onSuccess() {
                Log.i(UserHook.TAG, "bought product: " + sku + " for: " + price);

                // fetch hook points from the server
                ((MainApplication)getApplication()).loadHookPoints("purchased");
            }
        });

    }
}
