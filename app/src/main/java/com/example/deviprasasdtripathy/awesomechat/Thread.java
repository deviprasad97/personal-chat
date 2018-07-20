package com.example.deviprasasdtripathy.awesomechat;

import android.util.Log;

import java.util.List;

public class Thread {

    public String name, email, uid, threadID, imageUrl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThreadID() {
        return threadID;
    }

    public void setThreadID(String threadID) {
        this.threadID = threadID;
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
    public Thread(String email, String threadID) {
        this.email = email;
        this.threadID = threadID;
    }
}
