package com.example.expierify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    Context context;
    ArrayList<LabelClass> labelList;

    public MyAdapter2(Context context, ArrayList<LabelClass> labelList) {
        this.context = context;
        this.labelList = labelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.labelitem, parent, false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LabelClass label = labelList.get(position);
        holder.labelName.setText(label.getlName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String labelName = label.getlName();
                String userID = label.getUserID();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String currentUserID= currentUser.getUid();
                DatabaseReference foodRef = database.getReference("Food");
                Query query = foodRef.orderByChild("userID").equalTo(currentUserID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mainLabel = label.getlName(); // get the category name
                        ArrayList<String> foodIDs = new ArrayList<>();
                        ArrayList<String> labelTitle = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String label = snapshot.child("label").getValue(String.class);
                            if (label != null && label.equals(mainLabel)) {
                                String foodID = snapshot.getKey();
                                String expiryDate = snapshot.child("expiry").getValue(String.class);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                                LocalDate expiry = LocalDate.parse(expiryDate, formatter);
                                LocalDate today = LocalDate.now();
                                if (expiry.isAfter(today) || expiry.isEqual(today)) {
                                    foodIDs.add(foodID);
                                    labelTitle.add(mainLabel);
                                }
                            }
                        }
                        Intent intent = new Intent(context, SubLabelPage.class);
                        intent.putStringArrayListExtra("foodIDs", (ArrayList<String>) foodIDs);
                        intent.putExtra("labelTitle", mainLabel);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to get food ID.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView labelName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            labelName= itemView.findViewById(R.id.labelName);
        }


    }
}