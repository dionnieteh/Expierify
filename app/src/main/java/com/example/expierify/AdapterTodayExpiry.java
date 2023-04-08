package com.example.expierify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        Food food = foodList.get(position);
        holder.labelName.setText(food.getName());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Define ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            labelName= itemView.findViewById(R.id.label);
        }
    }
}
