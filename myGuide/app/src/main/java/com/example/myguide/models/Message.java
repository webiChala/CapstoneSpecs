package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String KEY_MESSAGE = "Message";
    public static final String KEY_SENDER = "Sender";
    public static final String KEY_RECEIVER = "Receiver";
    public static final String KEY_IS_FOR_TUTOR = "IsForTutor";

    public Message() {

    }

    public User getSender() {
        return (User) getParseUser(KEY_SENDER);
    }
    public void setSender(User sender) {
        put(KEY_SENDER, sender);
    }

    public User getReceiver() {
        return (User) getParseUser(KEY_RECEIVER);
    }
    public void setReceiver(User receiver) {
        put(KEY_RECEIVER, receiver);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }
    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public boolean isForTutor () {
        return getBoolean(KEY_IS_FOR_TUTOR);
    }
    public void setIsForTutor(boolean isForTutor) {
        put(KEY_IS_FOR_TUTOR, isForTutor);
    }


}
