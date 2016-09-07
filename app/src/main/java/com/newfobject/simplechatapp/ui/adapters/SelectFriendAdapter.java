package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class SelectFriendAdapter extends FirebaseRecyclerAdapter {
    private FriendClickListener listener;

    public SelectFriendAdapter( DatabaseReference ref, FriendClickListener listener) {
        super(User.class, R.layout.select_friend_to_chat_item, ViewHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        ((ViewHolder) viewHolder).friendName.setText(((User) model).getName());

        final String friendId = getRef(position).getKey();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(friendId);
            }
        });
    }

    public interface FriendClickListener {
        void onClick(String friendId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_profile_image)
        CircleImageView friendImage;
        @BindView(R.id.friend_name)
        TextView friendName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
