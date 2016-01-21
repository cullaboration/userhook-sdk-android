/**
 * Copyright (c) 2015 - present, Cullaboration Media, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.userhook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class UHPromptView extends RelativeLayout {

    protected TextView label;
    protected Button positiveButton, negativeButton;
    protected int layoutResource;

    public UHPromptView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UHPromptView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UHPromptView(Context context) {
        super(context);
        if (UserHook.getCustomPromptLayout() != 0) {
            this.layoutResource = UserHook.getCustomPromptLayout();
        }
        else {
            this.layoutResource = UserHook.getResourceId("uh_promptview","layout");
        }
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(layoutResource, this);

        if (!isInEditMode()) {
            View overlay = findViewById(UserHook.getResourceId("overlay","id"));
            overlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialog();
                }
            });

            label = (TextView)findViewById(UserHook.getResourceId("label","id"));

            View dialog = findViewById(UserHook.getResourceId("dialog","id"));
            dialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // consume click so overlay click isn't called
                }
            });

            positiveButton = (Button)findViewById(UserHook.getResourceId("positiveButton","id"));


            negativeButton = (Button)findViewById(UserHook.getResourceId("negativeButton","id"));
            negativeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDialog();
                }
            });


        }

    }

    public void showDialog() {

        int overlayId = UserHook.getResourceId("overlay", "id");
        int dialogId = UserHook.getResourceId("dialog", "id");

        int overlayInId = UserHook.getResourceId("uh_overlay_in","anim");
        int dialogInId = UserHook.getResourceId("uh_dialog_in","anim");

        findViewById(overlayId).startAnimation(AnimationUtils.loadAnimation(getContext(), overlayInId));
        findViewById(dialogId).startAnimation(AnimationUtils.loadAnimation(getContext(), dialogInId));
    }

    public void hideDialog() {


        int overlayId = UserHook.getResourceId("overlay", "id");
        int dialogId = UserHook.getResourceId("dialog", "id");

        int overlayOutId = UserHook.getResourceId("uh_overlay_out","anim");
        int dialogOutId = UserHook.getResourceId("uh_dialog_out","anim");

        findViewById(overlayId).startAnimation(AnimationUtils.loadAnimation(getContext(), overlayOutId));

        Animation a = AnimationUtils.loadAnimation(getContext(), dialogOutId);

        final View thisView = this;
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getRootView().post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup) getParent()).removeView(thisView);
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(dialogId).startAnimation(a);
    }




    public TextView getLabel() {
        return label;
    }

    public Button getNegativeButton() {
        return negativeButton;
    }

    public Button getPositiveButton() {
        return positiveButton;
    }
}
