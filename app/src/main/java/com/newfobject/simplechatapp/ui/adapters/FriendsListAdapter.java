package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.Friend;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsListAdapter extends FirebaseRecyclerAdapter {
    private ClickListener listener;

    public FriendsListAdapter(DatabaseReference ref, ClickListener listener) {
        super(Friend.class, R.layout.friend_item, ViewHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, final int position) {
        ((ViewHolder) viewHolder).friendName.setText(((Friend) model).getName());
        final String friendId = getRef(position).getKey();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemFriendClick(friendId);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_image)
        CircleImageView profileImage;
        @BindView(R.id.friend_name)
        TextView friendName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ClickListener {
        void onItemFriendClick(String friendId);
    }
}
