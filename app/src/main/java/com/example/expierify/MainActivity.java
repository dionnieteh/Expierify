package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.expierify.databinding.ActivityMainBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ExpiredAdapter adapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Food> foodList;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_custom);

        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment()); //Home will be open when application is on
        getExpiredCount(binding);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.labels:
                    replaceFragment(new SortFragment());
                    break;
                case R.id.add:
                    Intent intent = new Intent(MainActivity.this, AddProductPage.class);
                    startActivity(intent);
                    break;
                case R.id.expired:
                    replaceFragment(new ExpiredFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.email.equals(email)) {
                        emailExists = true;
                        break;
                    }
                }
                if (!emailExists) {
                    String name = currentUser.getDisplayName();
                    String email = currentUser.getEmail();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("users");
                    String uid = currentUser.getUid();
                    User user = new User(name, email);
                    usersRef.child(uid).setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });
    }

    public void getExpiredCount(ActivityMainBinding binding) {
        // initialize the RecyclerView and adapter
        mRecyclerView = findViewById(R.id.expireditemlist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new ExpiredAdapter(this, foodList);
        // set the adapter for the RecyclerView
        mRecyclerView.setAdapter(adapter);

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
                // An icon only badge will be displayed unless a number is set:
                if(adapter.getItemCount()>0){
                    BadgeDrawable badge = MainActivity.this.binding.bottomNavigationView.getOrCreateBadge(R.id.expired);
                    badge.setVisible(true);
                    badge.setNumber(adapter.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //bottom navi able to switch between fragments
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment); // replace frame layout when button is clicked
        fragmentTransaction.commit();
    }
}