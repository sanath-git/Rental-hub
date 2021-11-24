package com.example.tourguide2.utils;


import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.tourguide2.*;
import com.example.tourguide2.R;
import com.example.tourguide2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class bottomNavigationHelper {
    public static void enableBottomNavigation (final Context context, BottomNavigationView view)
    {

            view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.homeNav :
                            Intent intentHome = new Intent(context, homeActivity.class);
                            context.startActivity(intentHome);
                            break;
                        case R.id.rentNav :
                            Intent intentRent = new Intent(context, setDateActivity.class);
                            context.startActivity(intentRent);
                            break;
                        case R.id.hotelNav :
                            Intent intentHotel = new Intent(context, hotelActivity.class);
                            context.startActivity(intentHotel);
                            break;
                        case R.id.searchNav :
                            Intent intentSearch = new Intent(context, searchActivity.class);
                            context.startActivity(intentSearch);
                            break;
                    }
                    return true;
                }

        });
    }
}
