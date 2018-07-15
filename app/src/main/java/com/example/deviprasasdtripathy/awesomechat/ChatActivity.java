package com.example.deviprasasdtripathy.awesomechat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    private String threadID;
    private String receiver_email;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String temp_key;
    ChatView chatView;
    private String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Profile);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_view_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        threadID = getIntent().getExtras().get("threadID").toString();
        receiver_email = getIntent().getExtras().get("receiver_email").toString();

        setTitle(receiver_email);

        root = FirebaseDatabase.getInstance().getReference().child("messages").child(threadID);
        user_name = user.getEmail();
        Log.e("threadID", threadID);
        chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                Date date = new Date();
                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg", chatView.getTypedMessage());
                map2.put("time", date.getTime());
                //input_msg.setText("");

                message_root.updateChildren(map2);
                chatView.getInputEditText().setText("");
                return false;
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //root = root.child("chats").child(room_name).child("messages");
    }

    private String chat_msg,chat_user_name, stime;
    private long time;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            time = (long)((DataSnapshot)i.next()).getValue();


            //chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
            if(user_name.equals(chat_user_name)){
                co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.SENT);
                chatView.addMessage(message);

            }else {
                co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.RECEIVED);
                chatView.addMessage(message);
            }
        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, BottomNavigation.class);
        startActivity(intent);
        finish();
    }
}
