package com.example.expressblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expressblog.Activities.Request_Post;
import com.example.expressblog.Fragments.PostDetailsFragment;
import com.example.expressblog.Fragments.UserProfileFragment;
import com.example.expressblog.Models.Post;
import com.example.expressblog.Models.UserModelClass;
import com.example.expressblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private Context mContext;
    private List<Post> mData;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
    private DatabaseReference databaseReferenceall = firebaseDatabase.getReference("Posts");
    private boolean mProcessCkick = false;
    private boolean mProcessCkick2 = false;


    private long saveCounter = 0;

    private String postUserSum;
    private String postUserName;
    private String imgURI = "default";


    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        if (mData.get(position).getPicture() != null && mData.get(position).getPicture().equals(" ")) holder.imgPost.setVisibility(View.GONE);
        else {
            Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        }

        holder.postDesc.setText(mData.get(position).getDescription());


        setUserDetails(databaseReference.child(mData.get(position).getUserId()).child("UserDetails"), holder);
        final DatabaseReference databaseReferencelike = databaseReferenceall.child(mData.get(position).getPostKey()).child("Likes");
        final DatabaseReference databaseReferencedislike = databaseReferenceall.child(mData.get(position).getPostKey()).child("DisLikes");


        if (mData.get(position).getLikes() != null)
            holder.like.setText(String.valueOf(mData.get(position).getLikes().size()));
        if (mData.get(position).getDisLikes() != null)
            holder.dislike.setText(String.valueOf(mData.get(position).getDisLikes().size()));

        holder.upvoteImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(final View v) {
                mProcessCkick = true;
                if (mProcessCkick) {
                    databaseReferencelike.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue("Liked");
                    databaseReferencelike.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists() && mProcessCkick) {

                                holder.like.setText(String.valueOf(snapshot.getChildrenCount()));
                                mProcessCkick = false;
                            } else {
                                holder.like.setText(String.valueOf(0));
                                mProcessCkick = false;
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        holder.downvoteImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                mProcessCkick2 = true;
                if (mProcessCkick2) {
                    databaseReferencedislike.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue("Disliked");
                    databaseReferencedislike.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.dislike.setText(String.valueOf(snapshot.getChildrenCount()));
                                mProcessCkick2 = false;
                            } else {
                                holder.dislike.setText(String.valueOf(0));
                                mProcessCkick2 = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        databaseReference.child(mData.get(position).getPostKey()).child("PostID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    holder.imageSave_fill.setVisibility(View.VISIBLE);
                    holder.savePost.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.savePost.getVisibility() == View.VISIBLE) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("SavedPosts");
                    databaseReference.child(mData.get(position).getPostKey()).child("PostID").setValue(mData.get(position).getPostKey());
                    holder.savePost.setVisibility(View.INVISIBLE);
                    holder.imageSave_fill.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.imgPostProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                Fragment myFragment = new UserProfileFragment(mData.get(position).getUserId());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();

            }
        });

        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new UserProfileFragment(mData.get(position).getUserId());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();

            }
        });

        holder.post_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,holder.post_options);
                popupMenu.inflate(R.menu.post_options);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1_p:
                                //handle menu1 click
                                return true;
                            case R.id.item2_p:
                                //handle menu2 click
                                return true;
                            case R.id.item3_p:
                                //handle menu3 click
                                return true;
                            case R.id.improve:
                                Intent in = new Intent(mContext, Request_Post.class);
                                in.putExtra("post_id",mData.get(position).getPostKey());
                                mContext.startActivity(in);
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });


    }



    private void setUserDetails(DatabaseReference databaseReference1, final MyViewHolder holder) {
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModelClass userModelClass = snapshot.getValue(UserModelClass.class);
                    assert userModelClass != null;
                    holder.user_name.setText(userModelClass.getUserName());
                    holder.summary.setText(userModelClass.getSummary());
                    if (userModelClass.getImageURI() !=null  && !userModelClass.getImageURI().equals("default")) {
                        Glide.with(mContext).load(userModelClass.getImageURI()).into(holder.imgPostProfile);
                        imgURI = userModelClass.getImageURI();
                    } else {
                        Glide.with(mContext).load(R.mipmap.user).into(holder.imgPostProfile);

                    }
                    postUserName = userModelClass.getUserName();
                    if (userModelClass.getSummary() != "") {
                        postUserSum = userModelClass.getSummary();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageSave_fill;
        TextView tvTitle;
        TextView like, dislike;
        ImageView imgPost, upvoteImg, downvoteImg, savePost;
        ImageView imgPostProfile,post_options;
        TextView postDesc, user_name, summary;

        MyViewHolder(View itemView) {
            super(itemView);
            like = itemView.findViewById(R.id.upvote_count);
            dislike = itemView.findViewById(R.id.downvote_count);
            imageSave_fill = itemView.findViewById(R.id.save_post_fill);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            upvoteImg = itemView.findViewById(R.id.upvote);
            downvoteImg = itemView.findViewById(R.id.downvote);
            postDesc = itemView.findViewById(R.id.description);
            user_name = itemView.findViewById(R.id.user_name);
            summary = itemView.findViewById(R.id.user_bio);
            savePost = itemView.findViewById(R.id.save_post);
            post_options = itemView.findViewById(R.id.post_options);
            postDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNewActivity();
                }
            });
            imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNewActivity();
                }
            });


        }

        public void startNewActivity() {
            int position = getAdapterPosition();
            AppCompatActivity activity = (AppCompatActivity) mContext;
            Fragment myFragment = new PostDetailsFragment(mData.get(position).getPicture(), mData.get(position).getTitle(),
                    imgURI, mData.get(position).getDescription(), postUserName, postUserSum, mData.get(position).getPostKey(), mData.get(position).getTimeStamp().toString());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();
        }

    }



}
