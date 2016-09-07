package com.newfobject.simplechatapp.model;


public class User {
    private String name;
    private String email;
    private String status;
    private String index_name;

    public User() {
    }

    public User(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
        index_name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getIndex_name() {
        return index_name;
    }
}
