package com.newfobject.simplechatapp.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.ChatDialog;
import com.newfobject.simplechatapp.model.RecentMessage;
import com.newfobject.simplechatapp.ui.adapters.RecentMessagesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    @BindView(R.id.messages_recycler_view)
    RecyclerView recyclerView;

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    public MessagesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //TODO fix your_uid
        DatabaseReference databaseReference = firebaseDatabase.getReference("recent_message").child("your_uid");
        RecentMessagesAdapter adapter = new RecentMessagesAdapter(RecentMessage.class,
                R.layout.recent_message_adapter_item, databaseReference);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        if (searchView != null) {
            SearchView.OnQueryTextListener onQueryTextListener = createOnQueryTextListener();
            searchView.setOnQueryTextListener(onQueryTextListener);
        }


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.addChatFab)
    public void onAddChatClick() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment dialogFragment = getFragmentManager().findFragmentByTag("dialog");
        if (dialogFragment != null) {
            ft.remove(dialogFragment);
        }
        ft.addToBackStack(null);

        SendFriendsDialogFragment newFragment = SendFriendsDialogFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    private SearchView.OnQueryTextListener createOnQueryTextListener() {

        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO hide recyclerview items if they don't match newtext
                return false;
            }
        };
    }
}
