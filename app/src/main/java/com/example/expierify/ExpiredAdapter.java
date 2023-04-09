package com.example.expierify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpiredAdapter extends RecyclerView.Adapter<ExpiredAdapter.ExpiredViewHolder> {

    private List<Food> expiredFoodList;

    public ExpiredAdapter(List<Food> expiredFoodList) {
        this.expiredFoodList = expiredFoodList;
    }

    @NonNull
    @Override
    public ExpiredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expiredfooditem, parent, false);
        return new ExpiredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiredViewHolder holder, int position) {
        Food currentFood = expiredFoodList.get(position);
        if (currentFood.isExpired()) {
            holder.foodNameTextView.setText(currentFood.getName());
            holder.expiryTextView.setText(currentFood.getExpiry());
        } else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return expiredFoodList.size();
    }

    public static class ExpiredViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView expiryTextView;

        public ExpiredViewHolder(View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name_text_view);
            expiryTextView = itemView.findViewById(R.id.expiry_date_text_view);
        }
    }
}
