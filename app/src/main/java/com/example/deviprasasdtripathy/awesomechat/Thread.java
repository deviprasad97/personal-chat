package com.example.deviprasasdtripathy.awesomechat;

import android.util.Log;

import java.util.List;

public class Thread {

    public String name, email, uid;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String image) {
        this.email = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String status) {
        this.uid = status;
    }

    public Thread(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }
    public Thread(String email) {
        this.email = email;
    }
}
