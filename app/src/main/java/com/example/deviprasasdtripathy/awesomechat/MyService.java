package com.example.deviprasasdtripathy.awesomechat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        userRef.child("online").setValue("false");
    }

}
