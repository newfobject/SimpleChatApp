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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.ui.activities.DialogActivity;
import com.newfobject.simplechatapp.ui.activities.ProfileActivity;
import com.newfobject.simplechatapp.ui.adapters.RecentMessagesAdapter;
import com.newfobject.simplechatapp.ui.decorations.DividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MessagesFragment extends Fragment implements RecentMessagesAdapter.onItemClickListener {


    @BindView(R.id.messages_recycler_view)
    RecyclerView recyclerView;
    private RecentMessagesAdapter adapter;
    private DatabaseReference databaseReference;
    private boolean defaultAdapter = true;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference(Constants.RECENT_MESSAGE)
                    .child(user.getUid());
            adapter = new RecentMessagesAdapter(databaseReference, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                    LinearLayoutManager.VERTICAL));

            recyclerView.setAdapter(adapter);
        }
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            SendFriendsDialogFragment friendChatSelectDialog = SendFriendsDialogFragment
                    .newInstance(user.getUid());

            friendChatSelectDialog.show(getFragmentManager(), "dialog");
        }

    }

    private SearchView.OnQueryTextListener createOnQueryTextListener() {

        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    newText = newText.toLowerCase();
                    Query query = databaseReference.orderByChild(Constants.CASE_INSENSITIVE_NAME)
                            .startAt(newText).endAt(newText + "~")
                            .limitToFirst(5);
                    recyclerView.swapAdapter(new RecentMessagesAdapter(query, MessagesFragment.this),
                            true);

                    defaultAdapter = false;
                    return true;
                } else if (!defaultAdapter) {
                    recyclerView.swapAdapter(adapter, true);
                    defaultAdapter = true;
                    return true;
                }

                return false;
            }
        };
    }

    @Override
    public void onAdapterItemClick(String key) {
        DialogActivity.startActivity(getContext(), key);
    }

    @Override
    public void onProfileImageClick(String friendId) {
        ProfileActivity.startActivity(getContext(), friendId);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (adapter != null) {
            adapter.cleanup();
        }
    }
}
