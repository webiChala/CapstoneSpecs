package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_USERIMAGE = "userImage";
    public static final String KEY_ISTUTOR = "isTutor";
    public static final String KEY_ISSTUDENT = "isStudent";
    public static final String KEY_LOGGEDASTUTOR = "loggedAsTutor";
    public static final String KEY_ISNEW = "isNew";
    public static final String KEY_NAME = "name";



    public boolean isTutor() {
        return getBoolean(KEY_ISTUTOR);
    }

    public void setKeyIstutor(boolean istutor) {
        put(KEY_ISTUTOR, istutor);
    }

    public boolean isLoggedAsTutor() {
        return getBoolean(KEY_LOGGEDASTUTOR);
    }

    public void setKeyLoggedastutor(boolean isLoggedAsTutor) { put(KEY_LOGGEDASTUTOR, isLoggedAsTutor);}

    public boolean isStudent() {
        return getBoolean(KEY_ISSTUDENT);
    }

    public void setKeyIsstudent(boolean isStudent) {
        put(KEY_ISSTUDENT, isStudent);
    }

    public boolean isNew() {
        return getBoolean(KEY_ISNEW);
    }

    public void setKeyIsnew(boolean isNew) { put(KEY_ISNEW, isNew);}

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) { put(KEY_NAME, name);}

    public User(){

    }

}
