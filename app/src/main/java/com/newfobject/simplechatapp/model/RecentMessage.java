package com.newfobject.simplechatapp.model;

/**
 * Created by Alexey on 7/22/2016.
 */
public class RecentMessage {
    private String text;
    private long timestamp;

    public RecentMessage() {
    }

    public RecentMessage(String text, long timeStamp) {
        this.text = text;
        this.timestamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
