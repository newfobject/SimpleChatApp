package com.newfobject.simplechatapp.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.Message;
import com.newfobject.simplechatapp.model.RecentMessage;
import com.newfobject.simplechatapp.ui.activities.DialogActivity;
import com.newfobject.simplechatapp.ui.adapters.DialogAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogChatFragment extends Fragment {

    private static final String FRIEND_ID_KEY = "friend_uid_key";

    @BindView(R.id.new_message_edittext)
    EditText newMessageEditText;
    @BindView(R.id.dialog_rcv)
    RecyclerView recyclerView;

    private DatabaseReference dbRef;
    private DialogAdapter dialogAdapter;
    private String friendName;
    private String userId;
    private String friendID;
    private DialogActivity dialogActivity;
    private String userName;


    public static DialogChatFragment newInstance(String friendID) {

        Bundle args = new Bundle();
        args.putString(FRIEND_ID_KEY, friendID);

        DialogChatFragment fragment = new DialogChatFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public DialogChatFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogActivity) {
            dialogActivity = (DialogActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendID = getArguments().getString(FRIEND_ID_KEY);

        setToolbarData();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            userName = user.getDisplayName();
            dbRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.DIALOGS)
                    .child(userId)
                    .child(friendID);
        }

    }

    /**
     * Updates user status when data changes
     */
    private void setToolbarData() {
        setHasOptionsMenu(true);

        DatabaseReference dbReference = FirebaseDatabase.getInstance()
                .getReference(Constants.USERS)
                .child(friendID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendName = dataSnapshot.child(Constants.NAME).getValue(String.class);
                String status = dataSnapshot.child(Constants.STATUS).getValue(String.class);

                dialogActivity.setupToolBarWithStatus(friendName, status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
        ButterKnife.bind(this, rootView);

        setupViews();

        return rootView;
    }

    private void setupViews() {
        newMessageEditText.requestFocus();
        dialogAdapter = new DialogAdapter(dbRef);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);

        dialogAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int items = dialogAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (items - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dialogAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0)
                    hideKeyboard(getView());
            }
        });

//        newMessageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//                    sendMessage();
////                    hideKeyboard(getView());
//
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imn = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.send_message_button)
    void sendMessage() {
        String messageText = newMessageEditText.getText().toString().trim();
        if (!messageText.isEmpty() && friendName != null && !friendName.isEmpty()) {

            ObjectMapper mapper = new ObjectMapper();
            Message message = new Message(messageText);

            //create a map with message
            Map<String, Object> msgToUser = mapper.convertValue(message, HashMap.class);

            Map<String, Object> msgToFriend = new HashMap<>();
            msgToFriend.putAll(msgToUser);
            msgToFriend.put(Constants.IS_YOUR, false);

            if (messageText.length() > 25) {
                messageText = messageText.substring(0, 25) + "...";
            }

            // create a map with recent message
            Map<String, Object> recentMsgToUser =
                    mapper.convertValue(new RecentMessage(messageText, friendName), HashMap.class);

            Map<String, Object> recentMsgToFriend =
                    mapper.convertValue(new RecentMessage(messageText, userName), HashMap.class);

            // generate unique push id
            DatabaseReference pushDbRef = dbRef.push();
            String pushId = pushDbRef.getKey();

            // combine all maps in one to make updates in different firebase-db places
            // in a single request
            Map<String, Object> post = new HashMap<>();
            post.put(Constants.DIALOGS + "/" + userId +
                    "/" + friendID + "/" + pushId, msgToUser);
            post.put(Constants.DIALOGS + "/" + friendID +
                    "/" + userId + "/" + pushId, msgToFriend);

            post.put(Constants.RECENT_MESSAGE + "/" + userId + "/" + friendID, recentMsgToUser);
            post.put(Constants.RECENT_MESSAGE + "/" + friendID + "/" + userId, recentMsgToFriend);

            DatabaseReference db = dbRef.getRoot();

            db.updateChildren(post);

            newMessageEditText.setText("");

        } else {
            newMessageEditText.setText("");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_messages) {
            deleteMessages();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMessages() {
        DatabaseReference root = dbRef.getRoot();
        Map<String, Object> deleteAllMessages = new HashMap<>();

        deleteAllMessages.put(Constants.RECENT_MESSAGE + "/" +
                userId + "/" + friendID, null);
        deleteAllMessages.put(Constants.DIALOGS + "/" + userId +
                "/" + friendID, null);

//        deleteAllMessages.put(Constants.RECENT_MESSAGE + "/" +
//                friendID + "/" + userId, null);
//        deleteAllMessages.put(Constants.DIALOGS + "/" + friendID +
//                "/" + userId, null);

        root.updateChildren(deleteAllMessages);

        Toast.makeText(getContext(), R.string.all_messages_was_deleted, Toast.LENGTH_SHORT).show();
    }
}
