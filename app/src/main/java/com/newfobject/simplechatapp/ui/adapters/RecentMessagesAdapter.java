package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.newfobject.simplechatapp.Constants;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.Utils;
import com.newfobject.simplechatapp.model.RecentMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentMessagesAdapter extends FirebaseRecyclerAdapter {
    onItemClickListener listener;

    public RecentMessagesAdapter(Query ref, onItemClickListener listener) {
        super(RecentMessage.class, R.layout.recent_message_adapter_item, ViewHolder.class, ref);
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, final Object model, int position) {
        RecentMessage recentMessage = (RecentMessage) model;
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.messageText.setText(recentMessage.getText());
        long timestamp = recentMessage.getTimeStampLong();
        holder.timeStamp.setText(Utils.getDateFromMillis(timestamp));
        holder.name.setText(recentMessage.getName());
        final String key = getRef(position).getKey();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAdapterItemClick(key);

            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProfileImageClick(key);
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_text)
        TextView messageText;
        @BindView(R.id.time_stamp)
        TextView timeStamp;
        @BindView(R.id.friend_profile_image)
        CircleImageView profileImage;
        @BindView(R.id.friend_name)
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface onItemClickListener {
        void onAdapterItemClick(String ref);

        void onProfileImageClick(String friendId);
    }
}
