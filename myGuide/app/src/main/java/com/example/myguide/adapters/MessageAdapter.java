package com.example.myguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.databinding.ItemMessageBinding;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.example.myguide.Utils.GetRelativeTime;
import com.example.myguide.ui.ChatActivity;
import com.parse.ParseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemMessageBinding itemMessageBinding;
        GetRelativeTime getRelativeTime = new GetRelativeTime();

        public ViewHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            itemMessageBinding = binding;
            itemMessageBinding.getRoot().setOnClickListener(this);
        }

        public void bind(Message message) {

            if (message.getSender().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                itemMessageBinding.tvMessageSenderName.setText(message.getReceiver().getName());
                if (message.getReceiver().getImage() != null) {
                    Glide.with(context).load(message.getReceiver().getImage().getUrl()).circleCrop().into(itemMessageBinding.ivMessageSender);
                }
            } else {
                itemMessageBinding.tvMessageSenderName.setText(message.getSender().getName());
                if (message.getSender().getImage() != null) {
                    Glide.with(context).load(message.getSender().getImage().getUrl()).circleCrop().into(itemMessageBinding.ivMessageSender);
                }
            }



            itemMessageBinding.tvMessageSent.setText((message.getMessage()));
            String dateSent = getRelativeTime.getRelativeTimeAgo(message.getCreatedAt().toString());
            itemMessageBinding.tvMessageDateSent.setText(dateSent);

            if (!message.isRead()) {
                Log.i("MessageAdapter", "bind: " + message.isRead());
                //itemMessageBinding.tvMessageSent.setTypeface(null, Typeface.BOLD);
            }

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, ChatActivity.class);
            User otherUser;
            String userId = ParseUser.getCurrentUser().getObjectId().toString();
            Message m = messages.get(getAdapterPosition());
            String messageSenderId = m.getSender().getObjectId().toString();

            if (userId.equals(messageSenderId))
            {
                otherUser = m.getReceiver();
            } else {
                otherUser = m.getSender();
            }
            i.putExtra("otherUser", otherUser);
            context.startActivity(i);

        }
    }
}
