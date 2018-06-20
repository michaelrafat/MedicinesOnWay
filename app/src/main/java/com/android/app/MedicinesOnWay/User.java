package com.android.app.MedicinesOnWay;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by miche on 5/30/2018.
 */

@IgnoreExtraProperties
public class User {

    private String name;
    private String email;
    private String image;
    private String type;
    private String address;
    private String mobile;

    public User() {

    }

    public User(String name, String email, String image, String type, String address, String mobile) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.type = type;
        this.address = address;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }
}