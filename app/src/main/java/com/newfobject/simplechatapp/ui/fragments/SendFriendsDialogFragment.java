package com.newfobject.simplechatapp.ui.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.DialogActivity;
import com.newfobject.simplechatapp.ui.decorations.DividerItemDecoration;
import com.newfobject.simplechatapp.ui.adapters.SelectFriendAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendFriendsDialogFragment extends DialogFragment {

    private static final String USER_ID_KEY = "user_id_key";
    @BindView(R.id.select_friend_rcv)
    RecyclerView recyclerView;
    private DatabaseReference reference;
    private SelectFriendAdapter adapter;

    public static SendFriendsDialogFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(USER_ID_KEY, userId);

        SendFriendsDialogFragment fragment = new SendFriendsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = getArguments().getString(USER_ID_KEY);
        if (userId != null)
            reference = FirebaseDatabase.getInstance().getReference(Constants.FRIENDS).child(userId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.send_friends_dialog_fragment, null);
        ButterKnife.bind(this, view);

        SelectFriendAdapter.FriendClickListener listener = new SelectFriendAdapter.FriendClickListener() {
            @Override
            public void onClick(String friendId) {
                DialogActivity.startActivity(getContext(), friendId);
                dismiss();
            }
        };
        adapter = new SelectFriendAdapter(reference, listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.select_a_friend_title))
                .setView(view);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (adapter != null)
            adapter.cleanup();
    }
}
