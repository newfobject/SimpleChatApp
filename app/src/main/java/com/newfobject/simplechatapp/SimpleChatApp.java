package com.newfobject.simplechatapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

/**
 * Created by Alexey on 7/22/2016.
 */
public class SimpleChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);
    }
}
