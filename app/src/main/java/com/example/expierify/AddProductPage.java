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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page);

        //Back arrow image button redirects user to homeFragment
        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
            }
        });

        //cancel button image button redirects user to homeFragment
        Button cancelBtn= (Button)findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
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
        DatabaseReference categoryRef = database.getReference("Category");
        Spinner newCategory= findViewById(R.id.newCategory);
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String userID = categorySnapshot.child("userID").getValue(String.class);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String currentUserID = currentUser.getUid();
                    if (userID != null && userID.equals(currentUserID)){
                        String category = categorySnapshot.child("cName").getValue(String.class);
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
        DatabaseReference labelRef = database.getReference("Label");
        Spinner newLocation= findViewById(R.id.newLocation);
        labelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> labels = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String userID = categorySnapshot.child("userID").getValue(String.class);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String currentUserID = currentUser.getUid();
                    if (userID != null && userID.equals(currentUserID)){
                        String label = categorySnapshot.child("lName").getValue(String.class);
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

                //insertFoodData();
                String name = foodName.getText().toString().trim();
                String desc = foodDesc.getText().toString().trim();
                String expiry = expiry_date.getText().toString().trim();
                String category = newCategory.getSelectedItem().toString();
                String label = newLocation.getSelectedItem().toString();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                // Validation
                if (name.isEmpty()) {
                    foodName.setError("Name is required");
                    foodName.requestFocus();
                    return;
                }

                if (expiry.isEmpty()) {
                    expiry_date.setError("Expiry date is required");
                    expiry_date.requestFocus();
                    return;
                }

                if (category.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (label.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new Food object and save to Firebase databasw
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

    /*private void insertFoodData(){
        EditText foodName = findViewById(R.id.newName);
        EditText foodDesc = findViewById(R.id.newDesc);
        EditText expiryDate = findViewById(R.id.expiry_date);
        Spinner categorySpinner = findViewById(R.id.newCategory);
        Spinner labelSpinner = findViewById(R.id.newLocation);
        ImageView foodImage = findViewById(R.id.foodImage);

        String name = foodName.getText().toString().trim();
        String desc = foodDesc.getText().toString().trim();
        String expiry = expiryDate.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String label = labelSpinner.getSelectedItem().toString();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();

        Bitmap bitmap = ((BitmapDrawable) foodImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);



        // Validation
        if (name.isEmpty()) {
            foodName.setError("Name is required");
            foodName.requestFocus();
            return;
        }

        if (expiry.isEmpty()) {
            expiryDate.setError("Expiry date is required");
            expiryDate.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (label.isEmpty()) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new Food object and save to Firebase database
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");
        String foodId = foodRef.push().getKey();
        Food newFood = new Food(userID, name, desc, expiry, category, label);
        foodRef.child(foodId).setValue(newFood).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadFoodImage(path);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to Upload Food Product", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear input fields
        foodName.setText("");
        foodDesc.setText("");
        expiryDate.setText("");

        // Display success message
        //Toast.makeText(this, "Food added successfully1", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
    }*/

    private void uploadFoodImage(String foodId) {
        String foodID=foodId;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Food/" + foodId);

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
                    Toast.makeText(getApplicationContext(), "Food Successfully Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
                });
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }




}