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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private static final String CATEGORY_NODE = "Category";
    private RecyclerView recyclerView;
    private DatabaseReference database;
    private MyAdapter myAdapter;
    private ArrayList<CategoryClass> categoryList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();
    private TextView empty;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingAddBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryFragment.this.getActivity(),  AddCategoryPage.class));
            }
        });

        recyclerView = view.findViewById(R.id.categoryList);
        database = FirebaseDatabase.getInstance().getReference(CATEGORY_NODE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        empty = view.findViewById(R.id.textView);
        categoryList = new ArrayList<>();
        myAdapter = new MyAdapter(requireContext(), categoryList);
        recyclerView.setAdapter(myAdapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference(CATEGORY_NODE).child(userID);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear(); // clear the list before adding new items
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryClass category = categorySnapshot.getValue(CategoryClass.class);
                    categoryList.add(category);
                }
                myAdapter.notifyDataSetChanged();
                if (myAdapter.getItemCount()==0){
                    empty.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to get categories", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}