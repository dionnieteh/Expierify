package com.example.expierify;

public class Food {
    String userID;
    String foodId;
    String name;
    String desc ;
    String expiry;
    String category ;
    String label ;

    public Food(String userID, String foodId, String name, String desc, String expiry, String category, String label) {
        this.userID=userID;
        this.foodId=foodId;
        this.name = name;
        this.desc = desc;
        this.expiry = expiry;
        this.category = category;
        this.label = label;
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
}
