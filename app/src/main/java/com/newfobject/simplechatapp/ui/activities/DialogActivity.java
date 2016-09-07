package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.fragments.DialogChatFragment;

import butterknife.ButterKnife;

public class DialogActivity extends BaseActivity {

    public static final String EXTRA_FRIEND_ID = "friend_id";

    public static void startActivity(Context context, String friendId) {
        Intent intent = new Intent(context, DialogActivity.class);
        intent.putExtra(EXTRA_FRIEND_ID, friendId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            String friendId = getIntent().getStringExtra(EXTRA_FRIEND_ID);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    DialogChatFragment.newInstance(friendId)).commit();
        }
    }


    public void setupToolBarWithStatus(String title, String status) {
        if (status.equals(Constants.Status.ONLINE)) {
            toolbar.setSubtitle(getString(R.string.online));
        } else if (status.equals(Constants.Status.OFFLINE) /*|| status.equals(Constants.LOGGED_OUT)*/) {
            toolbar.setSubtitle(getString(R.string.offline));
        }
        setupToolBar(title);
    }
}
