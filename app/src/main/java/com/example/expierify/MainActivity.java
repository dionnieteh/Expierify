package com.example.expierify;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.expierify.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        ActivityMainBinding binding;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment()); //Home will be open when application is on

        binding.bottomNavigationView.setOnItemSelectedListener(item->{
            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.category:
                    replaceFragment(new CategoryFragment());
                    break;
                case R.id.camera:
                    //replaceFragment(new CameraFragment());
                    break;
                case R.id.tags:
                    replaceFragment(new LabelFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
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