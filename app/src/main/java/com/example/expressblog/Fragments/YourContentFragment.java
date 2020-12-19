package com.example.expressblog.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.expressblog.Adapters.UserProfilePostAdapter;
import com.example.expressblog.Models.PostModel;
import com.example.expressblog.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class YourContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView postRecyclerView;
    private UserProfilePostAdapter UserProfilePostAdapter;

    private List<PostModel> postList;

    private EditText search_edit;
    private ImageView search_btn;


    private String uid="";
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase firebaseDatabase;

    public YourContentFragment() {

    }
    public YourContentFragment(String id) {
        uid = id;
    }


    public static YourContentFragment newInstance(String param1, String param2) {
        YourContentFragment fragment = new YourContentFragment();
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
        firebaseDatabase =FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_your_content, container, false);

        search_edit = view.findViewById(R.id.search);
        search_btn = view.findViewById(R.id.search_my_post_img);
        postRecyclerView = view.findViewById(R.id.your_content_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);


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
                UserProfilePostAdapter = new UserProfilePostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(UserProfilePostAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
