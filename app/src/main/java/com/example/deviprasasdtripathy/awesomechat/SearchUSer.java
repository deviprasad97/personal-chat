package com.example.deviprasasdtripathy.awesomechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.UUID;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchUSer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchUSer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchUSer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mUserDatabase;
    private FirebaseRecyclerAdapter<Users, SearchUSer.UsersViewHolder> firebaseRecyclerAdapter;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

    Context context;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchUSer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchUSer.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchUSer newInstance(String param1, String param2) {
        SearchUSer fragment = new SearchUSer();
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
        view = inflater.inflate(R.layout.fragment_search_u, container, false);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");
        mUserDatabase.keepSynced(true);

        // Activity Objects initialize
        mSearchField = (EditText) view.findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) view.findViewById(R.id.search_btn);
        mResultList = (RecyclerView) view.findViewById(R.id.result_list);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(context));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });

        return view;
    }


    private void firebaseUserSearch(String searchText) {

        Toast.makeText(context, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setLifecycleOwner(this)
                        .setQuery(firebaseSearchQuery, Users.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, SearchUSer.UsersViewHolder>(options) {
            @Override
            public SearchUSer.UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_layout, viewGroup, false);
                return new SearchUSer.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(SearchUSer.UsersViewHolder holder, int position, Users model) {
                if(!user.getEmail().equals(model.getEmail())){
                    holder.setDetails(context, model.getEmail());
                }else {
                    holder.setDetails(context, "curr");
                }

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


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
                //Log.e("Username", "Its Empty");
                user_name.setText("No User Found");
            }
            else if(userName.equals("curr")){
                user_name.setVisibility(View.GONE);
            }else{
                //Log.e("Username", userName);
                user_name.setText(userName);
            }
            user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.e("Clicked: ", user_name.getText().toString());
                    //appendThread(user_name.getText().toString(), user.getEmail());
                    openChatActivity(user_name.getText().toString());
                }
            });
        }

        public void openChatActivity(final String receiver_email){
            final String currentUser = user.getEmail();
            final ArrayList<String> participants = new ArrayList<>();
            Query firebaseSearchQuery = root.child("threads");
            firebaseSearchQuery.keepSynced(true);
            firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count= 0;
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        count++;
                        String data = snapshot.getValue().toString();
                        //Log.e("Count", count+"");
                        //Log.e("Data", data);

                        if(data.contains(receiver_email) && data.contains(currentUser)){

                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            intent.putExtra("threadID", snapshot.getRef().getKey());
                            intent.putExtra("receiver_email", receiver_email);


                            Query nameQuery = userRef;
                            nameQuery.keepSynced(true);
                            nameQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot each_user: dataSnapshot.getChildren()){
                                        if(each_user.child("email").getValue().toString().equals(receiver_email)){
                                            intent.putExtra("name", each_user.child("name").getValue().toString());
                                            intent.putExtra("uid", each_user.child("uid").getValue().toString());
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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
                        else if(!(data.contains(receiver_email) && data.contains(currentUser)) && count == dataSnapshot.getChildrenCount()) {
                            //Log.e("In else if","let's see");
                            //Log.e("Count", count+"");
                            ArrayList<String> members = new ArrayList<>();
                            members.add(receiver_email);
                            members.add(currentUser);
                            root = root.child("threads");
                            root.keepSynced(true);
                            String uniqueID = UUID.randomUUID().toString();
                            root.child(uniqueID).child(receiver_email.replace(".","")).setValue(true);
                            root.child(uniqueID).child(currentUser.replace(".","")).setValue(true);
                            root.child(uniqueID).child("members").setValue(members);
                            Intent intent = new Intent(context, ChatActivity.class);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
