package com.example.expierify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<CategoryClass> categoryList;

    public MyAdapter(Context context, ArrayList<CategoryClass> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.categoryitem, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryClass category = categoryList.get(position);
        holder.categoryName.setText(category.getcName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();

                // Query the database for the foodIDs
                Query query = ref.child("Food").orderByChild("userID_category").equalTo(category.getUserID() + "_" + category.getcName());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> foodIDs = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String foodID = child.getKey();
                            foodIDs.add(foodID);
                        }

                        // Create the intent and pass the list of foodIDs as an extra
                        Intent intent = new Intent(context, SubCategoryPage.class);
                        intent.putStringArrayListExtra("foodIDs", (ArrayList<String>) foodIDs);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed get FoodID", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SubCategoryPage.class);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView categoryName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName= itemView.findViewById(R.id.categoryName);
        }


    }
}