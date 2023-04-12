package com.example.expierify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationPage extends AppCompatActivity {

    private EditText days;
    Boolean notificationStatus ;
    TimePicker timePicker;
    int hour;
    int min;
    private DatabaseReference notificationDB;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //days before edit text
        days = findViewById(R.id.days);
        String text = days.getText().toString();
        if (!text.isEmpty()) {
            int intValue = Integer.parseInt(text);
        }

        TextView statusHeader = (TextView)findViewById(R.id.daysHeader);
        EditText daybefore = (EditText) findViewById(R.id.days);
        TextView notificationTime = (TextView)findViewById(R.id.notificationTime);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        //notification status radiogroup
        RadioGroup status = (RadioGroup)findViewById(R.id.notificationStatus);
        RadioButton onNotification = findViewById(R.id.onNotification);
        RadioButton offNotification = findViewById(R.id.offNotification);

        status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find which radio button is selected
                if (checkedId == R.id.onNotification) {
                    notificationStatus=true;
                    statusHeader.setVisibility(View.VISIBLE);
                    daybefore.setVisibility(View.VISIBLE);
                    notificationTime.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.offNotification) {
                    notificationStatus=false;
                    statusHeader.setVisibility(View.GONE);
                    daybefore.setVisibility(View.GONE);
                    notificationTime.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                }else{
                    notificationStatus=true;
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
            }
        });

        Button saveBtn=(Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNotificationSettings();
            }
        });

        notificationDB = FirebaseDatabase.getInstance().getReference().child("Notification");

    }

    private void insertNotificationSettings() {
        if (notificationStatus == null) {
            // Show an error message and return if notificationStatus is null
            Toast.makeText(this, "Please select a notification status", Toast.LENGTH_SHORT).show();
            return;
        }
        int minutes = min;
        int hours = hour;
        Notification notification;
        if(!notificationStatus){
            notification = new Notification(false);
            notificationDB.child(userID).setValue(notification)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Saved Notification Settings", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error when saving notification settings", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }else {
            String daysString = days.getText().toString();
            if (daysString.isEmpty()) {
                days.setError("This field cannot be empty");
            } else {
                int intDays = Integer.parseInt(daysString);
                notification = new Notification(true, intDays, hours, minutes);
                notificationDB.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the existing notification data
                        Notification existingNotification = dataSnapshot.getValue(Notification.class);
                        if (existingNotification != null) {
                            // Update the necessary fields of the Notification object
                            existingNotification.setStatus(notification.getStatus());
                            existingNotification.setDaysBefore(notification.getDaysBefore());
                            existingNotification.setHour(notification.getHour());
                            existingNotification.setMin(notification.getMin());
                            // Create a map with the updated fields
                            Map<String, Object> updateFields = new HashMap<>();
                            updateFields.put("status", existingNotification.getStatus());
                            updateFields.put("daysBefore", existingNotification.getDaysBefore());
                            updateFields.put("hour", existingNotification.getHour());
                            updateFields.put("min", existingNotification.getMin());
                            // Update the specific children of the database node
                            notificationDB.child(userID).updateChildren(updateFields)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Saved Notification Settings", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error when saving notification settings", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error when saving notification settings", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        notificationDB.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);
                TextView statusHeader = (TextView)findViewById(R.id.daysHeader);
                EditText daybefore = (EditText) findViewById(R.id.days);
                TextView notificationTime = (TextView)findViewById(R.id.notificationTime);
                timePicker = (TimePicker) findViewById(R.id.timePicker);
                //notification status radiogroup
                RadioGroup status = (RadioGroup)findViewById(R.id.notificationStatus);
                RadioButton onNotification = findViewById(R.id.onNotification);
                RadioButton offNotification = findViewById(R.id.offNotification);

                if (notification != null && notification.getStatus()) {
                    // If notification status is on, set the form fields with the saved values
                    onNotification.setChecked(true);
                    days.setText(String.valueOf(notification.getDaysBefore()));
                    timePicker.setHour(notification.getHour());
                    timePicker.setMinute(notification.getMin());
                } else {
                    // If notification status is off, hide the form fields
                    offNotification.setChecked(true);
                    statusHeader.setVisibility(View.GONE);
                    daybefore.setVisibility(View.GONE);
                    notificationTime.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}