/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.cullaboration.userhookdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.userhook.UserHook;
import com.userhook.hookpoint.UHHookPoint;

import java.util.HashMap;
import java.util.Map;


public class CustomFieldActivity extends AppCompatActivity {

    protected TextView scoreLabel;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Custom Field");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        scoreLabel = (TextView) findViewById(R.id.scoreLabel);

        Button btnAddScore = (Button) findViewById(R.id.btnAddScore);
        btnAddScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences prefs = getSharedPreferences(MainActivity.GROUP_NAME, Context.MODE_PRIVATE);

                int currentScore = prefs.getInt(MainActivity.SCORE, 0);
                currentScore++;

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(MainActivity.SCORE, currentScore);
                editor.apply();

                updateScoreLabel(currentScore);

                Map<String,Object> customFields = new HashMap<>();
                customFields.put("score", currentScore);

                UserHook.updateCustomFields(customFields, new UserHook.UHSuccessListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(UserHook.TAG, "custom fields updated");

                        // fetch hook points from the server
                        ((MainApplication)getApplication()).loadHookPoints("score_updated");

                    }
                });
            }
        });

    }

    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(MainActivity.GROUP_NAME, Context.MODE_PRIVATE);
        updateScoreLabel(prefs.getInt(MainActivity.SCORE,0));

    }

    protected void updateScoreLabel(int score) {
        scoreLabel.setText(score+"");
    }
}
