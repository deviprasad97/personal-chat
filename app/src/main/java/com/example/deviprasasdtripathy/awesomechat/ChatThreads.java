package com.example.deviprasasdtripathy.awesomechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ChatThreads extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mResultList;
    Context context;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserDatabaseRunner;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView.Adapter mAdapter;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    //UserRecord userRecord = FirebaseAuth.getInstance();
    private OnFragmentInteractionListener mListener;


    public ChatThreads() {
        // Required empty public constructor
    }

    public static ChatThreads newInstance(String param1, String param2) {
        ChatThreads fragment = new ChatThreads();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        view = inflater.inflate(R.layout.fragment_chat_threads, container, false);
        mResultList =  view.findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(context));
        mUserDatabase = FirebaseDatabase.getInstance().getReference("threads");
        mUserDatabase.keepSynced(true);
        mUserDatabaseRunner = FirebaseDatabase.getInstance().getReference("users");
        mUserDatabaseRunner.keepSynced(true);
        mResultList.setAdapter(firebaseRecyclerAdapter);
        //toolbar.setTitle("Awesome Chat");
        getThreads();



        return view;
    }
    public void getThreads(){
        ValueEventListener valueEventListener = mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> threads = new ArrayList<>();
                ArrayList<String> receiver = new ArrayList<>();
                Map<String, String> data;
                String record = "";
                List<Thread> t = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email1 = " ";
                    String email2 = " ";

                    record = ds.getValue().toString();
                    Log.e("Record", record);
                    //{members=[tripathy.devi@yahoo.com, hello@helloworld.com]}
                    if (record.contains(user.getEmail())) {
                        record = record.replace("{members=[", "");
                        record = record.replace(user.getEmail(), "");
                        record = record.replace(",", "");
                        record = record.replace("]", "");
                        record = record.replace("}", "").trim();
                        Log.e("Record replace", record);
                        receiver.add(record);
                        t.add(new Thread(record, ds.getKey()));

                    }
                }
                //
                SimpleRecyclerAdapter<Thread, CarBinder> adapter =
                        new SimpleRecyclerAdapter<>(new CarBinder());

                mResultList.setAdapter(adapter);
                adapter.setData(t);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public class CarBinder extends ItemBinder<Thread, CarBinder.CarViewHolder> {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        private DatabaseReference lastMessage = FirebaseDatabase.getInstance().getReference();
        private String lastMessageValue;
        private String access;
        private String sender;
        private Uri profilePath;

        @Override public CarViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            return new CarViewHolder(inflater.inflate(R.layout.thread_layout, parent, false));
        }

        @Override
        public void bind(final CarViewHolder holder, final Thread item) {
            try {
                holder.user_name.setText(item.getEmail());
            }catch (Exception e){
                Log.e("Raise", "No email");
            }
            holder.profileStorageRef.child("images/"+item.getEmail()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    profilePath = uri;
                    Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(uri).into(holder.profile_image);
                        }
                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Picasso.get().load(Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"))
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(holder.profile_image, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"))
                                            .into(holder.profile_image);
                                }
                            });
                }
            });
            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChatActivity(item.getEmail());
                }
            });
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChatActivity(item.getEmail());
                }
            });
            Query firebaseLastMessageQuery = lastMessage.child("messages").child(item.getThreadID());
            firebaseLastMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        access = ds.child("-"+user.getEmail().replace(".","")).getValue().toString();
                        sender = ds.child("name").getValue().toString();
                        lastMessageValue = ds.child("msg").getValue().toString();
                    }
                    if(lastMessageValue!=null && access.equals("true")){
                        if(!lastMessageValue.contains("https://firebasestorage.googleapis.com/")){
                            if(sender.equals(user.getEmail()))
                                holder.lastMessagView.setText("You: " + lastMessageValue);
                            else
                                holder.lastMessagView.setText(lastMessageValue);
                        }else {
                            if(sender.equals(user.getEmail()))
                                holder.lastMessagView.setText("You: Image");
                            else
                                holder.lastMessagView.setText("Image");
                        }
                    }else {
                        holder.lastMessagView.setText("No messages");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override public boolean canBindData(Object item) {
            return item instanceof Thread;
        }


        class CarViewHolder extends BaseViewHolder<Thread> {
            TextView user_name;
            TextView lastMessagView;
            CircleImageView profile_image;
            StorageReference profileStorageRef;
            RelativeLayout relativeLayout;
            public CarViewHolder(View itemView) {
                super(itemView);
                user_name = (TextView) itemView.findViewById(R.id.email_text);
                profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
                profileStorageRef = FirebaseStorage.getInstance().getReference();
                relativeLayout = itemView.findViewById(R.id.threadLayout);
                lastMessagView = itemView.findViewById(R.id.status_text);

            }

            // Normal ViewHolder code
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

                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            intent.putExtra("threadID", snapshot.getRef().getKey());
                            intent.putExtra("receiver_email", receiver_email);
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                            profileStorageRef.child("images/"+receiver_email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    intent.putExtra("photo_url", uri);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    intent.putExtra("photo_url", Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"));
                                    startActivity(intent);

                                }
                            });
                            break;
                        }
                        else if(!(data.contains(receiver_email) && data.contains(currentUser))
                                && count == dataSnapshot.getChildrenCount()) {
                            Log.e("In else if","let's see");
                            Log.e("Count", count+"");
                            ArrayList<String> members = new ArrayList<>();
                            members.add(receiver_email);
                            members.add(currentUser);
                            root = root.child("threads");
                            root.keepSynced(true);
                            String uniqueID = UUID.randomUUID().toString();
                            root.child(uniqueID).child("members").setValue(members);
                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            intent.putExtra("threadID", uniqueID);
                            intent.putExtra("receiver_email", receiver_email);
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference();
                            profileStorageRef.child("images/"+receiver_email).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    intent.putExtra("photo_url", uri);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    intent.putExtra("photo_url", Uri.parse("https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png"));
                                    startActivity(intent);
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
