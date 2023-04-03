package com.example.expierify;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private static final String CATEGORY_NODE = "Category";
    private static final String USER_ID = "userID";

    private RecyclerView recyclerView;
    private DatabaseReference database;
    private MyAdapter myAdapter;
    private ArrayList<CategoryClass> categoryList;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.categoryList);
        database = FirebaseDatabase.getInstance().getReference(CATEGORY_NODE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        categoryList = new ArrayList<>();
        myAdapter = new MyAdapter(requireContext(), categoryList);
        recyclerView.setAdapter(myAdapter);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query categoryQuery = database.orderByChild(USER_ID).equalTo(currentUserID);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryClass category = categorySnapshot.getValue(CategoryClass.class);
                    categoryList.add(category);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get categories", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}