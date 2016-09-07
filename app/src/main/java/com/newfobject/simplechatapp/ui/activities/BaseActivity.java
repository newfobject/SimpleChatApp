package com.newfobject.simplechatapp.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.newfobject.simplechatapp.R;

import butterknife.BindView;

public class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public GoogleApiClient googleApiClient;

    private FirebaseAuth.AuthStateListener authStateListener;


    private FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user signed in
                    Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName() + " \n" +
                            user.getEmail() + "\n" + user.getUid());

                    if (BaseActivity.this instanceof LoginActivity) {

                        RecentMessagesActivity.startActivity(BaseActivity.this);
                        finish();
                    }

                } else {

                    if (!(BaseActivity.this instanceof LoginActivity)) {

                        LoginActivity.startActivity(BaseActivity.this);
                        finish();
                    }

                }
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean onBackPressedOnce;

    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
            return;
        } else if (onBackPressedOnce) {
            super.onBackPressed();
        }
        onBackPressedOnce = true;
        Toast.makeText(this, R.string.click_back_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressedOnce = false;
            }
        }, 2000);
    }

    public void setupToolBar(String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public void setupToolBar(String title, boolean setDisplayHomeAsUpEnabled) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(setDisplayHomeAsUpEnabled);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void logout() {
        getAuth().signOut();
        Auth.GoogleSignInApi.revokeAccess(googleApiClient);
    }


}
