package com.example.uidesignlab;

public class User {

    private String name;
    private String role;
    private String email;

    // Empty constructor
    public User() {
    }

    // Constructor with three fields
    public User(String name, String role, String email) {
        this.name = name;
        this.role = role;
        this.email = email;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }
}
