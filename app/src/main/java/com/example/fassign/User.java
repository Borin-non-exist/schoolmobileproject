package com.example.fassign;

// User.java

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String imageUrl;

    // Empty constructor required for Firebase
    public User() {
    }

    public User(String userId, String firstName, String lastName, String imageUrl) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

