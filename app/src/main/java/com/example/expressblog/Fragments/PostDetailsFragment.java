package com.example.expressblog.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.expressblog.Adapters.CommentAdapter;
import com.example.expressblog.Models.Comment;
import com.example.expressblog.Models.UserModelClass;
import com.example.expressblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class PostDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ImageView imgPost, imgUserPost, imgCurrentUser;
    private TextView txtPostDesc, txtPostDateName, txtPostTitle;
    private EditText editTextComment;
    private ImageView btnAddComment;
    private String PostKey;
    FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView RvComment;
    private CommentAdapter commentAdapter;
    private List<Comment> listComment;
    private TextView postUser, postUserBio;
    private String uimg = "default";
    private static String COMMENT_KEY = "Comment";


    private String postImage;
    private String postTitle;
    private String userPostImage;
    private String postDesc;
    private String userName;
    private String sum;
    private String postKey;
    private String postDate;

    private String mParam1;
    private String mParam2;

    public PostDetailsFragment() {
    }
    public PostDetailsFragment(String pimg,String ptitle,String uimg,String pDesc,String uname,String usum,String key,String pDate) {
        postImage = pimg;
        postTitle = ptitle;
        userPostImage = uimg;
        postDesc = pDesc;
        userName = uname;
        sum = usum;
        postKey = key;
        postDate = pDate;
    }

    public static PostDetailsFragment newInstance(String param1, String param2) {
        PostDetailsFragment fragment = new PostDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        postUser = view.findViewById(R.id.user_name2);
        postUserBio = view.findViewById(R.id.user_bio);
        // let's set the statue bar to transparent

        RvComment = view.findViewById(R.id.rv_comment);
        imgPost = view.findViewById(R.id.post_detail_img);
        imgUserPost = view.findViewById(R.id.post_detail_user_img);
        imgCurrentUser = view.findViewById(R.id.post_detail_currentuser_img);

        txtPostTitle = view.findViewById(R.id.post_detail_title);
        txtPostDesc = view.findViewById(R.id.post_detail_desc);
        txtPostDateName = view.findViewById(R.id.post_detail_date_name);

        editTextComment = view.findViewById(R.id.post_detail_comment);
        btnAddComment = view.findViewById(R.id.post_detail_add_comment_btn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        registerForContextMenu(RvComment);
        // add Comment button click listner

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                final DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();
                Log.e("Done..","Master");

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("UserDetails").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Done..","Master ....In DATA");
                        if (snapshot.exists()) {
                            Log.e("Enter","Snapshot");
                            UserModelClass userProfileModel = snapshot.getValue(UserModelClass.class);
                            String comment_content = editTextComment.getText().toString();
                            String uid = userProfileModel.getUid();
                            String uname = userProfileModel.getUserName();
                            Log.e("Enter 2.....","..."+comment_content+" ......"+uid+" ......."+uname+"  ......."+COMMENT_KEY);

                            if (userProfileModel.getImageURI() != "default") {
                                uimg = userProfileModel.getImageURI();
                            }
                            Comment comment = new Comment(comment_content, uid, uimg, uname);
                            commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showMessage("comment added");
                                    editTextComment.setText("");
                                    btnAddComment.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage("fail to add comment : " + e.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        Glide.with(this).load(postImage).into(imgPost);

        txtPostTitle.setText(postTitle);

        assert userPostImage != null;
        if (!userPostImage.equals("default")) {
            Glide.with(this).load(userPostImage).into(imgUserPost);
        } else {
            Glide.with(this).load(R.mipmap.user).into(imgUserPost);

        }

        txtPostDesc.setText(postDesc);


        postUser.setText(userName);

        postUserBio.setText(sum);

        // setcomment user image
        if (!uimg.equals("default")) {
            Glide.with(this).load(uimg).into(imgCurrentUser);

        }

        PostKey = postKey;
        String date = timestampToString(postDate);
        txtPostDateName.setText(date);


        // ini Recyclerview Comment
        iniRvComment();


        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
       getActivity().getMenuInflater().inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item3_p)
        {
            Log.e("Item","selected");
        }

        return super.onContextItemSelected(item);
    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);

                }

                commentAdapter = new CommentAdapter(getActivity(), listComment);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showMessage(String message) {

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

    }


    private String timestampToString(String time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;


    }



}