package com.newfobject.simplechatapp.model;


public class ChatDialog {
    private String creatorEmail;
    private String companionEmail;

    public ChatDialog(String creator, String companion) {
        this.creatorEmail = creator;
        this.companionEmail = companion;
    }

    public String getCreator() {
        return creatorEmail;
    }

    public String getCompanion() {
        return companionEmail;
    }
}
