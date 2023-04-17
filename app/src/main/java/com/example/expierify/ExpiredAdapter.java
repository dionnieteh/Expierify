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
    ArrayList<Food> foodList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

    public ExpiredAdapter(Context context,ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ExpiredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerviewfooditem, parent, false);
        return new ExpiredAdapter.ExpiredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiredViewHolder holder, int position) {
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
    public void sortExpiryDateAscending() {
        ArrayList<Date> expiryList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Food food : foodList) {
            if (food.getUserID().equals(userID)) { // Check if food userID matches current user's ID
                try {
                    Date expiryDate = sdf.parse(food.getExpiry());
                    if (!expiryList.contains(expiryDate)) { // Check if expiryList already contains the expiry date
                        expiryList.add(expiryDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(expiryList);

        ArrayList<Food> sortedFoodList = new ArrayList<>();
        for (Date expiryDate : expiryList) {
            for (Food food : foodList) {
                if (food.getUserID().equals(userID)) { // Check if food userID matches current user's ID
                    String expiryDateString = food.getExpiry();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date foodExpiryDate = dateFormat.parse(expiryDateString);
                        if (foodExpiryDate.equals(expiryDate)) {
                            sortedFoodList.add(food);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        foodList.clear(); // clear the previous food items
        foodList.addAll(sortedFoodList); // add the sorted food items to the foodList
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ExpiredViewHolder extends RecyclerView.ViewHolder {
        TextView labelName;
        ImageView foodImageView;
        TextView expiryDateTextView;

        public ExpiredViewHolder(@NonNull View itemView) {
            super(itemView);
            labelName= itemView.findViewById(R.id.foodName);
            foodImageView = itemView.findViewById(R.id.foodImage);
            expiryDateTextView=itemView.findViewById(R.id.date_text_view);
        }
    }
}
