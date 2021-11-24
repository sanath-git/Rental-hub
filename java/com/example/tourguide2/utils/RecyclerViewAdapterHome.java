package com.example.tourguide2.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tourguide2.R;
import com.example.tourguide2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerViewAdapterHome extends RecyclerView.Adapter<com.example.tourguide2.utils.RecyclerViewAdapterHome.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mTitle = new ArrayList<>();

    private ArrayList<String> mImageurls = new ArrayList<>();
    private Context mcontext;

    public RecyclerViewAdapterHome(Context context, ArrayList<String> title, ArrayList<String> Imageurls){
        mTitle=title;

        mImageurls=Imageurls;
        mcontext=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_card_layout_home,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.title.setText(mTitle.get(position));
        Picasso.get().load(mImageurls.get(position)).into(holder.image);
        holder.progressBarHome.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        ProgressBar progressBarHome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.placeImage);
            title = itemView.findViewById(R.id.placeName);
            progressBarHome = itemView.findViewById(R.id.progressBarHome);


        }
    }
}
