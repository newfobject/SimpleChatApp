package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.fragments.ProfileFragment;

import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {

    public static String EXTRA_FRIEND_ID = "friend_id";

    public static void startActivity(Context context, String friendId) {
        Intent intent = new Intent(context, ProfileActivity.class).putExtra(EXTRA_FRIEND_ID, friendId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        String userId = getIntent().getStringExtra(EXTRA_FRIEND_ID);
        if (isUserProfile(userId)) {
            setupToolBar(getString(R.string.my_profile_title));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ProfileFragment.newInstance())
                    .commit();
        }
    }

    /**
     * @param userId uid from firebase auth
     * @return true if it is a profile of the current user,
     */
    public boolean isUserProfile(String userId) {
        FirebaseUser currentUser = getAuth().getCurrentUser();
        return currentUser != null && currentUser.getUid().equals(userId);
    }


    /**
     * replace previous fragment with a new one
     * to display updates
     */
    public void redrawFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment.newInstance())
                .commit();
    }
}

