package com.newfobject.simplechatapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Message {

    private String text;
    private boolean your;
    private long timestamp;

    public Message() {
    }

    public Message(String text) {
        this.your = true;
        this.text = text;
    }

    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public long getTimestampLong() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public boolean isYour() {
        return your;
    }


}
