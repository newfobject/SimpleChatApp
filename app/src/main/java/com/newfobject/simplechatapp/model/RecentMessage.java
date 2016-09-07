package com.newfobject.simplechatapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.ServerValue;

import java.util.Map;


public class RecentMessage {
    private String index_name;
    private String text;
    private long timestamp;
    private String name;

    public RecentMessage() {
    }

    public RecentMessage(String text, String name) {
        this.text = text;
        this.name = name;
        index_name = name.toLowerCase();
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public long getTimeStampLong() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getIndex_name() {
        return index_name;
    }
}
