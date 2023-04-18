package com.example.expierify;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterTodayExpiry myAdapter;
    private ArrayList<Food> foodList;
    private ArrayList<Food> foodList2;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();


    public HomeFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Recycler view for Today's expiry
        recyclerView = view.findViewById(R.id.todayExpiryList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        foodList = new ArrayList<>();
        myAdapter = new AdapterTodayExpiry(requireContext(), foodList);
        recyclerView.setAdapter(myAdapter);


        Date todayDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        String dateString = dateFormat.format(todayDate);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Food");
        Query foodRef = database.orderByChild("userID").equalTo(userID);

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear(); // clear previous list
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Food food = categorySnapshot.getValue(Food.class);
                    String expiryDate = food.getExpiry();
                    if (Objects.equals(expiryDate, dateString)) {
                        foodList.add(food);
                    }
                }
                myAdapter.notifyDataSetChanged();

                // Hide the TextView if the foodList is empty
                TextView todayCategory = view.findViewById(R.id.todayCategory);
                if (foodList.isEmpty()) {
                    todayCategory.setVisibility(View.GONE);
                } else {
                    todayCategory.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get food items.", Toast.LENGTH_SHORT).show();
            }
        });


        // Recycler view for Upcoming's expiry
        RecyclerView recyclerView2 = view.findViewById(R.id.upcomingExpiryList);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));

        foodList2 = new ArrayList<>();
        AdapterUpcomingExpiry myAdapter2 = new AdapterUpcomingExpiry(requireContext(), foodList2);
        recyclerView2.setAdapter(myAdapter2);

        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference("Food");
        Query foodRef2 = database.orderByChild("userID").equalTo(userID);

        foodRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList2.clear(); // clear previous list
                for (DataSnapshot categorySnapshot2 : snapshot.getChildren()) {
                    Food food2 = categorySnapshot2.getValue(Food.class);
                    String expiryDate2 = food2.getExpiry();
                    try {
                        Date expiryDateObj = dateFormat.parse(expiryDate2);
                        if ((!Objects.equals(expiryDate2, dateString)) && expiryDateObj.after(todayDate)) {
                            foodList2.add(food2);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                myAdapter2.sortExpiryDateAscending();
                myAdapter2.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get food items.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Number of Food
        TextView foodNo = (view.findViewById((R.id.foodNo)));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentuserId = currentUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference("Food").orderByChild("userID").equalTo(currentuserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int foodCount = (int) snapshot.getChildrenCount();
                foodNo.setText(Integer.toString(foodCount)); // foodCount needs to be converted to a String
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                foodNo.setText("ERROR");
            }
        });

        //Number of Category
        TextView categoryNo = (view.findViewById((R.id.categoryNo)));
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Category").child(currentUserID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int categoryCount = (int) snapshot.getChildrenCount();
                categoryNo.setText(Integer.toString(categoryCount)); // foodCount needs to be converted to a String
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                categoryNo.setText("ERROR");
            }
        });

        //Number of Location
        TextView locationNo = (view.findViewById((R.id.locationNo)));
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Label").child(currentUserID);
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int locationCount = (int) snapshot.getChildrenCount();
                locationNo.setText(Integer.toString(locationCount)); // foodCount needs to be converted to a String
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                locationNo.setText("ERROR");
            }
        });


        //PRESENT DATE
        TextView presentDate = (view.findViewById(R.id.todayDate));
        // create a date format
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("d/M/yyy").format(new Date());
        presentDate.setText(currentDate);
        //PRESENT DATE END

        //enter new activity with notification (bell) button


        //Tips button
        ImageButton tipsBtn = (ImageButton) view.findViewById(R.id.tipsBtn);
        tipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeFragment.this.getActivity(),  TipsPage.class));
            }


        });



    }



}