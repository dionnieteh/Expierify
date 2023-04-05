package com.example.expierify;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LabelFragment extends Fragment {

    private static final String LABEL_NODE = "Label";
    private static final String USER_ID = "userID";

    private RecyclerView recyclerView;
    private DatabaseReference database;
    private MyAdapter2 myAdapter2;
    private ArrayList<LabelClass> labelList;

    public LabelFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_label, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingAddBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabelFragment.this.getActivity(),  AddLabelPage.class));
            }
        });

        recyclerView = view.findViewById(R.id.labelList);
        database = FirebaseDatabase.getInstance().getReference(LABEL_NODE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        labelList = new ArrayList<>();
        myAdapter2 = new MyAdapter2(requireContext(), labelList);
        recyclerView.setAdapter(myAdapter2);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query categoryQuery = database.orderByChild(USER_ID).equalTo(currentUserID);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    LabelClass label = categorySnapshot.getValue(LabelClass.class);
                    labelList.add(label);
                }
                myAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get labels", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}