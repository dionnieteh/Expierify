package com.example.expierify;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ESubCategoryPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EAdapterSubCategory adapter;
    private ArrayList<Food> foodList;
    private DatabaseReference foodRef;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esub_category_page);

        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish the current activity
            }
        });

        TextView emptyfoodlist = (TextView) findViewById(R.id.emptyfoodlist);
        recyclerView = findViewById(R.id.subCategoryList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new EAdapterSubCategory(foodList, this);
        recyclerView.setAdapter(adapter);


        // Create a reference to the "Food" node in the database
        foodRef = FirebaseDatabase.getInstance().getReference("Food").child("foodID");

        Query query1 = foodRef.orderByChild("userID").equalTo(userID);

        // Add a listener to the query to retrieve the data
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Query query2 = foodRef.orderByChild("expiry").endAt(String.valueOf(getTodayDate()));
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
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
                                    String message = "There are no expired food items ";
                                    emptyfoodlist.setText(message);

                                }
                            } else {
                                String message = "There are no food items";
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


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to get food products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Date getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Change the date format to match the format in your Firebase database
        String todayString = dateFormat.format(new Date()); // Get today's date as a string in the correct format
        Date todayDate = null; // Convert the string to a Date object
        try {
            todayDate = dateFormat.parse(todayString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return todayDate;
    }


}