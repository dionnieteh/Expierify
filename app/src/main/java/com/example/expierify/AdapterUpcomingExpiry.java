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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    public void sortExpiryDateAscending() {
        ArrayList<Date> expiryList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        Set<String> addedFoods = new HashSet<>(); // Keep track of food IDs that have already been added
        ArrayList<Food> sortedFoodList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date today = calendar.getTime();


        for (Food food : foodList) {
            if (food.getUserID().equals(currentUser.getUid())) { // Check if food userID matches current user's ID
                try {
                    Date expiryDate = sdf.parse(food.getExpiry());
                    if (expiryDate.after(today)) { // Check if expiry date is after today's date
                        if (!expiryList.contains(expiryDate)) {
                            expiryList.add(expiryDate);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(expiryList);

        for (Date expiryDate : expiryList) {
            for (Food food : foodList) {
                if (food.getUserID().equals(currentUser.getUid())) { // Check if food userID matches current user's ID
                    String expiryDateString = food.getExpiry();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date foodExpiryDate = dateFormat.parse(expiryDateString);
                        if (foodExpiryDate.equals(expiryDate) && !addedFoods.contains(food.getFoodId()) && foodExpiryDate.after(today)) { // Check if expiry date is after today's date
                            sortedFoodList.add(food);
                            addedFoods.add(food.getFoodId()); // Add food ID to the set of added foods
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        foodList = sortedFoodList;
        notifyDataSetChanged();
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
