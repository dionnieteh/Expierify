package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCategoryPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterSubCategory adapter;
    private ArrayList<Food> foodList;
    private DatabaseReference foodRef;
    private ArrayList<String> foodIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_page);

        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish the current activity
            }
        });


        ImageButton deleteBtn= (ImageButton)findViewById(R.id.deleteCategoryBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        String category = getIntent().getStringExtra("categoryTitle");
        TextView categoryTitle= (TextView) findViewById(R.id.categoryName);
        categoryTitle.setText(category);

        foodIDs = getIntent().getStringArrayListExtra("foodIDs");

        TextView emptyfoodlist = (TextView) findViewById(R.id.emptyfoodlist);
        recyclerView = findViewById(R.id.subCategoryList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new AdapterSubCategory(foodList, this, foodIDs);
        recyclerView.setAdapter(adapter);


        // Find the position of the item with the name "Uncategorized"
        if ("Uncategorized".equals(category)) {
            // Get a reference to the delete button
            deleteBtn.setVisibility(View.GONE);
        }



        // Create a reference to the "Food" node in the database
        foodRef = FirebaseDatabase.getInstance().getReference("Food");

        // Create a query to retrieve all the food items where the "foodId" is in the list of "foodIDs"
        if (foodIDs.isEmpty()) {
            String message = "There are no food items in this category";
            emptyfoodlist.setText(message);

        } else {
            Query query = foodRef.orderByChild("foodId").startAt(foodIDs.get(0)).endAt(foodIDs.get(foodIDs.size() - 1));


            // Add a listener to the query to retrieve the data
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        foodList.clear();
                        ArrayList<String> foodNames = new ArrayList<String>(); // Create a new ArrayList to store unique food names
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the Food object
                            Food food = snapshot.getValue(Food.class);

                            // Check if the food name is already in the foodNames ArrayList
                            if (!foodNames.contains(food.getName())) {
                                // Add the Food object to the list
                                foodList.add(food);

                                // Add the food name to the foodNames ArrayList
                                foodNames.add(food.getName());
                            }
                        }

                        adapter.sortExpiryDateAscending();
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        if (foodList.isEmpty()) {
                            String message = "There are no food items in this category";
                            emptyfoodlist.setText(message);

                        }

                    } else {
                        String message = "There are no food items in this category";
                        emptyfoodlist.setText(message);
                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to get food products", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this category?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(userID);
                Query query = categoryRef.orderByChild("cName").equalTo(getIntent().getStringExtra("categoryTitle"));
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue()

                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Step 2: Update all the foods that belong to this category to "Uncategorized"
                                            foodRef.orderByChild("category").equalTo(getIntent().getStringExtra("categoryTitle"))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                                                                String foodKey = foodSnapshot.getKey();
                                                                foodRef.child(foodKey).child("category").setValue("Uncategorized");
                                                                Toast.makeText(getApplicationContext(), "Category Deleted", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Log.e("Firebase", "Error getting foods with category: " + getIntent().getStringExtra("categoryTitle"), databaseError.toException());
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firebase", "Error removing category: " + getIntent().getStringExtra("categoryTitle"), e);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Failed to get categories", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

}