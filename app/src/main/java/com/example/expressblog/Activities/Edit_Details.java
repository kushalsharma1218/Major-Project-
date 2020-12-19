package com.example.expressblog.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.expressblog.Fragments.ProfileFragment;
import com.example.expressblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Details extends AppCompatActivity {

    public static final String EXTRA_TEXT_NAME = "com.example.expressblog.EXTRA_NAME";
    public static final String EXTRA_TEXT_USERNAME = "com.example.expressblog.EXTRA_USERNAME";
    public static final String EXTRA_TEXT_HEADLINE = "com.example.expressblog.EXTRA_HEADLINE";
    public static final String EXTRA_TEXT_EMAIL = "com.example.expressblog.EXTRA_EMAIL";
    public static final String EXTRA_TEXT_ABOUT = "com.example.expressblog.EXTRA_ABOUT";
    public static final String EXTRA_TEXT_WEBSITE = "com.example.expressblog.EXTRA_WEBSITE";
    public static final String EXTRA_IMAGE_PROFILE = "com.example.expressblog.EXTRA_PROFILE";


    EditText edit_username;
    EditText edit_name;
    EditText edit_summary;
    EditText edit_about;
    EditText edit_email;
    EditText edit_website;
    ProgressBar progressBar;
    ScrollView scrollView;


    ImageView save_edit;
    CircleImageView edit_profile_image;
    private Uri pickedImgUri;
    String imgUri="default";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__details);

        ImageView image_back_edit_details = findViewById(R.id.image_back_edit_details);
        image_back_edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save_edit = findViewById(R.id.image_save_edit_details);
        save_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                scrollView.setAlpha((float) 0.5);
                savedata();
            }
        });

        scrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);

        edit_profile_image = findViewById(R.id.edit_profile_image);
        edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(500, 500)
                        .setAspectRatio(1, 1)
                        .start(Edit_Details.this);

            }
        });


        Intent intent = getIntent();
        edit_username = findViewById(R.id.edit_username);
        edit_name = findViewById(R.id.edit_name);
        edit_summary = findViewById(R.id.edit_summary);
        edit_about = findViewById(R.id.edit_about);
        edit_email = findViewById(R.id.edit_email);
        edit_website = findViewById(R.id.edit_website);

        edit_username.setText(intent.getStringExtra(EXTRA_TEXT_USERNAME));
        edit_name.setText(intent.getStringExtra(EXTRA_TEXT_NAME));
        edit_summary.setText(intent.getStringExtra(EXTRA_TEXT_HEADLINE));
        edit_about.setText(intent.getStringExtra(EXTRA_TEXT_ABOUT));
        edit_email.setText(intent.getStringExtra(EXTRA_TEXT_EMAIL));
        edit_website.setText(intent.getStringExtra(EXTRA_TEXT_WEBSITE));

        if (!intent.getStringExtra(EXTRA_IMAGE_PROFILE).equals("default")) {
            imgUri = intent.getStringExtra(EXTRA_IMAGE_PROFILE);
            Glide.with(getApplicationContext()).load(intent.getStringExtra(EXTRA_IMAGE_PROFILE)).into(edit_profile_image);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                pickedImgUri = result.getUri();
                imgUri = pickedImgUri.toString();
                edit_profile_image.setImageURI(pickedImgUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Edit_Details.this, "Error" + error, Toast.LENGTH_LONG).show();

            }
        }

    }

    private void savedata() {
        String uname = edit_username.getText().toString();
        String name = edit_name.getText().toString();
        String email = edit_email.getText().toString();
        String sum = edit_summary.getText().toString();
        String about = edit_about.getText().toString();


        Intent intent = getIntent();


        if(
                !uname.equals(intent.getStringExtra(EXTRA_TEXT_USERNAME)) ||
                        !name.equals(intent.getStringExtra(EXTRA_TEXT_NAME)) ||
                        !sum.equals(intent.getStringExtra(EXTRA_TEXT_HEADLINE)) ||
                        !about.equals(intent.getStringExtra(EXTRA_TEXT_ABOUT))  ||
                        !email.equals(intent.getStringExtra(EXTRA_TEXT_EMAIL))  ||
                        !imgUri.equals(getIntent().getStringExtra(EXTRA_IMAGE_PROFILE))
        )
        {


            final Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", name);
            hashMap.put("userName", uname);
            hashMap.put("email", email);
            hashMap.put("summary", sum);
            hashMap.put("about", about);
            if (!imgUri.equals(getIntent().getStringExtra(EXTRA_IMAGE_PROFILE))) {
                StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
                final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
                imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                hashMap.put("imageURI", uri.toString());
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("UserDetails").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });

                            }
                        });
                    }
                });
            } else {

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("UserDetails").updateChildren(hashMap);
            }
        }
        progressBar.setVisibility(View.GONE);
        onBackPressed();
    }
}