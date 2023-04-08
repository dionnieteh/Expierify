package com.example.expierify;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {




    public HomeFragment() {
        // Required empty public constructor

    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);


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
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd-MM-yyy").format(new Date());
        presentDate.setText(currentDate);
        //PRESENT DATE END

        //enter new activity with notification (bell) button
        ImageButton notificationBtn = (ImageButton) view.findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeFragment.this.getActivity(),  AddCategoryPage.class));
            }


        });
    }
}