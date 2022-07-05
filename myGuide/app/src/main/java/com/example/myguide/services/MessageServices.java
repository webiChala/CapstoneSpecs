package com.example.myguide.services;

import android.util.Log;

import com.example.myguide.interfaces.CourseInterface;
import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.models.Course;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MessageServices {
    public static final String TAG = "MessageServices";
    public MessageInterface delegate = null;
    public MessageServices(MessageInterface asyncResponse) {
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
                    Log.i(TAG, "done: " + e.toString());
                    delegate.getProcessFinish(null);
                }
            }
        });

    }

    public void sendMessage(Message message) {

        if (((User) ParseUser.getCurrentUser()).isLoggedAsTutor() == true) {
            message.setIsForTutor(false);
        } else {
            message.setIsForTutor(true);
        }

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                delegate.postProcessFinish(e);
            }
        });
    }

}


