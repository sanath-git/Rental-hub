package com.example.tourguide2.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguide2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<com.example.tourguide2.utils.RecyclerViewAdapterHistory.ViewHolder>{
    private Context context;
    private ArrayList<String> carName = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private ArrayList<String> startDate = new ArrayList<>();
    private ArrayList<String> endDate = new ArrayList<>();
    private ArrayList<String> imageUrl = new ArrayList<>();

    public RecyclerViewAdapterHistory(Context context, ArrayList<String> carName, ArrayList<String> price, ArrayList<String> startDate, ArrayList<String> endDate, ArrayList<String> imageUrl) {
        this.context = context;
        this.carName = carName;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_history,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("onbindholder","called");
        holder.carNameTV.setText(carName.get(position));
        holder.rentAmountTV.setText(price.get(position));
        holder.startDateTV.setText(startDate.get(position));
        holder.endDateTV.setText(endDate.get(position));
        Picasso.get().load(imageUrl.get(position)).into(holder.carImageIV);
    }

    @Override
    public int getItemCount() {
        return carName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView carNameTV,rentAmountTV,startDateTV,endDateTV;
        ImageView carImageIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carNameTV = itemView.findViewById(R.id.carNameHistory);
            rentAmountTV = itemView.findViewById(R.id.rentAmountHistory);
            startDateTV = itemView.findViewById(R.id.startDate);
            endDateTV = itemView.findViewById(R.id.endDate);
            carImageIV = itemView.findViewById(R.id.carImageHistory);
        }
    }
}
