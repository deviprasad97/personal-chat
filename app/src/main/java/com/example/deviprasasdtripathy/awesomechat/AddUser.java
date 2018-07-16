package com.example.deviprasasdtripathy.awesomechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

public class AddUser extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    Toolbar toolbar;
    TextView toolbar_add_user;
    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting Layout for Add User Activity
        setTheme(R.style.Search);
        this.setContentView(R.layout.activity_add_user);

        // Firebase Initialize
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Activity Objects initialize
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        toolbar = findViewById(R.id.bottom_nav_toolbar);
        toolbar_add_user = findViewById(R.id.toolbar_add_user);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));



        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });
        toolbar_add_user.setText("Search");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(AddUser.this, BottomNavigation.class);
        startActivity(intent);
        finish();
    }
    private void firebaseUserSearch(String searchText) {

        Toast.makeText(AddUser.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setLifecycleOwner(this)
                        .setQuery(firebaseSearchQuery, Users.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_layout, viewGroup, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                holder.setDetails(getApplicationContext(), model.getEmail());
            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }
    // View Holder Class

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, final String userName){

            final TextView user_name = (TextView) mView.findViewById(R.id.email_text);
            user_name.setLinksClickable(true);
            if(userName == null || userName.isEmpty() || userName.equals(" ")){
                Log.e("Username", "Its Empty");
                user_name.setText("No User Found");
            }else{
                Log.e("Username", userName);
                user_name.setText(userName);
            }
            user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Clicked: ", user_name.getText().toString());
                    //appendThread(user_name.getText().toString(), user.getEmail());
                    openChatActivity(user_name.getText().toString());
                }
            });
        }

        public void openChatActivity(final String receiver_email){
            final String currentUser = user.getEmail();
            final ArrayList<String> participants = new ArrayList<>();
            Query firebaseSearchQuery = root.child("threads");
            firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count= 0;
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        count++;
                        String data = snapshot.getValue().toString();
                        Log.e("Count", count+"");
                        Log.e("Data", data);

                        if(data.contains(receiver_email) && data.contains(currentUser)){

                            Intent intent = new Intent(AddUser.this, ChatActivity.class);
                            intent.putExtra("threadID", snapshot.getRef().getKey());
                            intent.putExtra("receiver_email", receiver_email);
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                            profileStorageRef.child("images/"+receiver_email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    intent.putExtra("photo_url", uri);
                                    startActivity(intent);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    intent.putExtra("photo_url", Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            break;
                        }
                        else if(!(data.contains(receiver_email) && data.contains(currentUser)) && count == dataSnapshot.getChildrenCount()) {
                            Log.e("In else if","let's see");
                            Log.e("Count", count+"");
                            ArrayList<String> members = new ArrayList<>();
                            members.add(receiver_email);
                            members.add(currentUser);
                            root = root.child("threads");
                            String uniqueID = UUID.randomUUID().toString();
                            root.child(uniqueID).child("members").setValue(members);
                            Intent intent = new Intent(AddUser.this, ChatActivity.class);
                            intent.putExtra("threadID", uniqueID);
                            intent.putExtra("receiver_email", receiver_email);
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                            profileStorageRef.child("images/"+receiver_email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    intent.putExtra("photo_url", uri);
                                    startActivity(intent);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    intent.putExtra("photo_url", Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
