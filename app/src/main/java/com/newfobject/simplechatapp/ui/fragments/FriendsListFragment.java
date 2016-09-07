package com.newfobject.simplechatapp.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.ProfileActivity;
import com.newfobject.simplechatapp.ui.decorations.DividerItemDecoration;
import com.newfobject.simplechatapp.ui.adapters.FriendsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsListFragment extends Fragment {

    @BindView(R.id.friends_list_rcv)
    RecyclerView recyclerView;

    DatabaseReference reference;

    public FriendsListFragment() {
    }

    public static FriendsListFragment newInstance() {

        Bundle args = new Bundle();

        FriendsListFragment fragment = new FriendsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userid = user.getUid();
            reference = FirebaseDatabase.getInstance()
                    .getReference(Constants.FRIENDS)
                    .child(userid);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, rootView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        FriendsListAdapter.ClickListener clickListener = new FriendsListAdapter.ClickListener() {
            @Override
            public void onItemFriendClick(String friendId) {
                ProfileActivity.startActivity(getContext(), friendId);
            }
        };

        recyclerView.setAdapter(new FriendsListAdapter(reference, clickListener));
        return rootView;
    }

}
