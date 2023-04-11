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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ExpiredAdapter extends RecyclerView.Adapter<ExpiredAdapter.ExpiredViewHolder> {

    private Context context;
    private ArrayList<ExpiredFood> expiredFoodArrayList;
    private ArrayList<String> foodIDList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

    public ExpiredAdapter(Context context,ArrayList<ExpiredFood> expiredFoodArrayList) {
        this.context = context;
        this.expiredFoodArrayList = expiredFoodArrayList;
    }

    @NonNull
    @Override
    public ExpiredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expiredfooditem, parent, false);
        return new ExpiredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiredViewHolder holder, int position) {
        if (expiredFoodArrayList != null && foodIDList != null && !expiredFoodArrayList.isEmpty() && !foodIDList.isEmpty() && foodIDList.size() > position) {
            ExpiredFood expiredFood = expiredFoodArrayList.get(position);
            if (expiredFood.isExpired()) {
                holder.foodNameTextView.setText(expiredFood.getName());
                holder.expiryDateTextView.setText(expiredFood.getExpiry());
                String imageUrl = expiredFood.getImage(); // Get the image URL from the food object
                Glide.with(context)
                        .load(imageUrl) // Load the image using Glide
                        .placeholder(R.drawable.placeholderimg) // Set a placeholder image while the actual image is loading
                        .into(holder.foodImageView); // Set the image in the ImageView
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                ExpiredFood food = expiredFoodArrayList.get(pos);
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

    public void sortExpiryDateAscending() {
        ArrayList<Date> expiryList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (ExpiredFood expiredFood : expiredFoodArrayList) {
            if (expiredFood.getUserID().equals(userID)) { // Check if food userID matches current user's ID
                try {
                    Date expiryDate = sdf.parse(expiredFood.getExpiry());
                    expiryList.add(expiryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(expiryList);

        ArrayList<ExpiredFood> sortedFoodList = new ArrayList<>();
        ArrayList<String> sortedFoodIDList = new ArrayList<>();
        for (Date expiryDate : expiryList) {
            for (ExpiredFood expiredFood : expiredFoodArrayList) {
                if (expiredFood.getUserID().equals(userID)) { // Check if food userID matches current user's ID
                    String expiryDateString = expiredFood.getExpiry();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date foodExpiryDate = dateFormat.parse(expiryDateString);
                        if (foodExpiryDate.equals(expiryDate)) {
                            sortedFoodList.add(expiredFood);
                            sortedFoodIDList.add(expiredFood.getFoodId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        expiredFoodArrayList = sortedFoodList;
        foodIDList = sortedFoodIDList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return expiredFoodArrayList.size();
    }

    public static class ExpiredViewHolder extends RecyclerView.ViewHolder {
        public TextView foodNameTextView;
        public ImageView foodImageView;
        public TextView expiryDateTextView;

        public ExpiredViewHolder(View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodName);
            foodImageView = itemView.findViewById(R.id.foodImage);
            expiryDateTextView=itemView.findViewById(R.id.date_text_view);
        }
    }
}
