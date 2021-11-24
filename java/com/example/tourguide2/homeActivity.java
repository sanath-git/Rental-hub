package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tourguide2.utils.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static android.widget.Toast.LENGTH_LONG;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context mcontext = homeActivity.this;
    private static final String TAG = "homeActivity";

    private static int ACTIVITY_NUM = 0;
    //    ---------------------navigation Drawer variables----------------------------
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    //    ---------------------recyclerview variables-----------------------------
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private String sTitle;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");
        setupBottomNavigationView();
        toolbar = findViewById(R.id.topToolBar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        Log.i("mAuth", mAuth.toString());


        drawerLayout = findViewById(R.id.navigationDrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationDrawer);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
//        getUserData();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("places/");
        Log.i("storage reference", storageReference.toString());
        populateTheArraylists();


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(homeActivity.this, loginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //    ----------------for navigation drawer----------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationHelper.enableBottomNavigation(mcontext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //    ---------------------------populate arraylists for recyclerview----------------
    private void populateTheArraylists() {
        Task task = storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference file : listResult.getItems()) {
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sTitle = file.getName().split("\\.")[0];
                            title.add(sTitle);
                            Log.i("File name", title.toString());
                            imageUrls.add(uri.toString());
                            Log.i("Url", imageUrls.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            setupRecyclerView();
                        }
                    });

                }
//
            }
        });


    }

    private void setupRecyclerView() {
        Log.i("recycler view", "started");
        RecyclerView recyclerView = findViewById(R.id.recyclerViewHome);
        RecyclerViewAdapterHome adapter = new RecyclerViewAdapterHome(mcontext, title, imageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                mAuth.signOut();
                Intent signInIntent = new Intent(homeActivity.this, loginActivity.class);
                startActivity(signInIntent);
                Log.i("Signing out ", "back to login activity");
                break;
            case R.id.myBookings:
                startActivity(new Intent(homeActivity.this, historyActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(homeActivity.this, profileActivity.class));
                break;
        }
        return true;
    }

    private void getUserData() {
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("User");
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String value = String.valueOf(dataSnapshot.child("mEmail").getValue());
                    Log.i("User email", value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("useremail", "couldnt fetch");

            }
        });
    }


}