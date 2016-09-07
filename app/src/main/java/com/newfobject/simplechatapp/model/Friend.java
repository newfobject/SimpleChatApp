package com.newfobject.simplechatapp.model;


public class Friend {
    private String name;
    private String email;

    public Friend() {
    }

    public Friend(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
