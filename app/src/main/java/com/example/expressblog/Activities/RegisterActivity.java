package com.example.expressblog.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.expressblog.Add_Extra_Details;
import com.example.expressblog.Models.AccountSetupModelClass;
import com.example.expressblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class RegisterActivity extends AppCompatActivity {


    ImageView ImgUserPhoto;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    Uri pickedImgUri ;

    private EditText userEmail,confirmPass,userName,u_name,uIntro,password;
    private ProgressBar loadingProgress;
    private Button regBtn;
    TextView loggedIn,img;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    String uName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userEmail = findViewById(R.id.regMail);
        userName = findViewById(R.id.regName);
        u_name = findViewById(R.id.userName);
        uIntro = findViewById(R.id.editTextTextMultiLine);
        confirmPass = findViewById(R.id.regPassword2);
        password = findViewById(R.id.regPassword);
        img = findViewById(R.id.textView);

        loadingProgress = findViewById(R.id.regProgressBar);

        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);
        loggedIn = findViewById(R.id.loggedIn);
        loggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password1 = password.getText().toString();
                final String password2 = confirmPass.getText().toString();
                final String name = userName.getText().toString();
                 uName = u_name.getText().toString();
                final String intro = uIntro.getText().toString();
                if( email.isEmpty() || name.isEmpty() || password1.isEmpty()  || !password1.equals(password2) || uName.isEmpty() ) {


                    showMessage("Please check the details") ;
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);


                }
                else {
                    FirebaseDatabase.getInstance().getReference("Usernames").child(uName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                u_name.setError("Please choose another username");
                            }
                            else
                                {
                                    CreateUserAccount(name,email,uName,intro,password1);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto) ;

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();

                }
                else
                {
                    openGallery();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512, 512)
                            .setAspectRatio(1, 1)
                            .start(RegisterActivity.this);

                }

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(RegisterActivity.this);

            }
        });



    }

    private void CreateUserAccount(final String name, final String email, final String uname, final String summary, final String pass) {


        // this method create user account with specific email and password

        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // user account created successfully
                            showMessage("Account created");
                            // after we created user account we need to update his profile picture and name
                            startActivity(new Intent(getApplicationContext(),Home.class));
                            updateUserInfo( name ,email,uname,summary,pickedImgUri,mAuth.getCurrentUser());


                        }
                        else
                        {
                            // account creation failed
                            showMessage("account creation failed...please check details"+task.getException());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String name, final String email, final String uname, final String sum , Uri pickedImgUri, final FirebaseUser currentUser) {
        if(pickedImgUri!=null) {

            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

//                             uri contain user image url


                            AccountSetupModelClass accountSetupModelClass = new AccountSetupModelClass(name, email, uname, sum, uri.toString(), currentUser.getUid(),"");
                            databaseReference.child(FirebaseAuth.getInstance().getUid()).child("UserDetails").setValue(accountSetupModelClass);
                            setUserName(FirebaseAuth.getInstance().getUid());

                        }
                    });

                }
            });
        }
        else
            {
                AccountSetupModelClass accountSetupModelClass = new AccountSetupModelClass(name, email, uname, sum, "default", currentUser.getUid(),"");
                databaseReference.child(FirebaseAuth.getInstance().getUid()).child("UserDetails").setValue(accountSetupModelClass);
                setUserName(FirebaseAuth.getInstance().getUid());

            }

    }
    public void setUserName(String uid)
    {
        FirebaseDatabase.getInstance().getReference("Usernames").child(uName).setValue(uid).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Done","user");
            }
        });
    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(),Home.class);
        startActivity(homeActivity);
        finish();
    }

    // simple method to show toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                pickedImgUri = result.getUri();
                ImgUserPhoto.setImageURI(pickedImgUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(RegisterActivity.this, "Error" + error, Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Home.class));
    }
}
