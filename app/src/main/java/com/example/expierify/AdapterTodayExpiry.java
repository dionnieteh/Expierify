package com.example.expierify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterTodayExpiry extends RecyclerView.Adapter<AdapterTodayExpiry.ViewHolder> {

    Context context;
    ArrayList<Food> foodList;

    public AdapterTodayExpiry(Context context, ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.todayexpiryitem, parent, false);
        return new AdapterTodayExpiry.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (foodList != null && !foodList.isEmpty()) {
            Food food = foodList.get(position);
            holder.labelName.setText(food.getName());
            holder.expiryDateTextView.setText(food.getExpiry());
            String imageUrl = food.getImage(); // Get the image URL from the food object
            Glide.with(context)
                    .load(imageUrl) // Load the image using Glide
                    .placeholder(R.drawable.placeholderimg) // Set a placeholder image while the actual image is loading
                    .into(holder.foodImageView); // Set the image in the ImageView
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Define ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelName;
        ImageView foodImageView;
        TextView expiryDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            labelName= itemView.findViewById(R.id.foodName);
            foodImageView = itemView.findViewById(R.id.foodImage);
            expiryDateTextView=itemView.findViewById(R.id.date_text_view);
        }
    }
}
