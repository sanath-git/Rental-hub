package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import com.example.tourguide2.utils.*;

public class bookingActivity extends AppCompatActivity implements PaymentResultListener {
    private Toolbar toolbar;
    private TextView carName,startDate,dropDate,type,fuel,totalAmount,rentAmount,kmLimit,mileage,extra;
    private ImageView carImage;
    private Button checkoutButton;
    private HashMap<String, String> extraMap = new HashMap<>();

    private int totalAmountPayment;
    private DatabaseReference bookingRef,userRef;
    private long historyId = 1;
    private long totalBookings = 1;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        toolbar = (Toolbar) findViewById(R.id.bookingToolBar);
        userRef = FirebaseDatabase.getInstance().getReference("User");
        bookingRef = FirebaseDatabase.getInstance().getReference("Booking");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bookingActivity.this,rentActivity.class));
                Log.i("clicked on back button","working");
            }
        });

        carName = findViewById(R.id.bookingCarName);

        startDate = findViewById(R.id.pickupDate);
        dropDate = findViewById(R.id.dropOffDate);
        type = findViewById(R.id.bookingCarType);
        rentAmount = findViewById(R.id.rentAmount);
        kmLimit = findViewById(R.id.KMIncluded);
        totalAmount = findViewById(R.id.totalAmount);
        mileage = findViewById(R.id.mileage);
        carImage = findViewById(R.id.bookingCarImage);
        extra = findViewById(R.id.excessCharge);
        Intent intent = getIntent();
        extraMap = (HashMap<String, String>) intent.getSerializableExtra("hashmap");
        Log.i("pickUpDate",extraMap.get("pickupDate"));
        setAllTheTextViews();
        Log.i("carname",extraMap.get("car name"));
        Log.i("extra",extraMap.get("extra"));
        checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });
        historyId = getHistoryId();
        totalBookings = getTotalBookings();



    }

    private void makePayment() {
        String sAmount = String.valueOf(totalAmountPayment);
        int amount = Math.round(Float.parseFloat(sAmount) * 100);
        Log.i("total Amount", String.valueOf(totalAmountPayment));
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_73NrzOXZldSIo8");
        checkout.setImage(R.drawable.enlarged_logo);
        JSONObject paymentObject = new JSONObject();
        try {
            paymentObject.put("name","Al-Misbah Car Hub");
            paymentObject.put("description","Payment for "+extraMap.get("car name"));
            paymentObject.put("currency","INR");
            paymentObject.put("amount",amount);
            checkout.open(bookingActivity.this,paymentObject);


        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void setAllTheTextViews()
    {
        String excessString = "Excess ₹" + extraMap.get("extra");

        carName.setText(extraMap.get("car name"));
        extra.setText(excessString);
        mileage.setText(extraMap.get("mileage"));
        kmLimit.setText(extraMap.get("KM Limit"));
        type.setText(extraMap.get("type"));
        startDate.setText(extraMap.get("pickupDate"));
        dropDate.setText(extraMap.get("dropOffDate"));
        rentAmount.setText("₹"+extraMap.get("rent amount"));
        totalAmountPayment = Integer.parseInt(extraMap.get("rent amount")) + 2000;
        totalAmount.setText("₹"+String.valueOf(totalAmountPayment));
        Picasso.get().load(extraMap.get("image url")).into(carImage);



    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.i("booking","Successful");
        String id;
        id = extraMap.get("id");
        HashMap<String,String> bookedCar = new HashMap<>();
        bookedCar.put("Car Name",extraMap.get("car name"));
        bookedCar.put("Start Date",extraMap.get("pickupDate"));
        bookedCar.put("End Date",extraMap.get("dropOffDate"));
        bookedCar.put("Rent Amount",extraMap.get("rent amount"));
        bookedCar.put("Returned","false");

        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_LONG).show();
        bookingRef = FirebaseDatabase.getInstance().getReference();
        bookingRef.child("Booking").child(String.valueOf(totalBookings)).setValue(bookedCar);
        addRentalHistory(historyId);


        Log.i("booking","Successful");
        startActivity(new Intent(bookingActivity.this,homeActivity.class));


    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_LONG).show();
        Log.i("OnpaymentError","error in payment");
    }
    private long getHistoryId()
    {

        userRef.child(firebaseUser.getUid()).child("Rental History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    historyId = snapshot.getChildrenCount() + 1;
                    Log.i("inside","history id");
                    Log.i("history id", String.valueOf(historyId));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return historyId;
    }


    private void addRentalHistory(long historyId)
    {
        vehicleHistory vehicleHistory = new vehicleHistory(extraMap.get("car name"),extraMap.get("image url"),extraMap.get("pickupDate"),extraMap.get("dropOffDate"),extraMap.get("rent amount"));
        Log.i("vehicle object",vehicleHistory.toString());

        userRef.child(firebaseUser.getUid()).child("Rental History").child(String.valueOf(historyId)).setValue(vehicleHistory);
    }
    private long getTotalBookings()
    {


        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 totalBookings = snapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return totalBookings;
    }
}