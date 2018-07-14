package com.example.deviprasasdtripathy.awesomechat;

import android.util.Log;

import java.util.List;

public class Thread {
    public String members;
    public String threadID;
    private static Thread thread;

    public Thread(){

    }

    public String getMembers() {
        return members;
    }

    public String getThreadID() {
        return threadID;
    }

    public void setThread(String email, String threadID){
        this.members = email;
        this.threadID = threadID;
    }

    public Thread(String email) {
        Log.e("Invoked", "Users Class with email:");
        this.members = email;
    }
    public static Thread getInstance(){
        if(thread == null){
            thread = new Thread();
        }
        return thread;
    }
}
