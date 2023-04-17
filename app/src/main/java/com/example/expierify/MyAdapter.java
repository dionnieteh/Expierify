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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                String categoryName = category.getcName();
                String userID = category.getUserID();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String currentUserID= currentUser.getUid();
                DatabaseReference foodRef = database.getReference("Food");
                Query query = foodRef.orderByChild("userID").equalTo(currentUserID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> foodIDs = new ArrayList<>();
                        ArrayList<String> categoryTitle = new ArrayList<>(); // declare and initialize categoryTitle
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String foodCategory = snapshot.child("category").getValue(String.class);
                            if (foodCategory != null && foodCategory.equals(categoryName)) {
                                String foodID = snapshot.getKey();
                                foodIDs.add(foodID);
                                categoryTitle.add(categoryName);
                            }
                        }
                        Intent intent = new Intent(context, SubCategoryPage.class);
                        intent.putStringArrayListExtra("foodIDs", (ArrayList<String>) foodIDs);
                        intent.putExtra("categoryTitle", categoryName);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed get FoodID", Toast.LENGTH_SHORT).show();
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