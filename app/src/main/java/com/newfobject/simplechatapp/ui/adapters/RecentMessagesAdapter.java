package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.model.RecentMessage;

public class RecentMessagesAdapter extends FirebaseRecyclerAdapter {

    public RecentMessagesAdapter(Class modelClass, int modelLayout, Query ref) {

        super(modelClass, modelLayout, ViewHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        String text = ((RecentMessage) model).getText();
        long timestamp = ((RecentMessage) model).getTimestamp();
        ((ViewHolder)viewHolder).messageText.setText(text);
        ((ViewHolder)viewHolder).timeStamp.setText(String.valueOf(timestamp));
    }



    private static class ViewHolder extends RecyclerView.ViewHolder {
         TextView messageText;
         TextView timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }
    }
}
