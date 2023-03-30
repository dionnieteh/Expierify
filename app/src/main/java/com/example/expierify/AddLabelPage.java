package com.example.expierify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
                startActivity(new Intent(AddLabelPage.this,  ProfileFragment.class));
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


        LabelClass labelClass = new LabelClass(lName);


        //push() to generate unique key for the new category
        labelDBH.push().setValue(labelClass);
        Toast.makeText(this, "New Label is Added", Toast.LENGTH_SHORT).show();
    }

}