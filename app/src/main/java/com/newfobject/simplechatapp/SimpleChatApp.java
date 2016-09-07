package com.newfobject.simplechatapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

public class SimpleChatApp extends Application {
    private DatabaseReference userOnlineRef;
    private ValueEventListener eventListener;
    private FirebaseDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);

        database.setPersistenceEnabled(true);

        setAuthStateChangeListener();

    }

    /**
     * Updates user status on connects or disconnects
     * @param user current user
     */
    private void updateUserStatus(final FirebaseUser user) {
        String userId = user.getUid();
        if (!userId.isEmpty()) {

            userOnlineRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.USERS).child(userId).child(Constants.STATUS);

            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.getValue(String.class);
                    if (status != null &&
                            status.equals(Constants.Status.OFFLINE)) {
                        userOnlineRef.setValue(Constants.Status.ONLINE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            userOnlineRef.addValueEventListener(eventListener);
            userOnlineRef.onDisconnect().setValue(Constants.Status.OFFLINE);

        }
    }

    /**
     * Performs status updates in firebase database
     */
    private void setAuthStateChangeListener() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // remove listener to prevent status update to previous account
                // if user changes account
                if (userOnlineRef != null && eventListener != null) {
                    userOnlineRef.removeEventListener(eventListener);
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    database.goOnline();
                    updateUserStatus(user);
                } else {
                    database.goOffline();
                }
            }
        });
    }
}
