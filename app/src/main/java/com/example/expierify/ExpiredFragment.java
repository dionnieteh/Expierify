package com.example.expierify;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpiredFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExpiredAdapter adapter;
    private ArrayList<ExpiredFood> expiredFoodArrayList;
    private DatabaseReference foodRef;
    private ArrayList<String> foodIDs;
    public ExpiredFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expired, container, false);
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // finish the current fragment
            }
        });


        foodIDs = getArguments().getStringArrayList("foodIDs");

        TextView emptyfoodlist = view.findViewById(R.id.emptyfoodlist);
        recyclerView = view.findViewById(R.id.expireditemlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expiredFoodArrayList = new ArrayList<>();
        adapter = new ExpiredAdapter(getContext(), expiredFoodArrayList);
        recyclerView.setAdapter(adapter);

        // Create a reference to the "Food" node in the database
        foodRef = FirebaseDatabase.getInstance().getReference("Food");

        // Create a query to retrieve all the food items where the "foodId" is in the list of "foodIDs"
        if (foodIDs.isEmpty()) {
            String message = "There are no expired food items";
            emptyfoodlist.setText(message);

        } else {
            Query query = foodRef.orderByChild("foodId").startAt(foodIDs.get(0)).endAt(foodIDs.get(foodIDs.size() - 1));

            // Add a listener to the query to retrieve the data
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        expiredFoodArrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the Food object
                            ExpiredFood food = snapshot.getValue(ExpiredFood.class);

                            // Add the Food object to the list
                            if(food.isExpired()) {
                                expiredFoodArrayList.add(food);
                            }
                        }

                        adapter.sortExpiryDateAscending();
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        if (expiredFoodArrayList.isEmpty()) {
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
                    }
                });
            }
        return view;

    }
}