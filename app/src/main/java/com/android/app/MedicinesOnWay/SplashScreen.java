package com.android.app.MedicinesOnWay;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;

import com.android.app.MedicinesOnWay.activity.LoginScreen;

public class SplashScreen extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
        final ImageView logoImageView = (ImageView) findViewById(R.id.logo_image_view);
        logoImageView.setBackgroundResource(R.drawable.logo_screen);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    AnimationDrawable animationDrawable = (AnimationDrawable) logoImageView.getBackground();
                    animationDrawable.start();
                    sleep(1000);
                    animationDrawable.stop();
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }
}