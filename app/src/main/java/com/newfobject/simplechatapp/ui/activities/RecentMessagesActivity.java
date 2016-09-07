package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.fragments.MessagesFragment;

public class RecentMessagesActivity extends NavDrawerActivity {


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RecentMessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolBar();
        setupNavDrawer();


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,
                            MessagesFragment.newInstance()).commit();
        }
    }

}