package com.example.deviprasasdtripathy.awesomechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class
MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth user = FirebaseAuth.getInstance();
        if(user != null){
            Intent intent = new Intent(MainActivity.this, BottomNavigation.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
