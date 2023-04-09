package com.example.expierify;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private TextView noDataTextView;
    private ExpiredAdapter adapter;
    private List<Food> foodList = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date currentDate = new Date();

    public ExpiredFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expired, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_expired);
        noDataTextView = view.findViewById(R.id.no_data_textView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpiredAdapter(foodList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Query the database to get all expired food items
        Query query = FirebaseDatabase.getInstance().getReference("Food")
                .orderByChild("expiry").endAt(sdf.format(currentDate));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Add all expired food items to the list
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food != null) {
                            foodList.add(food);
                        }
                    }
                    // Notify the adapter that data has changed
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    noDataTextView.setVisibility(View.GONE);
                } else {
                    // Show a message when no expired food items found
                    recyclerView.setVisibility(View.GONE);
                    noDataTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
