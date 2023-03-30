package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCategoryPage extends AppCompatActivity {

    EditText categoryName;
    DatabaseReference category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category_page);

        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCategoryPage.this,  ProfileFragment.class));
            }


        });

        categoryName =(EditText)findViewById(R.id.newName);
        category = FirebaseDatabase.getInstance().getReference().child("Category");

        Button saveBtn=(Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCategory();
            }
        });
    }

    private void insertCategory(){
        String cName = categoryName.getText().toString();


        CategoryClass categoryClass = new CategoryClass(cName);

        
        //push() to generate unique key for the new category
        category.push().setValue(categoryClass);
        Toast.makeText(this, "New Category is Added", Toast.LENGTH_SHORT).show();
    }



}