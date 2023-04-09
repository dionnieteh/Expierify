package com.example.expierify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Food {
    String userID;
    String foodId;
    String name;
    String desc ;
    String expiry;
    String category ;
    String label ;
    String image; // add field for image

    public Food(String userID, String foodId, String name, String desc, String expiry, String category, String label) {
        this.userID = userID;
        this.foodId = foodId;
        this.name = name;
        this.desc = desc;
        this.expiry = expiry;
        this.category = category;
        this.label = label;
    }

    public Food() {
        // Required empty constructor for Firebase
    }


    public String getUserID(){
        return userID;
    }

    public String getFoodId(){ return foodId; }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isExpired() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date expiryDate = null;
        try {
            expiryDate = sdf.parse(this.expiry);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        return currentDate.after(expiryDate);
    }
}
