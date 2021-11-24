package com.example.tourguide2.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguide2.R;
import com.example.tourguide2.bookingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapterRent extends RecyclerView.Adapter<com.example.tourguide2.utils.RecyclerViewAdapterRent.ViewHolder>{
    private ArrayList<String> mCarName = new ArrayList<>();
    private ArrayList<String> mPrice = new ArrayList<>();
    private ArrayList<String> mKMLimit = new ArrayList<>();
    private ArrayList<String> mType = new ArrayList<>();
    private ArrayList<String> mMileage = new ArrayList<>();
    private ArrayList<String> mImageUrl = new ArrayList<>();
    private ArrayList<String> mExtra = new ArrayList<>();
    private ArrayList<String> mId = new ArrayList<>();
    private HashMap<String,String> extraMap = new HashMap<>();
    private String sPickupDate,sDropOffDate;

    private Context mContext;
    public RecyclerViewAdapterRent(Context context,ArrayList<String> id, ArrayList<String> carName, ArrayList<String> price, ArrayList<String> KMLimit, ArrayList<String> type, ArrayList<String> mileage, ArrayList<String> extra, ArrayList<String> imageUrl)
    {
        mContext = context;
        mCarName = carName;
        mPrice = price;
        mKMLimit = KMLimit;
        mType = type;
        mMileage = mileage;
        mExtra = extra;
        mImageUrl = imageUrl;
        mId = id;


    }
    public  void getDate(String pickupDate,String dropOffDate)
    {
        extraMap.put("pickupDate",pickupDate);
        extraMap.put("dropOffDate",dropOffDate);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_card_layout_rent,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.carName.setText(mCarName.get(position));
        holder.extra.setText(mExtra.get(position));
        holder.mileage.setText(mMileage.get(position));
        holder.KMLimit.setText(mKMLimit.get(position));
        holder.type.setText(mType.get(position));
        holder.carRentAmount.setText("₹"+mPrice.get(position));
        Picasso.get().load(mImageUrl.get(position)).into(holder.carImage);


        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                extraMap.put("car name",mCarName.get(position));
                extraMap.put("extra",mExtra.get(position));
                extraMap.put("mileage",mMileage.get(position));
                extraMap.put("KM Limit",mKMLimit.get(position));
                extraMap.put("type",mType.get(position));
                extraMap.put("rent amount",mPrice.get(position));
                extraMap.put("id",mId.get(position));
                extraMap.put("image url",mImageUrl.get(position));
                Intent bookingIntent =  new Intent(mContext, bookingActivity.class);
                bookingIntent.putExtra("hashmap",extraMap);

                mContext.startActivity(bookingIntent);
                Log.i("booking intent","working");
            }
        });



    }

    @Override
    public int getItemCount() {
        return mCarName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView carName,mileage,extra,type,KMLimit,carRentAmount;
        RelativeLayout relativeLayout;
        Button bookButton;
        ImageView carImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.carName);
            mileage = itemView.findViewById(R.id.carMileage);
            extra = itemView.findViewById(R.id.extraPrice);
            type = itemView.findViewById(R.id.carType);
            KMLimit = itemView.findViewById(R.id.KMLimit);
            carRentAmount = itemView.findViewById(R.id.carRentAmount);
            relativeLayout = itemView.findViewById(R.id.cardViewRentParentLayout);
            bookButton = itemView.findViewById(R.id.bookButton);
            carImage = itemView.findViewById(R.id.carImage);


        }
    }
}
