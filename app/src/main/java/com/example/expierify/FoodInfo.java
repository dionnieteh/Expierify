package com.example.expierify;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FoodInfo extends AppCompatActivity{

        TextView foodTitle;
        ImageView foodImage, enlargedFoodImg;
        TextView descLabel;
        TextView categLabel;
        TextView expDateLabel;
        TextView locateLabel;
        ImageButton edit, back, back2, delete;
        Button save;
        ImageButton calendarBtn;
        Spinner newLocation, newCategory;
        private DatePickerDialog picker;



    EditText titleEdit, descLabelEdit, expDateLabelEdit;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_food_info);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_custom);
            // Get the Intent that started this activity
            Intent intent = getIntent();

            // Retrieve the data passed through the Intent using the getStringExtra() method
            String foodName = intent.getStringExtra("foodName");
            String foodId = intent.getStringExtra("foodId");
            String description = intent.getStringExtra("desc");
            String expiry = intent.getStringExtra("expiry");
            String category = intent.getStringExtra("category");
            String label = intent.getStringExtra("label");
            String imageUrl = intent.getStringExtra("imageUrl");

            // Initialize the TextView and ImageView variables
            foodTitle = findViewById(R.id.title);
            foodImage = findViewById(R.id.foodImg);
            enlargedFoodImg = findViewById(R.id.enlargedFoodImg);
            enlargedFoodImg.setElevation(8f); // set elevation to 8dp
            descLabel = findViewById(R.id.descLabel);
            expDateLabel = findViewById(R.id.expDateLabel);
            categLabel = findViewById(R.id.categLabel);
            locateLabel = findViewById(R.id.locateLabel);
            back = findViewById(R.id.backBtn);
            back2 = findViewById(R.id.backBtn2);
            edit = findViewById(R.id.editBtn);
            save = findViewById(R.id.saveBtn);
            delete = findViewById(R.id.deleteBtn);
            titleEdit = findViewById(R.id.titleEdit);
            InputFilter[] limitTitle = new InputFilter[] {new ExactLengthFilter(18)};
            titleEdit.setFilters(limitTitle);
            descLabelEdit = findViewById(R.id.descLabelEdit);
            InputFilter[] limitDesc = new InputFilter[] {new ExactLengthFilter(48)};
            descLabelEdit.setFilters(limitDesc);
            newLocation= findViewById(R.id.newLocation);
            newCategory= findViewById(R.id.newCategory);
            expDateLabelEdit = findViewById(R.id.expDateLabelEdit);
            calendarBtn=(ImageButton)findViewById(R.id.calendar);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Food").child(foodId);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Extract the values from the DataSnapshot object
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String expDate = dataSnapshot.child("expiry").getValue(String.class);
                    String description = dataSnapshot.child("desc").getValue(String.class);
                    String category = dataSnapshot.child("category").getValue(String.class);
                    String label = dataSnapshot.child("label").getValue(String.class);
                    // Create a new instance of the Food class
                    Food food = new Food(userID, foodId, name, description, expDate, category, label);
                    foodTitle.setText(food.getName());
                    titleEdit.setText(food.getName());
                    descLabel.setText(food.getDesc());
                    descLabelEdit.setText(food.getDesc());
                    expDateLabel.setText(food.getExpiry());
                    expDateLabelEdit.setText(food.getExpiry());
                    categLabel.setText(food.getCategory());
                    locateLabel.setText(food.getLabel());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            // Load the image using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholderimg)
                    .into(foodImage);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholderimg)
                    .into(enlargedFoodImg);

            foodImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enlargedFoodImg.setVisibility(View.VISIBLE);
                    back2.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.INVISIBLE);
                    back2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enlargedFoodImg.setVisibility(View.INVISIBLE);
                            back2.setVisibility(View.INVISIBLE);
                            delete.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // finish the current activity
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteConfirmationDialog(foodId);
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setVisibilityTextView(4);
                    setVisibilityEditText(0);
                    edit.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                    calendarBtn.setVisibility(View.VISIBLE);

                    //set DatePicker for expiry Date
                    calendarBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Calendar calendar = Calendar.getInstance();
                            int day= calendar.get(Calendar.DAY_OF_MONTH);
                            int month= calendar.get(Calendar.MONTH);
                            int year= calendar.get(Calendar.YEAR);

                            //define datepicker
                            picker = new DatePickerDialog(FoodInfo.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    expDateLabelEdit.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                }
                            },year, month, day);

                            picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                            picker.show();
                        }
                    });

                    labelSpinner();
                    categorySpinner();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create a reference to the specific node in the database that you want to update
                            DatabaseReference ref = database.getReference("Food").child(foodId);
                            // Create a Map object to hold the updated values
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("name", titleEdit.getText().toString().trim());
                            updates.put("expiry", expDateLabelEdit.getText().toString().trim());
                            updates.put("desc", descLabelEdit.getText().toString().trim());
                            String category = newCategory.getSelectedItem() != null ? newCategory.getSelectedItem().toString() : "Uncategorized";
                            updates.put("category", category);
                            String newLabel = newLocation.getSelectedItem() != null ? newLocation.getSelectedItem().toString() : "Unlabeled";
                            updates.put("label", newLabel);
                            // Update the node in the database with the new values
                            ref.updateChildren(updates);
                            save.setVisibility(View.INVISIBLE);
                            calendarBtn.setVisibility(View.INVISIBLE);
                            edit.setVisibility(View.VISIBLE);
                            setVisibilityTextView(0);
                            setVisibilityEditText(4);
                        }
                    });
                }
            });
        }
    public void setVisibilityTextView(int visibility){
        foodTitle.setVisibility(visibility);
        descLabel.setVisibility(visibility);
        expDateLabel.setVisibility(visibility);
        categLabel.setVisibility(visibility);
        locateLabel.setVisibility(visibility);
    }
    public void setVisibilityEditText(int visibility){
        newLocation.setVisibility(visibility);
        newCategory.setVisibility(visibility);
        titleEdit.setVisibility(visibility);
        descLabelEdit.setVisibility(visibility);
        expDateLabelEdit.setVisibility(visibility);
    }

    public void labelSpinner(){
        //Label Spinner dropdown
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference labelRef = database.getReference("Label").child(userID);
        Intent intent = getIntent();
        String foodId = intent.getStringExtra("foodId");
        DatabaseReference foodRef = database.getReference("Food").child(foodId).child("label");
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String selectedLabel = dataSnapshot.getValue(String.class);
                labelRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> labels = new ArrayList<>();
                        labels.add(selectedLabel);
                        for (DataSnapshot labelSnapshot : snapshot.getChildren()) {
                            String label = labelSnapshot.getKey();
                            if (!label.equals(selectedLabel)) {
                                labels.add(label);
                            }

                        }
                        // Update the Spinner with the retrieved categories
                        ArrayAdapter<String> labelAdapter = new ArrayAdapter<String>(FoodInfo.this, android.R.layout.simple_spinner_item, labels);
                        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newLocation.setAdapter(labelAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to retrieve labels", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void categorySpinner(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("Category").child(userID);
        Intent intent = getIntent();
        String foodId = intent.getStringExtra("foodId");
        DatabaseReference foodRef = database.getReference("Food").child(foodId).child("category");
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String selectedCategory = dataSnapshot.getValue(String.class);
                categoryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> category = new ArrayList<>();
                        category.add(selectedCategory);
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            String categories = categorySnapshot.getKey();
                            if (!categories.equals(selectedCategory)) {
                                category.add(categories);
                            }

                        }
                        // Update the Spinner with the retrieved categories
                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(FoodInfo.this, android.R.layout.simple_spinner_item, category);
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newCategory.setAdapter(categoryAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to retrieve categories", error.toException());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
    public void showDeleteConfirmationDialog(String foodId) {
        // Define the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this record?");

        // Set the positive button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the deleteFoodRecord() method to delete the record
                deleteFoodRecord(foodId);
                dialog.dismiss(); // Dismiss the dialog
                finish();
            }
        });

        // Set the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Show the AlertDialog
        dialog.show();
    }

    public void deleteFoodRecord(String foodId) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food").child(foodId);
        foodRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    // Record deleted successfully
                    Log.d(TAG, "deleteFoodRecord: Record deleted successfully.");
                } else {
                    // Failed to delete record
                    Log.e(TAG, "deleteFoodRecord: Failed to delete record.", error.toException());
                }
            }
        });
    }

}
