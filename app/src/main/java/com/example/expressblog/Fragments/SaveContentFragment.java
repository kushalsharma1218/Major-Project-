package com.example.expressblog.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressblog.Adapters.SavedContentAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveContentFragment extends Fragment {

    RecyclerView postRecyclerView;
    SavedContentAdapter savedContentAdapter;

    List<PostModel> postList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String uid = "";
    private String mParam1;
    private String mParam2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReferencePost;

    public SaveContentFragment() {

    }
    public SaveContentFragment(String id) {
        uid = id;
    }


    public static SaveContentFragment newInstance(String param1, String param2) {
        SaveContentFragment fragment = new SaveContentFragment();
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

        View view =  inflater.inflate(R.layout.fragment_save_content, container, false);

        postRecyclerView = view.findViewById(R.id.saveContentVoew);
        postRecyclerView.setHasFixedSize(true);


        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("User id",uid);
        firebaseDatabase.getReference().child("Users").child(uid).child("SavedPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                Log.e("SaveContent","in onData change");

                for(DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    Log.e("SaveContent","In for");
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    postList.add(post);
                }
                savedContentAdapter = new SavedContentAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(savedContentAdapter);
                postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
