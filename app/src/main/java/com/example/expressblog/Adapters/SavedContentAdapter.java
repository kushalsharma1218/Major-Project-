package com.example.expressblog.Adapters;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expressblog.Fragments.UserProfileFragment;
import com.example.expressblog.Models.Post;
import com.example.expressblog.Models.PostModel;
import com.example.expressblog.Models.UserModelClass;
import com.example.expressblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SavedContentAdapter extends RecyclerView.Adapter<SavedContentAdapter.ContentViewHolder> {

    Context mContext;
    List<PostModel> mData;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
    DatabaseReference databaseReferenceall = firebaseDatabase.getReference("Posts");
    private boolean mProcessCkick = false;
    private boolean mProcessCkick2 = false;

    String USER_ID = "";

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new ContentViewHolder(view);
    }

    public SavedContentAdapter(Context mContext, List<PostModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewHolder holder, int position) {
        final String postId = mData.get(position).getPostID();
        databaseReferenceall.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                USER_ID = post.getUserId();
                holder.tvTitle.setText(post.getTitle());
                holder.postDate.setText(timestampToString((Long) post.getTimeStamp()));
                holder.postDesc.setText(post.getDescription());
                if (post.getPicture() != null && !post.getPicture().equals("")) {
                    Glide.with(mContext).load(post.getPicture()).into(holder.imgPost);

                } else {
                    holder.imgPost.setVisibility(View.GONE);
                }
                if (post.getLikes() != null)
                    holder.like.setText(String.valueOf(post.getLikes().size()));
                if (post.getDisLikes() != null)

                    holder.dislike.setText(String.valueOf(post.getDisLikes().size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (USER_ID != "") {
            final String uid = USER_ID;
            FirebaseDatabase.getInstance().getReference("Users").child(uid).child("UserDetails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModelClass userModelClass = snapshot.getValue(UserModelClass.class);
                        assert userModelClass != null;
                        holder.user_name.setText(userModelClass.getUserName());
                        holder.summary.setText(userModelClass.getSummary());
                        Glide.with(mContext).load(userModelClass.getImageURI()).into(holder.imgPostProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        final DatabaseReference finalDatabaseReferencelike = databaseReferenceall.child(mData.get(position).getPostID()).child("Likes");
        holder.upvoteImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(final View v) {
                mProcessCkick = true;
                if (mProcessCkick) {
                    finalDatabaseReferencelike.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue("Liked");
                    finalDatabaseReferencelike.addValueEventListener(new ValueEventListener() {
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
        final DatabaseReference databaseReferencedislike = databaseReferenceall.child(mData.get(position).getPostID()).child("DisLikes");


        holder.downvoteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessCkick2 = true;
                if (mProcessCkick2) {
                    databaseReferencedislike.child(FirebaseAuth.getInstance().getUid()).setValue("Disliked");
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
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("EEEE,dd MMMM yyyy HH:mm a", calendar).toString();
        return date;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {


        TextView tvTitle, postDate;
        TextView like, dislike;
        ImageView imgPost, upvoteImg, downvoteImg, savePost;
        ImageView imgPostProfile;
        TextView postDesc, user_name, summary;

        public ContentViewHolder(View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.upvote_count);
            dislike = itemView.findViewById(R.id.downvote_count);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            upvoteImg = itemView.findViewById(R.id.upvote);
            downvoteImg = itemView.findViewById(R.id.downvote);
            postDesc = itemView.findViewById(R.id.description);
            user_name = itemView.findViewById(R.id.user_name);
            summary = itemView.findViewById(R.id.user_bio);
            savePost = itemView.findViewById(R.id.save_post);
            postDate = itemView.findViewById(R.id.post_date);
            imgPostProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    if (!USER_ID.equals("")) {
                        Fragment myFragment = new UserProfileFragment(USER_ID);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();
                    }
                }
            });

            user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    if (!USER_ID.equals("")) {
                        Fragment myFragment = new UserProfileFragment(USER_ID);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();
                    }
                }
            });

        }


//        public void startNewActivity() {
//            Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
//            int position = getAdapterPosition();
//            postDetailActivity.putExtra("title", mData.get(position).getTitle());
//            postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
//            postDetailActivity.putExtra("description", mData.get(position).getDescription());
//            postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
//
//            // will fix this later i forgot to add user name to post object
//            //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
//            long timestamp = (long) mData.get(position).getTimeStamp();
//            postDetailActivity.putExtra("postDate", timestamp);
//            mContext.startActivity(postDetailActivity);
//        }
//    }

    }
}
