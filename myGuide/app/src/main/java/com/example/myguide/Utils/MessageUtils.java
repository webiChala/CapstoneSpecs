package com.example.myguide.Utils;

import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class MessageUtils {
    public static final String TAG = "MessageUtils";
    public MessageInterface delegate = null;
    public MessageUtils(MessageInterface asyncResponse) {
        delegate = asyncResponse;
    }

    public void getMessage(ParseQuery<Message> messageParseQuery) {

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);

        if (messageParseQuery != null) {
            query = messageParseQuery;
        }
        query.include(Message.KEY_MESSAGE);
        query.include(Message.KEY_RECEIVER);
        query.include(Message.KEY_SENDER);
        query.include(Message.KEY_CREATED_AT);
        query.orderByDescending(Message.KEY_CREATED_AT);


        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e==null) {
                    delegate.getProcessFinish(messages);
                } else {
                    delegate.getProcessFinish(null);
                }
            }
        });

    }

    public void sendMessage(Message message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                delegate.postProcessFinish(e);
            }
        });
    }

}


