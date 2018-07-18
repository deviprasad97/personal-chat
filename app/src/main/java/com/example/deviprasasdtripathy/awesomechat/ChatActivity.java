package com.example.deviprasasdtripathy.awesomechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String threadID;
    private String receiver_email;
    private String chat_msg,chat_user_name, type;
    private String currparticipant;
    private long time;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("notifications");
    private StorageReference mImageStorage;
    private String temp_key;
    private ArrayList <String> messageKey = new ArrayList<>();

    private ChatView chatView;
    private ImageButton imageMessage;
    private ImageView imageMessageView;
    private ListView chat_list;
    private final int PICK_IMAGE_REQUEST = 71;

    private String user_name;
    private CircleImageView toolbar_profile_icon;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Profile);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_view_toolbar);
        imageMessage = findViewById(R.id.pic_message);
        chat_list = findViewById(R.id.chat_list);
        imageMessageView = findViewById(R.id.image_message_view);
        toolbar_profile_icon = (CircleImageView) findViewById(R.id.toolbar_profile_icon);
        mImageStorage = FirebaseStorage.getInstance().getReference();
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
        try {
            uri = Uri.parse(getIntent().getExtras().get("photo_url").toString());

        }catch (Exception e){
            Log.e("photo_url", "not supplied");
        }
        setTitle(receiver_email);
        Picasso.get().load(uri).into(toolbar_profile_icon);
        root = FirebaseDatabase.getInstance().getReference().child("messages").child(threadID);
        root.keepSynced(true);
        user_name = user.getEmail();
        Log.e("threadID", threadID);
        chatView = (ChatView) findViewById(R.id.chat_view);
        imageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        registerForContextMenu(chat_list);
        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                Date date = new Date();
                DatabaseReference message_root = root.child(temp_key);
                message_root.keepSynced(true);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg", chatView.getTypedMessage());
                map2.put("time", date.getTime());
                map2.put("type", "text");
                map2.put("-"+user.getEmail().replace(".",""), "true");
                map2.put("-"+receiver_email.replace(".",""), "true");
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


    }

    @Override
    protected void onStop() {
        super.onStop();
        messageKey.clear();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.chat_list){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((AdapterView.AdapterContextMenuInfo)info).position;

        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("messages")
                .child(threadID).child(messageKey.get(position));
        switch (item.getItemId()){
            case R.id.chat_delete:
                Log.e("Delete", "Selected" + messageKey.get(position));

                Log.e("Delete", "Selected "+position);
                deleteRef.child("-"+user.getEmail().replace(".","")).setValue("false");
                chatView.removeMessage(position);
                messageKey.remove(position);
                Log.e("After Delete", messageKey.toString() + " Size " + messageKey.size());

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Log.e("Image Uri", uri.toString());
            temp_key = root.push().getKey();
            StorageReference filepath = mImageStorage.child("image_messages").child( temp_key + ".jpg");
            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                        profileStorageRef.child("image_messages/"+temp_key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download = uri.toString();
                                Log.e("Uri", download);
                                Map<String,Object> map = new HashMap<String, Object>();
                                root.updateChildren(map);
                                Date date = new Date();
                                DatabaseReference message_root = root.child(temp_key);
                                message_root.keepSynced(true);
                                Map<String,Object> map2 = new HashMap<String, Object>();
                                map2.put("name",user_name);
                                map2.put("msg", download);
                                map2.put("time", date.getTime());
                                map2.put("type", "image");
                                map2.put("-"+user.getEmail().replace(".",""), "true");
                                map2.put("-"+receiver_email.replace(".",""), "true");
                                message_root.updateChildren(map2);
                                chatView.getInputEditText().setText("");
                            }
                        });

                    }
                }
            });

        }
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        Log.e("message key->", messageKey.toString());
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            DataSnapshot part1, part2;
            String access;
            part1 = ((DataSnapshot)i.next());
            part2 = ((DataSnapshot)i.next());
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            time = (long)((DataSnapshot)i.next()).getValue();
            type = (String)((DataSnapshot)i.next()).getValue();

            if(part1.getKey().equals("-"+user.getEmail().replace(".", ""))){
                access = part1.getValue().toString();
            }else {
                access = part2.getValue().toString();
            }

            String data = "{\n\tchat_msg: " + chat_msg
                    + "\n\tchat_user_name: " + chat_user_name
                    + "\n\ttime: "  + time
                    + "\n\ttype: "  + type
                    + "\n\taccess: "+ access
                    +"\n}";

            Log.e("Data", data);

            //chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
            if(type.equals("text")){
                if(access.equals("true")){
                    messageKey.add(key);
                    if(user_name.equals(chat_user_name)){
                        co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.SENT);
                        chatView.addMessage(message);

                    }else {
                        co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.RECEIVED,uri);
                        chatView.addMessage(message);
                    }
                }
            }
            else if(type.equals("image")){
                Uri imageMessage = Uri.parse(chat_msg);
                Log.e("Chat Image", chat_msg);
                if(access.equals("true")) {
                    messageKey.add(key);
                    if (user_name.equals(chat_user_name)) {
                        co.intentservice.chatui.models.ChatMessage message = new ChatMessage(imageMessage, time, ChatMessage.Type.SENT);
                        chatView.addMessage(message);

                    } else {
                        co.intentservice.chatui.models.ChatMessage message = new ChatMessage(imageMessage, time, ChatMessage.Type.RECEIVED, uri);
                        chatView.addMessage(message);
                    }
                }
            }

        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, BottomNavigation.class);
        startActivity(intent);
        finish();
    }
}
