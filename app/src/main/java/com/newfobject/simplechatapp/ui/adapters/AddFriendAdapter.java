package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.Friend;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class AddFriendAdapter extends FirebaseRecyclerAdapter {
//    private String userId;
    private OnProfileClick listener;

    public AddFriendAdapter(Query ref, String userId, OnProfileClick listener) {
        super(Friend.class, R.layout.add_friend_item, ViewHolder.class, ref);
//        this.userId = userId;
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.name.setText( ((Friend)model).getName());
        final String firebaseUid = getRef(position).getKey();


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(firebaseUid);
            }
        });
    }

//    private void addFriend(String friendId, Friend friend) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String userID = user.getUid();
//            // add friend to user in db
//            Friend addFriend = new Friend(friend.getName(), friend.getEmail());
//
//            HashMap<String, Object> addFriendMap =
//                    (HashMap<String, Object>) new ObjectMapper().convertValue(addFriend, Map.class);
//
//            // user to friend in db
//
//            Friend addUser = new Friend(user.getDisplayName(), user.getEmail());
//            HashMap<String, Object> addUserMap =
//                    (HashMap<String, Object>) new ObjectMapper().convertValue(addUser, Map.class);
//
//            Map<String, Object> update = new HashMap<>();
//            update.put(userID + "/" + friendId, addFriendMap);
//            update.put(friendId + "/" + userID, addUserMap);
//
//            DatabaseReference dbRef = FirebaseDatabase.getInstance()
//                    .getReference(Constants.FRIENDS);
//            dbRef.updateChildren(update);
//        }
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_image)
        CircleImageView profileImage;
        @BindView(R.id.user_name)
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnProfileClick {
        void onClick(String userId);
    }
}
