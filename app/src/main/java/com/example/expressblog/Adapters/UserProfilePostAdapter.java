package com.example.expressblog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expressblog.Models.Post;
import com.example.expressblog.Models.PostModel;
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

public class UserProfilePostAdapter extends RecyclerView.Adapter<UserProfilePostAdapter.MyViewHolder> {

    private Context mContext;
    private List<PostModel> mData;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReferenceall = firebaseDatabase.getReference("Posts");
    private boolean mProcessCkick = false;
    private boolean mProcessCkick2 = false;

    public UserProfilePostAdapter(Context mContext, List<PostModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_uers_post_item, parent, false);
        return new UserProfilePostAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String postId = mData.get(position).getPostID();
        databaseReferenceall.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                holder.tvTitle.setText(post.getTitle());
                holder.postDate.setText(timestampToString((Long) post.getTimeStamp()));
                holder.postDesc.setText(post.getDescription());
                if(post.getPicture()!=null && !post.getPicture().equals("")) {
                    Glide.with(mContext).load(post.getPicture()).into(holder.imgPost);
                }
                else
                    {
                        holder.imgPost.setVisibility(View.GONE);
                    }
                if(post.getLikes()!= null)
                holder.like.setText(String.valueOf(post.getLikes().size()));
                if(post.getDisLikes()!= null)
                holder.dislike.setText(String.valueOf(post.getDisLikes().size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final DatabaseReference finalDatabaseReferencelike = databaseReferenceall.child(mData.get(position).getPostID()).child("Likes");
        holder.upvoteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mProcessCkick = true;
                if (mProcessCkick) {
                    finalDatabaseReferencelike.child(FirebaseAuth.getInstance().getUid()).setValue("Liked");
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

    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, postDate;
        TextView like, dislike;
        ImageView imgPost, upvoteImg, downvoteImg;

        TextView postDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            like = itemView.findViewById(R.id.upvote_count_p);
            dislike = itemView.findViewById(R.id.downvote_count_p);
            tvTitle = itemView.findViewById(R.id.row_post_title_p);
            imgPost = itemView.findViewById(R.id.row_post_img_p);
            upvoteImg = itemView.findViewById(R.id.upvote_p);
            downvoteImg = itemView.findViewById(R.id.downvote_p);
            postDesc = itemView.findViewById(R.id.description_p);
            postDate = itemView.findViewById(R.id.post_date_p);
        }
    }
    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("EEEE,dd MMMM yyyy HH:mm a",calendar).toString();
        return date;


    }
}
