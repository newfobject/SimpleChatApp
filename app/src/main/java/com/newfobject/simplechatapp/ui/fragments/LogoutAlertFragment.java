package com.newfobject.simplechatapp.ui.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.BaseActivity;

public class LogoutAlertFragment extends DialogFragment {

    public static LogoutAlertFragment newInstance() {

        Bundle args = new Bundle();

        LogoutAlertFragment fragment = new LogoutAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public LogoutAlertFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sign_out_alert)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((BaseActivity) getActivity()).logout();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
