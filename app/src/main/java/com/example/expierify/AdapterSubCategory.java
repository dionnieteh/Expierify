package com.example.expierify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AdapterSubCategory extends RecyclerView.Adapter<AdapterSubCategory.ViewHolder> {
    private ArrayList<Food> foodList;
    private ArrayList<String> foodIDList;
    private Context context;
    private ArrayList<Date> expiryList;

    public AdapterSubCategory(ArrayList<Food> foodList, Context context, ArrayList<String> foodIDList) {
        this.foodList = foodList;
        this.context = context;
        this.foodIDList = foodIDList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategoryitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (foodList != null && foodIDList != null && !foodList.isEmpty() && !foodIDList.isEmpty()) {
            if (foodIDList.size() > 0) {
                String foodID = foodIDList.get(position);
                Food food1 = foodList.get(position);
                holder.foodNameTextView.setText(food1.getName());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference foodsRef = database.getReference("Food");
                DatabaseReference foodRef = foodsRef.child(foodID);



                foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get the food object corresponding to the foodID
                        Food food = dataSnapshot.getValue(Food.class);

                        // Check if the food object is not null and its ID matches the requested ID
                        if (food != null && food.getFoodId().equals(foodID)) {
                            // Get the food name from the food object
                            String foodName = food.getName();
                            // Set the food name in the holder
                            holder.foodNameTextView.setText(foodName);

                            String imageUrl = food.getImage(); // Get the image URL from the food object
                            Glide.with(context)
                                    .load(imageUrl) // Load the image using Glide
                                    .placeholder(R.drawable.placeholderimg) // Set a placeholder image while the actual image is loading
                                    .into(holder.foodImageView); // Set the image in the ImageView

                            String expiryDate = food.getExpiry();
                            holder.expiryDateTextView.setText(expiryDate);



                        }else{
                            Toast.makeText(context.getApplicationContext(), "There are no items in this category", Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context.getApplicationContext(), "Failed to get food products", Toast.LENGTH_SHORT).show();
                    }
                });


                Food food = null;

                for (int i = 0; i < foodList.size(); i++) {
                    if (foodList.get(i).getFoodId().equals(foodID)) {
                        food = foodList.get(i);
                        break;
                    }
                }

                if (food != null) {
                    holder.foodNameTextView.setText(food.getName());
                    holder.expiryDateTextView.setText(food.getExpiry());



                    String imageUrl = food.getImage(); // Get the image URL from the food object
                    Glide.with(context)
                            .load(imageUrl) // Load the image using Glide
                            .placeholder(R.drawable.placeholderimg) // Set a placeholder image while the actual image is loading
                            .into(holder.foodImageView); // Set the image in the ImageView
                }

            }
        }

    }

    public void sortExpiryDateAscending() {
        ArrayList<Date> expiryList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        for (Food food : foodList) {
            if (food.getUserID().equals(currentUser.getUid())) { // Check if food userID matches current user's ID
                try {
                    Date expiryDate = sdf.parse(food.getExpiry());
                    expiryList.add(expiryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(expiryList);

        ArrayList<Food> sortedFoodList = new ArrayList<>();
        ArrayList<String> sortedFoodIDList = new ArrayList<>();
        for (Date expiryDate : expiryList) {
            for (Food food : foodList) {
                if (food.getUserID().equals(currentUser.getUid())) { // Check if food userID matches current user's ID
                    String expiryDateString = food.getExpiry();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date foodExpiryDate = dateFormat.parse(expiryDateString);
                        if (foodExpiryDate.equals(expiryDate)) {
                            sortedFoodList.add(food);
                            sortedFoodIDList.add(food.getFoodId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        foodList = sortedFoodList;
        foodIDList = sortedFoodIDList;
        notifyDataSetChanged();
    }


    public void setFoodIDList(ArrayList<String> foodIDList) {
        this.foodIDList = foodIDList;
    }

    @Override
    public int getItemCount() {
        return foodIDList.size();

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodNameTextView;
        public ImageView foodImageView;
        public TextView expiryDateTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            foodNameTextView = itemView.findViewById(R.id.foodName);
            foodImageView = itemView.findViewById(R.id.foodImage);
            expiryDateTextView=itemView.findViewById(R.id.date_text_view);
        }
    }
}

