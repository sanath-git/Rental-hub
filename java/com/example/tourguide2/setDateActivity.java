package com.example.tourguide2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tourguide2.utils.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import static com.google.android.material.datepicker.DateValidatorPointForward.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class setDateActivity extends AppCompatActivity {
    private Context mContext = setDateActivity.this;
    private static int ACTIVITY_NUM = 2;
    private TextInputEditText locationSelector, pickupDateSelector,dropOffDateSelector;
    private TextInputLayout pickupDateLayout,dropOffDateLayout;
    private Button findCars;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private GetDateFromString getDateFromString;
    private String pickupDateString = "",dropOffDateString = "";
    private LocalDate pickupDate,dropOffDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);
        locationSelector = findViewById(R.id.locationSelector);
        pickupDateSelector = findViewById(R.id.pickupDateSelector);
        dropOffDateSelector = findViewById(R.id.dropOffDateSelector);
        pickupDateLayout = findViewById(R.id.pickupLayout);
        dropOffDateLayout = findViewById(R.id.dropOffLayout);
        findCars = findViewById(R.id.findCars);
        setupBottomNavigationView();
//        -------date picker----
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());



        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setCalendarConstraints(constraintBuilder.build());
        materialDateBuilder.setTitleText("SELECT A DATE");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        final MaterialDatePicker materialDatePickerPickup = materialDateBuilder.build();
        final MaterialDatePicker materialDatePickerDropOff = materialDateBuilder.build();
        long longToday = materialDatePickerDropOff.todayInUtcMilliseconds();
        materialDateBuilder.setSelection(longToday);


        pickupDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked","on pickup date selector");
                materialDatePickerPickup.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


            }
        });
        dropOffDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked","on drop off date selector");
                materialDatePickerDropOff.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });
        materialDatePickerPickup.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                pickupDateSelector.setText(materialDatePickerPickup.getHeaderText());
                pickupDateString = pickupDateSelector.getText().toString();
                Log.i("pickUpDate",pickupDateString);

                try {
                    pickupDate = LocalDate.parse(materialDatePickerPickup.getHeaderText(),format);
                    Log.i("date",pickupDate.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("error","error");
                }
            }
        });
        materialDatePickerDropOff.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                dropOffDateSelector.setText(materialDatePickerDropOff.getHeaderText());
                dropOffDateString = dropOffDateSelector.getText().toString();

                try {
                    dropOffDate = LocalDate.parse(materialDatePickerDropOff.getHeaderText(),format);
                    Log.i("date",dropOffDate.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("error","error");
                }
            }
        });
        findCars.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                if(pickupDateString.isEmpty())
                {

                    Toast.makeText(mContext, "Pick A Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dropOffDateString.isEmpty())
                {
                    Toast.makeText(mContext, "Pick A Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                LocalDate today = LocalDate.now();
                Log.i("today's date",today.toString());
                int daysBetween = (int) ChronoUnit.DAYS.between(pickupDate, dropOffDate);
                Log.i("daysbetween", String.valueOf(daysBetween));
                Log.i("comparing", String.valueOf(pickupDate.compareTo(today)));
                if ((pickupDate.compareTo(today) < 0 ))
                {
                    Log.i("comparing","cannot pick a car in the past");
                    Toast.makeText(mContext, "Selected date is passed already.Try someother date! ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((daysBetween < 1))
                {
                    Toast.makeText(mContext, "Cannot book a car for less than 1 day", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent rentIntent = new Intent(mContext,rentActivity.class);
                HashMap<String,String> rentHashMap = new HashMap<>();
                rentHashMap.put("daysBetween", String.valueOf(daysBetween));
                rentHashMap.put("pickupDate", pickupDateString);
                rentHashMap.put("dropOffDate", dropOffDateString);
                rentIntent.putExtra("rentHashMap",rentHashMap);
                startActivity(rentIntent);


            }
        });


    }
    private void setupBottomNavigationView()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationHelper.enableBottomNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
}