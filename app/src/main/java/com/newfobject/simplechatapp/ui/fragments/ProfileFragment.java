package com.newfobject.simplechatapp.ui.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.Friend;
import com.newfobject.simplechatapp.model.User;
import com.newfobject.simplechatapp.ui.activities.DialogActivity;
import com.newfobject.simplechatapp.ui.activities.ProfileActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileFragment extends Fragment {


    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.is_online)
    TextView isOnline;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.add_friends_button)
    Button addFriendButton;
    @BindView(R.id.send_message_button)
    Button sendMsgButton;
    @BindView(R.id.profile_load_progress)
    ProgressBar profileProgressBar;
    @BindView(R.id.profile_card)
    CardView profileCard;
    private ProfileActivity profileActivity;
    boolean isUserProfile;
    @BindInt(android.R.integer.config_shortAnimTime)
    int shortAnimDuration;

    public static ProfileFragment newInstance() {

//        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ProfileActivity) {
            profileActivity = (ProfileActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);

        setProfileData();

        return rootView;
    }

    private void setProfileData() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final String userId = getActivity().getIntent().getStringExtra(ProfileActivity.EXTRA_FRIEND_ID);
        isUserProfile = profileActivity.isUserProfile(userId);

        // if this is a current user profile
        if (isUserProfile) {
            sendMsgButton.setVisibility(View.GONE);
            addFriendButton.setVisibility(View.GONE);
        } else {
            // check if friend is already in friend list
            setupButtons(dbRef, userId);

            sendMsgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogActivity.startActivity(getContext(), userId);
                }
            });
        }

        getUserDetails(dbRef, userId);
    }

    private void getUserDetails(DatabaseReference dbRef, final String userId) {
        DatabaseReference reference = dbRef.child(Constants.USERS).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                userEmail.setText(user.getEmail());
                userName.setText(user.getName());

                if (user.getStatus().equals(Constants.Status.ONLINE)) {
                    isOnline.setText(R.string.online);
                } else {
                    isOnline.setText(R.string.offline);
                }

                addFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addUserToFriendList(userId, user.getName(), user.getEmail());
                        Toast.makeText(getContext(),
                                getString(R.string.friend_was_added_toast,
                                        user.getName()),
                                Toast.LENGTH_LONG).show();
                    }
                });

                showProfileCardCrossFadeAnimation();

                if (!isUserProfile) {
                    profileActivity.setupToolBar(dataSnapshot.child(Constants.NAME).getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showProfileCardCrossFadeAnimation() {
        profileCard.setAlpha(0f);
        profileCard.setVisibility(View.VISIBLE);

        profileCard.animate()
                .alpha(1f)
                .setDuration(shortAnimDuration)
                .setListener(null);

        profileProgressBar.animate()
                .alpha(0f)
                .setDuration(shortAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        profileProgressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void setupButtons(DatabaseReference dbRef, final String userId) {
        FirebaseUser user = profileActivity.getAuth().getCurrentUser();
        if (user != null) {
            String firebaseUid = user.getUid();
            DatabaseReference reference = dbRef.child(Constants.FRIENDS + "/" + firebaseUid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(userId)) {
                        addFriendButton.setText(R.string.you_are_friends);
                        addFriendButton.setEnabled(false);
                        setHasOptionsMenu(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void addUserToFriendList(String friendId, String name, String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            // add friend to user in db
            Friend friend = new Friend(name, email);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> addFriendMap =
                    (HashMap<String, Object>) mapper.convertValue(friend, HashMap.class);

            Friend addUser = new Friend(user.getDisplayName(), user.getEmail());

            Map<String, Object> addUserMap =
                    (HashMap<String, Object>) mapper.convertValue(addUser, HashMap.class);

            Map<String, Object> update = new HashMap<>();
            update.put(userID + "/" + friendId, addFriendMap);
            update.put(friendId + "/" + userID, addUserMap);

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.FRIENDS);
            dbRef.updateChildren(update);

            profileActivity.redrawFragment();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_friend) {
            deleteUserFromFriendList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteUserFromFriendList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String friendId = getActivity().getIntent()
                    .getStringExtra(ProfileActivity.EXTRA_FRIEND_ID);

            Map<String, Object> deleteFromFriends = new HashMap<>();

            // delete user from users table
            deleteFromFriends.put(Constants.FRIENDS + "/" + friendId + "/" + userId, null);
            deleteFromFriends.put(Constants.FRIENDS + "/" + userId + "/" + friendId, null);

            // delete recent messages
            deleteFromFriends.put(Constants.RECENT_MESSAGE + "/" +
                    friendId + "/" + userId, null);
            deleteFromFriends.put(Constants.RECENT_MESSAGE + "/" +
                    userId + "/" + friendId, null);

            DatabaseReference dbref = FirebaseDatabase.getInstance()
                    .getReference();

            dbref.updateChildren(deleteFromFriends);

            profileActivity.redrawFragment();
        }
    }
}
