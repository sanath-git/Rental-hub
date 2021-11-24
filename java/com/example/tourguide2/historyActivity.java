package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourguide2.utils.RecyclerViewAdapterHistory;
import com.example.tourguide2.utils.RecyclerViewAdapterRent;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class historyActivity extends AppCompatActivity {
    private static final String TAG = "historyActivity";
    private Context mcontext = historyActivity.this;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ArrayList<String> carName = new ArrayList<>();
    private ArrayList<String> startDate = new ArrayList<>();
    private ArrayList<String> endDate = new ArrayList<>();
    private ArrayList<String> imageUrl = new ArrayList<>();
    private ArrayList<String> rentAmount = new ArrayList<>();
    private TextView noBookingTV;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Log.i(TAG,"started");
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        backButton = findViewById(R.id.backButtonIcon);
        noBookingTV = findViewById(R.id.noBookings);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mcontext,homeActivity.class));
            }
        });
        try {
            getDBData();
        }
        catch (NullPointerException e)
        {
            noBookingTV.setVisibility(View.VISIBLE);
        }
    }
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistory);
        RecyclerViewAdapterHistory adapterHistory = new RecyclerViewAdapterHistory(mcontext,carName,rentAmount,startDate,endDate,imageUrl);
        recyclerView.setAdapter(adapterHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i("recycler view", "working");
    }
    private void getDBData()
    {
        databaseReference.child(firebaseUser.getUid()).child("Rental History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    carName.add(String.valueOf(dataSnapshot.child("carName").getValue()));
                    startDate.add(String.valueOf(dataSnapshot.child("pickUpDate").getValue()));
                    endDate.add(String.valueOf(dataSnapshot.child("dropOffDate").getValue()));
                    imageUrl.add(String.valueOf(dataSnapshot.child("imageUrl").getValue()));
                    rentAmount.add(String.valueOf(dataSnapshot.child("rentAmount").getValue()));
                }
                Log.i("carname",carName.toString());
                Log.i("getting data","working");
                if(carName.isEmpty())
                {
                    noBookingTV.setVisibility(View.VISIBLE);
                }
                else
                {
                    setupRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}