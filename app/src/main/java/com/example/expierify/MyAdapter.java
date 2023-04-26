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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<CategoryClass> categoryList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

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
                DatabaseReference foodRef = database.getReference("Food");
                Query query = foodRef.orderByChild("userID").equalTo(userID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mainCategory = category.getcName(); // get the category name
                        ArrayList<String> foodIDs = new ArrayList<>();
                        ArrayList<String> categoryTitle = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String category = snapshot.child("category").getValue(String.class);
                            if (category != null && category.equals(mainCategory)) {
                                String foodID = snapshot.getKey();
                                String expiryDate = snapshot.child("expiry").getValue(String.class);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                                LocalDate expiry = LocalDate.parse(expiryDate, formatter);
                                LocalDate today = LocalDate.now();
                                if (expiry.isAfter(today) || expiry.isEqual(today)){
                                    foodIDs.add(foodID);
                                    categoryTitle.add(mainCategory);
                                }
                            }
                        }
                        Intent intent = new Intent(context, SubCategoryPage.class);
                        intent.putStringArrayListExtra("foodIDs", (ArrayList<String>) foodIDs);
                        intent.putExtra("categoryTitle", mainCategory);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to get food ID.", Toast.LENGTH_SHORT).show();
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