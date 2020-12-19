package com.example.expressblog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.expressblog.Activities.Home;
import com.example.expressblog.Models.AccountSetupModelClass;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Extra_Details extends AppCompatActivity {

    ProgressBar progressBar,progressBar1;
    CircleImageView userImg;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private EditText userName, summary;
    Button continue_btn;
    private String name;
    private String email;
    private String imgUri;
    boolean clicked = false;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    String userImg2;
    LinearLayout linearLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__extra__details);

        name = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        imgUri = getIntent().getStringExtra("PhotoUri");


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("UserDetails");
        progressBar = findViewById(R.id.regProgressBar);
        progressBar1 = findViewById(R.id.progress_bar);

        linearLayout = findViewById(R.id.linear);

        userName = findViewById(R.id.userName);
        summary = findViewById(R.id.editTextTextMultiLine);

        continue_btn = findViewById(R.id.regBtn1);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_name = userName.getText().toString();
                if (u_name.isEmpty()) {
                    userName.setError("Field cannot be empty");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    continue_btn.setVisibility(View.GONE);
                    String sum = summary.getText().toString();
                    if (sum.isEmpty()) sum = "";
                   svaeToFirebase(name,email,u_name,sum,imgUri);
                    startActivity(new Intent(getApplicationContext(), Home.class));
                }
            }
        });
        userImg = findViewById(R.id.regUserPhoto);
        Glide.with(getApplicationContext()).load(imgUri).into(userImg);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();

                } else {
                    openGallery();
                }

            }
        });


    }

    public void svaeToFirebase(final String name, final String email, final String userName, final String sum, String userImg) {

        if (clicked) {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            AccountSetupModelClass accountSetupModelClass = new AccountSetupModelClass(name, email, userName, sum, uri.toString(), FirebaseAuth.getInstance().getUid(),"");
                            databaseReference.setValue(accountSetupModelClass);
                        }
                    });
                }
            });
        }
        else
            {
                AccountSetupModelClass accountSetupModelClass = new AccountSetupModelClass(name, email, userName, sum, userImg, FirebaseAuth.getInstance().getUid(),"");
                databaseReference.setValue(accountSetupModelClass);
            }
        progressBar.setVisibility(View.INVISIBLE);

    }


    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(Add_Extra_Details.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Add_Extra_Details.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Add_Extra_Details.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }
            else
            {
                ActivityCompat.requestPermissions(Add_Extra_Details.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            openGallery();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            pickedImgUri = data.getData() ;
            userImg.setImageURI(pickedImgUri);
        }
    }

    @Override
    protected void onStart() {

        linearLayout.setAlpha((float) 0.5);
        progressBar1.setVisibility(View.VISIBLE);

        databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("UserDetails");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        }
                        else
                            {
                               linearLayout.setVisibility(View.INVISIBLE);
                                progressBar1.setVisibility(View.INVISIBLE);

                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        super.onStart();
    }
}