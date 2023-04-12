package com.example.expierify;

public class LabelClass {
    String lName;
    String userID;

    public LabelClass(String lName) {
        this.lName = lName;
    }

    public LabelClass() {
        // Required empty constructor for Firebase
    }

    public String getlName() {
        return lName;
    }

    public String getUserID() {
        return userID;
    }
}
