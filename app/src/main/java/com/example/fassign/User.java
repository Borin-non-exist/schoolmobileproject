package com.example.fassign;
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String imageUrl;

    // Constructors, getters...

    // Constructor without imageUrl (for basic user information)
    public User(String userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Constructor with all fields (for complete user information)
    public User(String userId, String firstName, String lastName, String imageUrl) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
    }

    // Getters...

    // Setter for imageUrl
    public void setImageUrl(String imageUrl) {
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

