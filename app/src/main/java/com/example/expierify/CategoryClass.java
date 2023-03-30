package com.example.expierify;

public class CategoryClass {

    String cName;
    String userID;

    public CategoryClass(String cName, String userID) {
        this.cName = cName;
        this.userID = userID;
    }

    public String getcName() {
        return cName;
    }

    public String getUserID() {
        return userID;
    }
}
