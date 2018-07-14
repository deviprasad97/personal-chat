package com.example.deviprasasdtripathy.awesomechat;

import android.util.Log;

public class Users {

    public String email;

    public Users(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Users(String email) {
        Log.e("Invoked", "Users Class with email:"  + email);
        this.email = email;
    }
}
