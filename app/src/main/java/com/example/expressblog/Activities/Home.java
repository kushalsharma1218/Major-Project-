package com.example.expressblog.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.expressblog.Fragments.HomeFragment;
import com.example.expressblog.Fragments.ProfileFragment;
import com.example.expressblog.Fragments.SaveContentFragment;
import com.example.expressblog.Fragments.YourContentFragment;
import com.example.expressblog.Models.AccountSetupModelClass;
import com.example.expressblog.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{



    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private Uri pickedImgUri = null;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadSettting();

        // ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();
        databaseReference = firebaseDatabase.getReference("Users").child(uid).child("UserDetails");
        // ini popup


        findViewById(R.id.nav_signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                FirebaseAuth.getInstance().signOut();
                finish();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreatPost.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        updateNavHeader();


        // set the home fragment as the default one

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.setting:
                //Toast.makeText(getApplicationContext(),"frtgh".Toast.LE);
                Intent i = new Intent(this, settings.class);
                startActivity(i);
                return true;
        }

        return false;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), Drafts.class));
        } else if (id == R.id.nav_signOut) {
            FirebaseAuth.getInstance().signOut();
            finish();
        } else if (id == R.id.nav_loki) {

            startActivity(new Intent(getApplicationContext(), CreatPost.class));
        } else if(id == R.id.nav_saved) {
            getSupportActionBar().setTitle("Saved Posts");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SaveContentFragment(uid)).commit();
        } else if(id == R.id.nav_content) {
            getSupportActionBar().setTitle("Your Posts");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new YourContentFragment(uid)).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView navUsername = headerView.findViewById(R.id.nav_username);
        final TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        final ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);



        // now we will use Glide to load user image
        // first we need to import the library

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AccountSetupModelClass accountSetupModelClass = snapshot.getValue(AccountSetupModelClass.class);
                    navUserMail.setText(accountSetupModelClass.getEmail());
                    navUsername.setText(accountSetupModelClass.getUserName());
                    if(!accountSetupModelClass.getImageURI().equals("default"))
                    Glide.with(getApplicationContext()).load(accountSetupModelClass.getImageURI()).into(navUserPhot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void showPostOptions(View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        popupMenu.setOnMenuItemClickListener(this);
//        popupMenu.inflate(R.menu.post_options);
//        popupMenu.show();
//    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item1_p:
//                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.item2_p:
//                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.item3_p:
//                Toast.makeText(this, "Item 3 clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.improve:
//
//                return true;
//            default:
//                return false;
//        }
//    }

    private void loadSettting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean chk_night = sp.getBoolean("NIGHT", false);

        String orien = sp.getString("ORIENTATION", "false");
        if ("1".equals(orien)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);

        } else if ("2".equals(orien)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        } else if ("3".equals(orien)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        }
    }


}
