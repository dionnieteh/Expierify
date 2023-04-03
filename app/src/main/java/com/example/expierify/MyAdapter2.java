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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
                // navigate to another page based on item click
                // for example, you can start a new activity
                //Intent intent = new Intent(context, SubCategoryPage.class);
                //intent.putExtra("foodID", category.getFoodId()); // pass some data to the next activity
                // Get a reference to the Firebase database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();

                // Query the database for the foodIDs
                Query query = ref.child("Food").orderByChild("userID_label").equalTo(label.getUserID() + "_" +label.getlName());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> foodIDs = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String foodID = child.getKey();
                            foodIDs.add(foodID);
                        }

                        // Create the intent and pass the list of foodIDs as an extra
                        Intent intent = new Intent(context, SubCategoryPage.class);
                        intent.putStringArrayListExtra("foodIDs", (ArrayList<String>) foodIDs);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed get FoodID", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SubCategoryPage.class);
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