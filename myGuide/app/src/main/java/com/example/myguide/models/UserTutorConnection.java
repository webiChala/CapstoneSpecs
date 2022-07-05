package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserTutorConnection")
public class UserTutorConnection extends ParseObject {
    public static final String KEY_STUDENT = "Student";
    public static final String KEY_TUTOR = "Tutor";
    public static final String KEY_MESSAGE = "Message";
    public static final String KEY_ACCEPTED = "Accepted";

    public UserTutorConnection() {
    }

    public User getStudent() {
        return (User) getParseUser(KEY_STUDENT);
    }
    public void setStudent(User student) {
        put(KEY_STUDENT, student);
    }

    public User getTutor() {
        return (User) getParseUser(KEY_TUTOR);
    }
    public void setTutor(User tutor) {
        put(KEY_TUTOR, tutor);
    }

    public boolean hasAccepted() {
        return getBoolean(KEY_ACCEPTED);
    }
    public void setHasAccepted(boolean hasaccepted) {
        put(KEY_ACCEPTED, hasaccepted);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }
    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }
}
