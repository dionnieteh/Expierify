package com.example.expierify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;

import com.example.expierify.databinding.ActivityMainBinding;

import java.util.Calendar;

public class AddProductPage extends AppCompatActivity {

    private DatePickerDialog picker;
    private ActivityMainBinding binding;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page);

        //Back arrow image button redirects user to homeFragment
        ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
            }
        });

        //cancel button image button redirects user to homeFragment
        Button cancelBtn= (Button)findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProductPage.this,  HomeFragment.class));
            }
        });

        //select image to store food images

        Button selectImgBtn= (Button)findViewById(R.id.selectImgBtn);
        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //set DatePicker for expiry Date
        EditText expiry_date=(EditText)findViewById(R.id.expiry_date);
        expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                int month= calendar.get(Calendar.MONTH);
                int year= calendar.get(Calendar.YEAR);

                //define datepicker
                picker = new DatePickerDialog(AddProductPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        expiry_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);

                picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                picker.show();
            }
        });
    }

    private void selectImage(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data !=null && data.getData() != null){
            imageUri=data.getData();
            ImageView foodImage = (ImageView) findViewById(R.id.foodImage);
            foodImage.setImageURI(imageUri);

        }
    }
}