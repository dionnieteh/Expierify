package com.example.expierify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;


public class Sort extends AppCompatActivity {

    private boolean isCategoryFragmentVisible = true;
    private boolean isLabelFragmentVisible = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        Button categBtn = findViewById(R.id.categBtn);
        categBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView6, CategoryFragment.class, null )
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });
        Button labelBtn = findViewById(R.id.labelBtn);
        labelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView6, LabelFragment.class, null )
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });
        /*SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text changes
                return true;
            }
        });*/
    }
}