package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCategoryPage extends AppCompatActivity {

    private EditText categoryName;
    private DatabaseReference category;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category_page);

        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });

        categoryName =(EditText)findViewById(R.id.newName);
        InputFilter[] limitWord = new InputFilter[] {new ExactLengthFilter(13)};
        categoryName.setFilters(limitWord);
        category = FirebaseDatabase.getInstance().getReference().child("Category").child(userID);

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
        if (cName.isEmpty()){
            categoryName.setError("This field cannot be empty");
        }else{
            CategoryClass categoryClass = new CategoryClass(cName);
            category.child(cName).setValue(categoryClass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "New Category is Added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error When Adding New Category", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        

    }



}