package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.tourguide2.utils.RecyclerViewAdapterRent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class rentActivity extends AppCompatActivity {
    private Context mcontext = rentActivity.this;
    private ArrayList<String> mCarName = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference bookingRef;
    private DatabaseReference vehicleRef;
    private ArrayList<String> carName = new ArrayList<>();
    private ArrayList<String> KMLimit = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<String> extra = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private ArrayList<String> type = new ArrayList<>();
    private ArrayList<String> mileage = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    private String sCarName, sMileage, sExtra, sType, sKMLimit, sPrice, sImageUrl, sId, sTotalAmount, sTotalKMLimit;
    private int daysBetween;
    private ProgressBar progressBarRent;
    private ImageView backButtonIcon;
    int totalAmount;
    private HashMap<String, String> rentHashMap = new HashMap<>();
    private Intent intentCheckDate;
    boolean flag = true;
    LocalDate startDate;
    LocalDate endDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
        progressBarRent = findViewById(R.id.progressBarRent);
        backButtonIcon = findViewById(R.id.backButtonIcon);
        database = FirebaseDatabase.getInstance();
        bookingRef = database.getReference().child("Booking");
        reference = database.getReference("Vehicle");
        intentCheckDate = getIntent();
        rentHashMap = (HashMap<String, String>) intentCheckDate.getSerializableExtra("rentHashMap");
        daysBetween = Integer.parseInt(rentHashMap.get("daysBetween"));

        checkBookingDB();
        backButtonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rentActivity.this, setDateActivity.class));
            }
        });

    }

    //    setup recyclerview-------------------
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRent);
        RecyclerViewAdapterRent adapterRent = new RecyclerViewAdapterRent(mcontext, id, carName, price, KMLimit, type, mileage, extra, imageUrls);
        adapterRent.getDate(rentHashMap.get("pickupDate"),rentHashMap.get("dropOffDate"));
        recyclerView.setAdapter(adapterRent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i("recycler view", "working");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkBookingDB()
    {
        ArrayList<String> vehicleNames = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        LocalDate searchStartDate = LocalDate.parse(rentHashMap.get("pickupDate"),format);
        LocalDate searchEndDate = LocalDate.parse(rentHashMap.get("dropOffDate"),format);



        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    String sStartDate = String.valueOf(dataSnapshot.child("Start Date").getValue());
                    String sEndDate =  String.valueOf(dataSnapshot.child("End Date").getValue());
                    startDate = LocalDate.parse(sStartDate,format);
                    endDate = LocalDate.parse(sEndDate,format);
                    if(!(searchStartDate.compareTo(startDate) < 0 && searchEndDate.compareTo(startDate) < 0) || (searchStartDate.compareTo(endDate) > 0 && searchEndDate.compareTo(endDate) > 0))
                    {
//                        Should not display the car
                        vehicleNames.add(dataSnapshot.child("Car Name").getValue().toString());
                        Log.i("vehicle name", vehicleNames.toString());
                    }
                    else {
//                        can display
                        Log.i("Flag in else", String.valueOf(flag));
                        Log.i("Flag", String.valueOf(flag));
                    }
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            if(vehicleNames.contains(dataSnapshot.child("name").getValue().toString()))
                            {
                                continue;
                            }
                            else
                            {
                                populateArrayList(dataSnapshot);
                            }
                        }
                        setupRecyclerView();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.i("Flag", String.valueOf(flag));

    }



    private void populateArrayList(DataSnapshot dataSnapshot)
    {
        sId = dataSnapshot.getKey();
        sCarName = (String) dataSnapshot.child("name").getValue();
        sPrice = (String) dataSnapshot.child("price").getValue();
        totalAmount = daysBetween * Integer.parseInt(sPrice);
        sTotalAmount = String.valueOf(totalAmount);
        sExtra = (String) dataSnapshot.child("extra").getValue();
        sImageUrl = (String) dataSnapshot.child("imageUrl").getValue();
        sKMLimit = (String) dataSnapshot.child("KMLimit").getValue();
        sTotalKMLimit = String.valueOf(250 * daysBetween) + "KM";
        sType = (String) dataSnapshot.child("type").getValue();
        sMileage = (String) dataSnapshot.child("mileage").getValue();

        id.add(sId);
        carName.add(sCarName);
        price.add(sTotalAmount);
        extra.add(sExtra);
        imageUrls.add(sImageUrl);
        KMLimit.add(sTotalKMLimit);
        type.add(sType);
        mileage.add(sMileage);
    }








}