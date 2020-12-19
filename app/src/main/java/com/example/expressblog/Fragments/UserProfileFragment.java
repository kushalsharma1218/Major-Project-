package com.example.expressblog.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.expressblog.Adapters.UserProfilePostAdapter;
import com.example.expressblog.Models.FollowoModel;
import com.example.expressblog.Models.PostModel;
import com.example.expressblog.Models.UserModelClass;
import com.example.expressblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {


    TextView follow_btn;
    RecyclerView postRecyclerView;
    UserProfilePostAdapter userProfilePostAdapter;

    List<PostModel> postList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String uid="";

    private String mParam1;
    private String mParam2;


    CircleImageView user_profile;
    TextView username,title,summary,about,email,website;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReferencePost;
    public UserProfileFragment() {
        // Required empty public constructor
    }


    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public UserProfileFragment(String id){
        uid = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseDatabase =FirebaseDatabase.getInstance();
        Toast.makeText(getContext(),uid,Toast.LENGTH_SHORT).show();
        databaseReference = firebaseDatabase.getReference("Users").child(uid);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_profile, container, false);
        postRecyclerView = view.findViewById(R.id.recyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        user_profile = view.findViewById(R.id.imageview_profile_u);
        follow_btn = view.findViewById(R.id.follow_unfollow_btn);
        username = view.findViewById(R.id.usernameP);
        title = view.findViewById(R.id.title);
        summary = view.findViewById(R.id.summary);
        about = view.findViewById(R.id.about);
        email = view.findViewById(R.id.email_p);
        website = view.findViewById(R.id.website_t);
        databaseReference.child("UserDetails").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModelClass userProfileModel = snapshot.getValue(UserModelClass.class);
                if (userProfileModel != null) {
                    if(!userProfileModel.getImageURI().equals("default")) {
                        Glide.with(requireContext()).load(userProfileModel.getImageURI()).into(user_profile);
                    }
                    else
                        {
                            Glide.with(getContext()).load(R.mipmap.user).into(user_profile);

                        }
                    username.setText(userProfileModel.getUserName());
                    title.setText(userProfileModel.getName());
                    summary.setText(userProfileModel.getSummary());
                    about.setText(userProfileModel.getAbout());
                    email.setText(userProfileModel.getEmail());
                }

                follow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FollowoModel followoModel = new FollowoModel(FirebaseAuth.getInstance().getUid());
                        FirebaseDatabase.getInstance().getReference("Users").child("Followers").setValue(followoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                follow_btn.setText("UNFOLLOW");
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseDatabase.getReference().child("Users").child(uid).child("PostDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    postList.add(post);

                }
                userProfilePostAdapter = new UserProfilePostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(userProfilePostAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
