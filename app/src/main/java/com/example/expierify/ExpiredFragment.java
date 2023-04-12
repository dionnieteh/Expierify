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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExpiredFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExpiredAdapter adapter;
    private ArrayList<Food> foodList;
    private DatabaseReference foodRef;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();
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
                getActivity().onBackPressed();; // finish the current fragment
            }
        });

        TextView emptyfoodlist = view.findViewById(R.id.emptyfoodlist);
        recyclerView = view.findViewById(R.id.expireditemlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foodList = new ArrayList<>();
        adapter = new ExpiredAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Create a reference to the "Food" node in the database
        foodRef = FirebaseDatabase.getInstance().getReference("Food");


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
                    try {
                        Date todayDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
                        Date expiryDateObj = dateFormat.parse(expiryDate);
                        // Calculate the date one day before today
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(todayDate);
                        cal.add(Calendar.DATE, -1);
                        Date oneDayBefore = cal.getTime();

                        if (expiryDateObj.before(oneDayBefore)) {
                            foodList.add(food);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get Food Items", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }
}