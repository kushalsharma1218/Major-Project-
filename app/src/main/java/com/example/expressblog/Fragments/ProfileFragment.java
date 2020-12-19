package com.example.expressblog.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.expressblog.Activities.Edit_Details;
import com.example.expressblog.Models.AccountSetupModelClass;
import com.example.expressblog.Models.UserModelClass;
import com.example.expressblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private static final String EXTRA_TEXT_NAME = "com.example.expressblog.EXTRA_NAME";
    private static final String EXTRA_TEXT_USERNAME = "com.example.expressblog.EXTRA_USERNAME";
    private static final String EXTRA_TEXT_HEADLINE = "com.example.expressblog.EXTRA_HEADLINE";
    private static final String EXTRA_TEXT_EMAIL = "com.example.expressblog.EXTRA_EMAIL";
    private static final String EXTRA_TEXT_ABOUT = "com.example.expressblog.EXTRA_ABOUT";
    private static final String EXTRA_TEXT_WEBSITE = "com.example.expressblog.EXTRA_WEBSITE";
    private static final String EXTRA_IMAGE_PROFILE = "com.example.expressblog.EXTRA_PROFILE";

    private String imgUri = "";
    private Context context;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static TextView name,summary,aboutText,userName;
    View view;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }
    private ImageView edit_details;
    private ImageView profileImage;
    private TextView email_text, website_text;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference =firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("UserDetails");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        summary = view.findViewById(R.id.summary);
        edit_details = view.findViewById(R.id.edit_details);
        userName = view.findViewById(R.id.usernameP);
        name = view.findViewById(R.id.title);
        aboutText = view.findViewById(R.id.about);
        email_text = view.findViewById(R.id.email_p);
        website_text = view.findViewById(R.id.website_p);
        profileImage = view.findViewById(R.id.imageview_profile_u);
        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              editDetailsMethod();
               }
        });
    return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {

                    UserModelClass accountSetupModelClass = snapshot.getValue(UserModelClass.class);
                    assert accountSetupModelClass != null;
                    if(!accountSetupModelClass.getImageURI().equals("default")) {
                        Glide.with(context).load(accountSetupModelClass.getImageURI()).into(profileImage);
                        imgUri = accountSetupModelClass.getImageURI();
                    }
                    userName.setText(accountSetupModelClass.getUserName());
                    name.setText(accountSetupModelClass.getName());
                    email_text.setText(accountSetupModelClass.getEmail());
                    aboutText.setText(accountSetupModelClass.getAbout());
                    if(!accountSetupModelClass.getSummary().equals("")) {
                        summary.setText(accountSetupModelClass.getSummary());
                    }
                    else
                        {
                            summary.setText("Please add a headline");

                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void editDetailsMethod() {

            Intent intent1 = new Intent(getActivity() , Edit_Details.class);
            intent1.putExtra(EXTRA_TEXT_NAME,name.getText() );
            intent1.putExtra(EXTRA_TEXT_USERNAME,userName.getText() );
            intent1.putExtra(EXTRA_TEXT_HEADLINE,summary.getText() );
            intent1.putExtra(EXTRA_TEXT_EMAIL,email_text.getText() );
            intent1.putExtra(EXTRA_TEXT_ABOUT,aboutText.getText() );
            intent1.putExtra(EXTRA_TEXT_WEBSITE,website_text.getText() );
            if(imgUri.equals(""))
            intent1.putExtra(EXTRA_IMAGE_PROFILE,"default");
            else
            {
                intent1.putExtra(EXTRA_IMAGE_PROFILE,imgUri);
                }
            startActivity(intent1);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
