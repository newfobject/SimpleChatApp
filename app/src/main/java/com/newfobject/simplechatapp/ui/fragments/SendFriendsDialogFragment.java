package com.newfobject.simplechatapp.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newfobject.simplechatapp.R;

public class SendFriendsDialogFragment extends DialogFragment {

    public static SendFriendsDialogFragment newInstance() {

        Bundle args = new Bundle();

        SendFriendsDialogFragment fragment = new SendFriendsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.send_friends_dialog_fragment, container, false);
        //TODO handle item click
        return rootView;
    }

}
