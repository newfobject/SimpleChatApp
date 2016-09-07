package com.newfobject.simplechatapp.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.newfobject.simplechatapp.ui.activities.ProfileActivity;
import com.newfobject.simplechatapp.ui.adapters.AddFriendAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendFragment extends Fragment {

    @BindView(R.id.add_friend_rcv)
    RecyclerView recyclerView;
//
//    @BindView(R.id.search_edittext)
//    EditText searchEditText;

    DatabaseReference databaseReference;
    AddFriendAdapter addFriendAdapter;
    String userId;


    public static AddFriendFragment newInstance() {

        Bundle args = new Bundle();

        AddFriendFragment fragment = new AddFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddFriendFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.USERS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        menuItem.expandActionView();

        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.write_your_friend_mail_hint));
            SearchView.OnQueryTextListener onQueryTextListener = createOnQueryTextListener();
            searchView.setOnQueryTextListener(onQueryTextListener);
        }

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getActivity().onBackPressed();
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    private SearchView.OnQueryTextListener createOnQueryTextListener() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        final AddFriendAdapter.OnProfileClick listener = new AddFriendAdapter.OnProfileClick() {
            @Override
            public void onClick(String userId) {
                ProfileActivity.startActivity(getContext(), userId);
            }
        };

        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                AddFriendAdapter oldAdapter = addFriendAdapter;

                if (newText.length() > 3 && userId != null) {
                    Query query = databaseReference.orderByChild(Constants.EMAIL)
                            .startAt(newText).endAt(newText + "~")
                            .limitToFirst(5);
                    addFriendAdapter = new AddFriendAdapter(query, userId, listener);
                    recyclerView.swapAdapter(addFriendAdapter, true);
                    if (oldAdapter != null)
                        oldAdapter.cleanup();
                } else {
                    recyclerView.setAdapter(null);
                    if (oldAdapter != null)
                        oldAdapter.cleanup();

                }
                return false;
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            final String userId = user.getUid();
//
//            final AddFriendAdapter.OnProfileClick listener = new AddFriendAdapter.OnProfileClick() {
//                @Override
//                public void onClick(String userId) {
//                    ProfileActivity.startActivity(getContext(), userId);
//                }
//            };
//
//            searchEditText.addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    String input = searchEditText.getText().toString().toLowerCase();
//                    if (addFriendAdapter != null) {
//                        addFriendAdapter.cleanup();
//                    }
//
//                    if (input.equals("") || input.length() < 3) {
//                        recyclerView.setAdapter(null);
//                    } else {
//                        Query query = databaseReference.orderByChild(Constants.EMAIL)
//                                .startAt(input).endAt(input + "~")
//                                .limitToFirst(5);
//                        addFriendAdapter = new AddFriendAdapter(query, userId, listener);
//                        recyclerView.swapAdapter(addFriendAdapter, true);
//                    }
//                }
//            });
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (addFriendAdapter != null)
            addFriendAdapter.cleanup();
    }
}
