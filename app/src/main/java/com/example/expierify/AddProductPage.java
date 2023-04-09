package com.example.expierify;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.expierify.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddProductPage extends AppCompatActivity {

    private DatePickerDialog picker;
    Uri imageUri;
    private DatabaseReference category;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page);

        //Back arrow image button redirects user to homeFragment
        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish the current activity
            }
        });

        //cancel button image button redirects user to homeFragment
        Button cancelBtn= (Button)findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish the current activity
            }
        });

        //select image to store food images
        Button selectImgBtn= (Button)findViewById(R.id.selectImgBtn);
        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //set DatePicker for expiry Date
        EditText expiry_date=(EditText)findViewById(R.id.expiry_date);
        expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                int month= calendar.get(Calendar.MONTH);
                int year= calendar.get(Calendar.YEAR);

                //define datepicker
                picker = new DatePickerDialog(AddProductPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        expiry_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);

                picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                picker.show();
            }
        });

        //Name and Description
        EditText foodName= (EditText) findViewById(R.id.newName);
        EditText foodDesc= (EditText) findViewById(R.id.newDesc);


        //Category Spinner dropdown
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("Category").child(userID);
        Spinner newCategory= findViewById(R.id.newCategory);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> categories = new ArrayList<>();
                categories.add("Uncategorized");
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getKey();
                        if (!category.equals("Uncategorized")) {
                            categories.add(category);
                        }
                    }

                // Update the Spinner with the retrieved categories
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(AddProductPage.this, android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve categories", error.toException());
            }
        });

        //Label Spinner dropdown
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference labelRef = database.getReference("Label").child(userID);
        Spinner newLocation= findViewById(R.id.newLocation);
        labelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> labels = new ArrayList<>();
                labels.add("Unlabeled");
                for (DataSnapshot labelSnapshot : snapshot.getChildren()) {
                    String label = labelSnapshot.getKey();
                    if (!label.equals("Unlabeled")) {
                        labels.add(label);
                    }

                }
                // Update the Spinner with the retrieved categories
                ArrayAdapter<String> labelAdapter = new ArrayAdapter<String>(AddProductPage.this, android.R.layout.simple_spinner_item, labels);
                labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newLocation.setAdapter(labelAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to retrieve labels", error.toException());
            }
        });


        //SAVE ALL INFO OF FOOD
        Button saveBtn = findViewById(R.id.saveBtn);
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertFoodData();
            }
        });



    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data !=null && data.getData() != null){
            imageUri=data.getData();
            ImageView foodImage = (ImageView) findViewById(R.id.foodImage);
            foodImage.setImageURI(imageUri);
        }
    }

    private void insertFoodData(){
        EditText foodName = findViewById(R.id.newName);
        EditText foodDesc = findViewById(R.id.newDesc);
        EditText expiry_date = findViewById(R.id.expiry_date);
        Spinner newCategory = findViewById(R.id.newCategory);
        Spinner newLocation = findViewById(R.id.newLocation);

        String name = foodName.getText().toString().trim();
        String desc = foodDesc.getText().toString().trim();
        String expiry = expiry_date.getText().toString().trim();
        String category = newCategory.getSelectedItem() != null ? newCategory.getSelectedItem().toString() : "Uncategorized";
        String label = newLocation.getSelectedItem() != null ? newLocation.getSelectedItem().toString() : "Unlabeled";

        // Validation
        boolean hasError = false;
        if (name.isEmpty()) {
            foodName.setError("Name is required");
            foodName.requestFocus();
            hasError = true;
        }

        if (expiry.isEmpty()) {
            expiry_date.setError("Expiry date is required");
            expiry_date.requestFocus();
            hasError = true;
        }

        if (hasError) {
            return; // Exit early if there are validation errors
        }


        // Create new Food object and save to Firebase database
        if (userID !=null){
            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");
            String foodId = foodRef.push().getKey();
            Food newFood = new Food(userID, foodId, name, desc, expiry, category, label);
            foodRef.child(foodId).setValue(newFood).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uploadFoodImage(foodId);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Upload Food Product", Toast.LENGTH_SHORT).show();
                }
            });
        }
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        //category uncategorized
        if (category != null && category.equals("Uncategorized")) {

            // Check if there is already an existing "Uncategorized" category for the current user
            databaseRef.child("Category").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean categoryExists = false;
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        CategoryClass category = categorySnapshot.getValue(CategoryClass.class);
                        if (category.getcName().equals("Uncategorized")) {
                            categoryExists = true;
                            break;
                        }
                    }

                    if (!categoryExists) {
                        // If an "Uncategorized" category for the current user does not exist, add it to the database
                        CategoryClass newCategory = new CategoryClass( "Uncategorized" );
                        databaseRef.child("Category").child(userID).child("Uncategorized").setValue(newCategory);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }

        //label unlabeled
        if (label != null && label.equals("Unlabeled")) {

            // Check if there is already an existing "Uncategorized" category for the current user
            databaseRef.child("Label").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean labelExists = false;
                    for (DataSnapshot labelSnapshot : dataSnapshot.getChildren()) {
                        LabelClass category = labelSnapshot.getValue(LabelClass.class);
                        if (category.getlName().equals("Unlabeled")) {
                            labelExists = true;
                            break;
                        }
                    }

                    if (!labelExists) {
                        // If an "Uncategorized" category for the current user does not exist, add it to the database
                        LabelClass newLabel = new LabelClass( "Unlabeled");
                        databaseRef.child("Label").child(userID).child("Unlabeled").setValue(newLabel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors here
                }
            });

        }

    }

    private void uploadFoodImage(String foodId) {
        String foodID=foodId;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Food/" + foodId);

        if (imageUri !=null){
            try {
                InputStream stream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageRef.putBytes(data);
                // Listen for upload success or failure
                uploadTask.addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), "Failed to Upload Food Image", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image and save to Firebase Realtime Database
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");
                        foodRef.child(foodId).child("image").setValue(uri.toString());
                        Food foodImg = new Food();
                        foodImg.setImage(uri.toString());
                        Toast.makeText(getApplicationContext(), "Food Successfully Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
                    });
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
            return;
        }
    }
}