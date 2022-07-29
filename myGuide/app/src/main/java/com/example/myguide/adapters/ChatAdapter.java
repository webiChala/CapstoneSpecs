package com.example.myguide.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.databinding.ItemCourseBinding;
import com.example.myguide.databinding.MessageIncomingBinding;
import com.example.myguide.databinding.MessageOutgoingBinding;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private String mUserId;
    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            MessageIncomingBinding binding = MessageIncomingBinding.inflate(LayoutInflater.from(context), parent, false);
            return new IncomingMessageViewHolder(binding);
        } else if (viewType == MESSAGE_OUTGOING) {
            MessageOutgoingBinding binding = MessageOutgoingBinding.inflate(LayoutInflater.from(context), parent, false);
            return new OutgoingMessageViewHolder(binding);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }



    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getSender().getObjectId() != null && message.getSender().getObjectId().equals(mUserId);
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        MessageIncomingBinding messageIncomingBinding;

        public IncomingMessageViewHolder(MessageIncomingBinding binding) {
            super(binding.getRoot());

            messageIncomingBinding = binding;
        }

        @Override
        public void bindMessage(Message message) {

            messageIncomingBinding.tvBody.setText(message.getMessage());
            try{
                User messageSender = (User) message.getSender().fetchIfNeeded();
                //messageIncomingBinding.tvName.setText(messageSender.getName());
//                if (messageSender.getImage() != null) {
//                    Glide.with(mContext).load(messageSender.getImage().getUrl()).circleCrop().into(messageIncomingBinding.ivProfileOther);
//                }

            } catch (ParseException e) {
            }
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        MessageOutgoingBinding messageOutgoingBinding;

        public OutgoingMessageViewHolder(MessageOutgoingBinding binding) {
            super(binding.getRoot());
            messageOutgoingBinding = binding;
        }

        @Override
        public void bindMessage(Message message) {
            messageOutgoingBinding.tvBody.setText(message.getMessage());
            User currentUser = (User) ParseUser.getCurrentUser();
//            if(currentUser.getImage() != null) {
//                Glide.with(mContext).load(currentUser.getImage().getUrl()).circleCrop().into(messageOutgoingBinding.ivProfileMe);
//            }
        }
    }


}
