package com.example.expressblog.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.expressblog.Models.Post;
import com.example.expressblog.R;
import com.example.expressblog.database.DraftDatabase;
import com.example.expressblog.entities.Draft;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Request_Post extends AppCompatActivity {

    ImageView boldText;
    ImageView post_image;
    ImageView reset_image;
    Uri pickedImgUri;
    Post previous_post;
    Boolean falg_draft = true;
    ProgressBar creatpost_progressBar;
    private EditText inputPostTitle, inputContent;
    private TextView textDateTime;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference user_databaseReference;
    private long postCounter;
    String post_key ;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request__post);

        post_key = getIntent().getStringExtra("post_id");

//        Toast.makeText(getApplicationContext(),"Post Id"+" "+post_key,Toast.LENGTH_SHORT).show();
        ImageView imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        creatpost_progressBar = findViewById(R.id.creatpost_progressBar);
        inputPostTitle = findViewById(R.id.inputPostTitle);
        inputContent = findViewById(R.id.inputContent);
        textDateTime = findViewById(R.id.textDateTime);
        post_image = findViewById(R.id.post_image);
        boldText = findViewById(R.id.boldeText);
        boldText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Request_Post.this, "Bold Clicked", Toast.LENGTH_SHORT).show();
                inputContent.getSelectionStart();
                int selectionStart = inputContent.getSelectionStart();
                int selectionEnd = inputContent.getSelectionEnd();


                SpannableStringBuilder ssb = new    SpannableStringBuilder(inputContent.getText());



                CharacterStyle cs = new StyleSpan(Typeface.BOLD);
                ssb.setSpan(cs, selectionStart, selectionEnd, 1);
                inputContent.setText(ssb);
            }


        });



        textDateTime.setText(
                new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getPostDetails();
        storageReference = firebaseStorage.getReference();
        user_databaseReference = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("PostDetails");
        ImageView image_save = findViewById(R.id.image_save);
        ImageView camera_for_post = findViewById(R.id.camera_for_post);
        reset_image = findViewById(R.id.reset_image);
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost(previous_post);
            }
        });

        reset_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_image.setVisibility(View.GONE);
            }
        });


        camera_for_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(500, 500)

                        .setAspectRatio(4, 3)

                        .setAspectRatio(4,3)

                        .start(Request_Post.this);


            }
        });


    }

    public void getPostDetails(){
//        Log.e("Clled.....","Called");
        DatabaseReference postDatabase = firebaseDatabase.getReference("Posts");
        postDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                previous_post = snapshot.getValue(Post.class);

                inputPostTitle.setText(previous_post.getTitle());
                inputContent.setText(previous_post.getDescription());
                if(previous_post.getPicture() != null && !previous_post.getPicture().equals("default"))
                {
                    Glide.with(getApplicationContext()).load(previous_post.getPicture()).into(post_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                pickedImgUri = result.getUri();
                post_image.setImageURI(pickedImgUri);
                post_image.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Request_Post.this, "Error" + error, Toast.LENGTH_LONG).show();

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void savePost() {
        falg_draft = false;
        creatpost_progressBar.setVisibility(View.VISIBLE);


        if (inputPostTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            creatpost_progressBar.setVisibility(View.INVISIBLE);

            return;
        } else if (inputContent.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Blog can't be empty", Toast.LENGTH_SHORT).show();
            creatpost_progressBar.setVisibility(View.INVISIBLE);

            return;

        }
        // code here to add this post to the database
        //
        final String title = inputPostTitle.getText().toString();
        final String desc = inputContent.getText().toString();
        final HashMap<String, String> newMap1 = new HashMap<>();
        final HashMap<String, String> newMap2 = new HashMap<>();



        if (post_image.getVisibility() == View.VISIBLE) {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Post p = new Post(title, desc, uri.toString(), FirebaseAuth.getInstance().getUid(), newMap1, newMap2);
                            addPost(p);
                            onBackPressed();

                        }
                    });

                }
            });

        }
        else {
            Post p = new Post(title, desc, null, FirebaseAuth.getInstance().getUid(), newMap1, newMap2);
            addPost(p);

        }
        // code to add item to draft
//
//        final Draft draft = new Draft();
//        draft.setTitle(title);
//        draft.setDesc(desc);
//        draft.setDateTime((String) textDateTime.getText());
//        draft.setImagePath("this is image path");
//        @SuppressLint("StaticFieldLeak")
//        class saveDraft extends AsyncTask<Void, Void ,Void> {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                DraftDatabase.getDatabase(getApplicationContext()).draftDao().insertDraft(draft);
//                Log.d("saved","is saved");
//                return null;
//            }
//
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                Intent intent = new Intent();
//                setResult(RESULT_OK,intent);
//                finish();
//
//            }
//        }
//        new saveDraft().execute();
        creatpost_progressBar.setVisibility(View.INVISIBLE);

    }
    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Pending_Posts");
        post.setPostKey(myRef.getKey());
        post.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        post.setTimeStamp(Firebast);
        myRef.child(post_key).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                postToPostDetails(post_key);
            }
        });

    }

    public void postToPostDetails(String key) {


        user_databaseReference.child(key).child("PostID").setValue(key);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
//        finish();
        if(falg_draft)
        {
            final String title = inputPostTitle.getText().toString();
            final String desc = inputContent.getText().toString();


            final Draft draft = new Draft();
            draft.setTitle(title);
            draft.setDesc(desc);
            draft.setDateTime((String) textDateTime.getText());
            draft.setImagePath("this is image path");
            @SuppressLint("StaticFieldLeak")
            class saveDraft extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    DraftDatabase.getDatabase(getApplicationContext()).draftDao().insertDraft(draft);
                    Log.d("saved", "is saved");
                    return null;
                }


                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                }
            }
            new saveDraft().execute();
            Toast.makeText(this, "Draft is saved", Toast.LENGTH_SHORT).show();

        }
        finish();

    }
}
