/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.cullaboration.userhookdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.userhook.hookpoint.UHHookPoint;
import com.userhook.model.UHMessageMetaButton;
import com.userhook.view.UHHostedPageActivity;
import com.userhook.util.UHOperation;
import com.userhook.model.UHPage;
import com.userhook.view.UHPromptView;
import com.userhook.util.UHUser;
import com.userhook.UserHook;

import org.xml.sax.XMLReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String GROUP_NAME = "userhook_demo";
    public static final String SCORE = "score";

    protected NavigationView navigationView;
    protected Map<String, UHPage> staticPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button btnCustomField = (Button) findViewById(R.id.btnCustomField);
        Button btnPurchase = (Button) findViewById(R.id.btnPurchase);
        Button btnReloadHookpoints = (Button) findViewById(R.id.btnReloadHookpoints);


        btnCustomField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomFieldActivity.class);
                startActivity(intent);
            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });

        btnReloadHookpoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHookPoints();
            }
        });


        staticPages = new HashMap<>();
        loadStaticPages();
    }

    /*
    If your activity uses launchMode="singleTop", you will need to override the onNewIntent
    with the code below to properly track push notifications opens
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // check for deep link

        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();

            // respond to deep link for url, userhookdemo://product
            if (uri.getHost().equals("product")) {
                Intent newIntent = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(newIntent);
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void loadStaticPages() {
        UserHook.fetchPageNames(new UHOperation.UHArrayListener<UHPage>() {
            @Override
            public void onSuccess(List<UHPage> items) {

                SubMenu staticMenu = navigationView.getMenu().addSubMenu("Static Pages");
                for (UHPage page : items) {
                    // add page to menu
                    staticMenu.add(page.getName());

                    // store the page item for use in the click listener
                    staticPages.put(page.getName(), page);
                }

                // hack to refresh menu
                MenuItem mi = navigationView.getMenu().getItem(navigationView.getMenu().size() - 1);
                mi.setTitle(mi.getTitle());
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (staticPages.containsKey(item.getTitle())) {

            UHPage page = staticPages.get(item.getTitle());
            Intent intent = new Intent(this, UHHostedPageActivity.class);
            intent.putExtra(UHHostedPageActivity.TYPE_PAGE, page);
            startActivity(intent);

        } else if (id == R.id.nav_feedback) {
            clickedFeedback();

        } else if (id == R.id.nav_rate) {

            UHMessageMetaButton button1 = new UHMessageMetaButton();
            button1.setTitle("Yes");
            button1.setOnClickListener(new UHMessageMetaButton.OnClickListener() {
                @Override
                public void onClick() {
                    showRatingPrompt();
                }
            });

            UHMessageMetaButton button2 = new UHMessageMetaButton();
            button2.setTitle("No");
            button2.setOnClickListener(new UHMessageMetaButton.OnClickListener() {
                @Override
                public void onClick() {
                    showFeedbackPrompt();
                }
            });

            UserHook.displayPrompt("We are glad you downloaded the app. Are you enjoying using it?", button1, button2);


        } else if (id == R.id.nav_clear_session) {

            clearSession();


        } else if (id == R.id.nav_clear_user) {

            clearUser();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if (tag.equals("ul") && !opening) output.append("\n");
            if (tag.equals("li") && opening) output.append("\n\t•");
        }
    }

    public void clickedFeedback() {

        UserHook.showFeedback();

    }

    public void showFeedbackPrompt() {

        UserHook.showFeedbackPrompt("We are sorry to hear that you aren't enjoying the app. Do you mind sending us some feedback on how to make it better?", "Sure", "Not Now");


    }

    public void showRatingPrompt() {

        UserHook.showRatingPrompt("Great! A rating or review really helps us. Would you mind leaving us one now?", "Sure", "Not Now");

    }

    public void loadHookPoints() {

        UserHook.fetchHookPoint(new UserHook.UHHookPointFetchListener() {
            @Override
            public void onSuccess(UHHookPoint hookPoint) {
                if (hookPoint != null) {
                    hookPoint.execute(MainActivity.this);
                }
            }

            @Override
            public void onError() {
                Log.e("userhook", "error fetching hookpoints");
            }
        });
    }

    public void addView(final View view) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                rootView.addView(view);
            }
        });
    }


    protected void clearSession() {

        // clear user preferences
        SharedPreferences preferences = getSharedPreferences(GROUP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // force the creation of a new session. In a production app, you would never do this. The SDK
        // handles all session tracking.
        UHOperation operation = new UHOperation();
        operation.createSession(null);

        Toast.makeText(this, "Your session data has been cleared", Toast.LENGTH_LONG).show();
    }

    protected void clearUser() {

        // clear user preferences
        SharedPreferences preferences = getSharedPreferences(GROUP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // used just in the demo app. In a production app, there is no reason to call this
        UHUser.clear();

        // force the creation of a new session. In a production app, you would never do this. The SDK
        // handles all session tracking.
        UHOperation operation = new UHOperation();
        operation.createSession(null);

        Toast.makeText(this, "User Hook user has been cleared", Toast.LENGTH_LONG).show();
    }

}
