package com.example.tourguide2.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PopulateVehicleDB {
    public int id = 1000;
    public DatabaseReference reference;
    public void populate(Vehicle vehicle)
    {
        reference = FirebaseDatabase.getInstance().getReference("Vehicle");
        reference.child(String.valueOf(id)).setValue(vehicle);
        id++;
    }
}
