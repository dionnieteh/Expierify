package com.example.expierify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterUpcomingExpiry extends RecyclerView.Adapter<AdapterUpcomingExpiry.ViewHolder> {

    Context context;
    ArrayList<Food> foodList;

    public AdapterUpcomingExpiry(Context context, ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public AdapterUpcomingExpiry.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerviewfooditem, parent, false);
        return new AdapterUpcomingExpiry.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUpcomingExpiry.ViewHolder holder, int position) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Food food = foodList.get(pos);
                String foodName = food.getName();
                String foodId = food.getFoodId();
                String desc = food.getDesc();
                String expiry = food.getExpiry();
                String category = food.getCategory();
                String label = food.getLabel();
                String imageUrl = food.getImage();

                Intent intent = new Intent(context, FoodInfo.class);
                intent.putExtra("foodName", foodName);
                intent.putExtra("foodId", foodId);
                intent.putExtra("desc", desc);
                intent.putExtra("expiry", expiry);
                intent.putExtra("category", category);
                intent.putExtra("label", label);
                intent.putExtra("imageUrl", imageUrl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (foodList == null) {
            return 0;
        }
        return Math.min(foodList.size(), 4);
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
