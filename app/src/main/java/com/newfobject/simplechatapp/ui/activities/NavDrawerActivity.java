package com.newfobject.simplechatapp.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.fragments.LogoutAlertFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class NavDrawerActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private int drawerSelectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavDrawer() {
        ButterKnife.bind(this);
        final Context context = NavDrawerActivity.this;

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {


                drawerSelectedItemId = item.getItemId();
                item.setCheckable(true);
                item.setChecked(true);


                // show drawer closing animation
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (drawerSelectedItemId) {
                            case R.id.drawer_friends:
                                FriendsListActivity.startActivity(context);
                                break;
                            case R.id.drawer_recent:
                                RecentMessagesActivity.startActivity(context);
                                break;
                            case R.id.drawer_add_friend:
                                AddFriendActivity.startActivity(context);
                                break;
                            case R.id.drawer_about:
                                AboutActivity.startActivity(context);
                                break;
                        }
                    }
                }, 250);

                closeNavDrawer();

                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);

        // get user email from firebase
        TextView email = ButterKnife.findById(headerView, R.id.user_email);
        TextView name = ButterKnife.findById(headerView, R.id.user_name);
        final String userId;
        FirebaseUser user = getAuth().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            userId = user.getUid();

            ButterKnife.findById(headerView, R.id.profile_image)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ProfileActivity.startActivity(context, userId);
                                }
                            }, 250);
                            closeNavDrawer();
                        }
                    });

        }

        ButterKnife.findById(headerView, R.id.profile_logout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogoutAlertFragment alertFragment = LogoutAlertFragment.newInstance();
                        alertFragment.show(getFragmentManager(), "logout_alert");
                    }
                });


        String title = null;

        if (this instanceof RecentMessagesActivity) {
            title = getString(R.string.recent_messages_title);
        } else if (this instanceof FriendsListActivity) {
            title = getString(R.string.friends_list_title);
        } else if (this instanceof AddFriendActivity) {
            title = getString(R.string.add_friend_activity_title);
        }

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null && title != null) {
            supportActionBar.setTitle(title);
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.app_name,
                    R.string.app_name);

            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(drawerToggle);
        }

    }


    protected void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        setSelectedDrawerItem();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
        setSelectedDrawerItem();
    }

    private void setSelectedDrawerItem() {
        if (this instanceof RecentMessagesActivity) {
            navigationView.setCheckedItem(R.id.drawer_recent);
        } else if (this instanceof FriendsListActivity) {
            navigationView.setCheckedItem(R.id.drawer_friends);
        } else if (this instanceof AddFriendActivity) {
            navigationView.setCheckedItem(R.id.drawer_add_friend);
        } else {
            navigationView.setCheckedItem(R.id.drawer_about);
        }
    }

}
