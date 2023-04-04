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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLabelPage extends AppCompatActivity {

    EditText labelName;
    DatabaseReference labelDBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label_page);

        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });

        labelName =(EditText)findViewById(R.id.newLabel);
        labelDBH = FirebaseDatabase.getInstance().getReference().child("Label");

        Button saveBtn=(Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertLabel();
            }
        });
    }

    private void insertLabel(){
        String lName = labelName.getText().toString();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();



        LabelClass labelClass = new LabelClass(lName, userID);


        labelDBH.push().setValue(labelClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "New Label is Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddLabelPage.this,  HomeFragment.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error When Adding New Label", Toast.LENGTH_SHORT).show();
            }
        });
    }

}