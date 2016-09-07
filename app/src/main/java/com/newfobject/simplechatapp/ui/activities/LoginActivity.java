package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.User;
import com.newfobject.simplechatapp.ui.fragments.CreateAccountFragment;
import com.newfobject.simplechatapp.ui.fragments.LoginFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;


public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {


    public static final String BACK_STACK_NAME = "back_stack_name";
    private static final int RC_GOOGLE_SIGN_IN = 3323;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment.newInstance())
                    .commit();
        }
    }

    public void addCreateAccountFragment() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,
                        CreateAccountFragment.newInstance())
                .addToBackStack(BACK_STACK_NAME)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * After first successful login creates a user in firebase database
     * @param name - user name
     * @param email - user email
     */
    public void firstSigningWithEmailAndPassword(String name, String email) {

        // change user name in firebase
        FirebaseUser currentUser = getAuth().getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            currentUser.updateProfile(profile);

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.USERS);

            User user = new User(name, email, Constants.Status.ONLINE);

            HashMap<String, Object> userMap = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(user, Map.class);

            // use user id as a key
            Map<String, Object> update = new HashMap<>();
            update.put(currentUser.getUid(), userMap);

            reference.updateChildren(update);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        final String name = account.getDisplayName();
        final String email = account.getEmail();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: sign in with credentials " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            firstSigningWithEmailAndPassword(name, email);
                        }

                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: sign in with credentials " + task.getException());
                        }
                    }
                });
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, R.string.google_play_services_error, Toast.LENGTH_SHORT).show();
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // if result is successful, auth with firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}