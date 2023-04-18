package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

public class SubLabelPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterSubLabel adapter;
    private ArrayList<Food> foodList;
    private DatabaseReference foodRef;
    private ArrayList<String> foodIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_label_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_custom);
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

        String label = getIntent().getStringExtra("labelTitle");
        TextView labelTitle= (TextView) findViewById(R.id.labelName);
        labelTitle.setText(label);

        foodIDs = getIntent().getStringArrayListExtra("foodIDs");

        TextView emptyfoodlist = (TextView) findViewById(R.id.emptyfoodlist);
        recyclerView = findViewById(R.id.subLabelList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new AdapterSubLabel(foodList, this, foodIDs);
        recyclerView.setAdapter(adapter);


        // Find the position of the item with the name "Uncategorized"
        if ("Unlabeled".equals(label)) {
            // Get a reference to the delete button
            deleteBtn.setVisibility(View.GONE);
        }



        // Create a reference to the "Food" node in the database
        foodRef = FirebaseDatabase.getInstance().getReference("Food");

        // Create a query to retrieve all the food items where the "foodId" is in the list of "foodIDs"
        if (foodIDs.isEmpty()) {
            String message = "There are no food items in this location.";
            emptyfoodlist.setText(message);

        } else {
            Query query = foodRef.orderByChild("label").equalTo(label);


            // Add a listener to the query to retrieve the data
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        foodList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the Food object
                            Food food = snapshot.getValue(Food.class);
                                // Add the Food object to the list
                                foodList.add(food);


                        }

                        adapter.sortExpiryDateAscending();
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        if (foodList.isEmpty()) {
                            String message = "There are no food items in this location.";
                            emptyfoodlist.setText(message);

                        }

                    } else {
                        String message = "There are no food items in this location.";
                        emptyfoodlist.setText(message);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to get food products.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this location?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Label").child(userID);
                Query query = categoryRef.orderByChild("lName").equalTo(getIntent().getStringExtra("labelTitle"));
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
                                            foodRef.orderByChild("label").equalTo(getIntent().getStringExtra("labelTitle"))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                                                                String foodKey = foodSnapshot.getKey();
                                                                foodRef.child(foodKey).child("label").setValue("Unlabeled");
                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Log.e("Firebase", "Error getting food within this location: " + getIntent().getStringExtra("categoryTitle"), databaseError.toException());
                                                        }
                                                    });
                                            Toast.makeText(getApplicationContext(), "Location deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firebase", "Error removing location: " + getIntent().getStringExtra("categoryTitle"), e);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss(); // Dismiss the dialog
                finish();
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