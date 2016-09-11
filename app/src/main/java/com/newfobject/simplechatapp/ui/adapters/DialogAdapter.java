package com.newfobject.simplechatapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.newfobject.simplechatapp.R;
import com.newfobject.simplechatapp.Utils;
import com.newfobject.simplechatapp.model.Message;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogAdapter extends FirebaseRecyclerAdapter {

    private static final int VIEW_TYPE_IN = 1;
    private static final int VIEW_TYPE_OUT = 2;


    public DialogAdapter(DatabaseReference ref) {
        super(Message.class, -1, ViewHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        String text = ((Message) model).getText();
        long time = ((Message) model).getTimestampLong();
        ((ViewHolder) viewHolder).messageText.setText(text);
        ((ViewHolder) viewHolder).date.setText(Utils.getDateFromMillis(time));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        ViewGroup viewGroup;

        switch (viewType) {
            case VIEW_TYPE_IN:
                viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_in_item, parent, false);
                viewHolder = new ViewHolder(viewGroup);
                return viewHolder;
            case VIEW_TYPE_OUT:
                viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_out_item, parent, false);
                viewHolder = new ViewHolder(viewGroup);
                return viewHolder;
            default:
                throw new RuntimeException("no such view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        // check if it is an incoming or outgoing message
        if (((Message) getItem(position)).isYour()) {
            return VIEW_TYPE_OUT;
        } else {
            return VIEW_TYPE_IN;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_message)
        TextView messageText;
        @BindView(R.id.date_message)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


