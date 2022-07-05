package com.example.myguide.services;

import android.util.Log;

import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.UserTutorConnection;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class UserTutorConnectionServices {

    public UserTutorConnectionInterface delegate = null;
    public static final String TAG = "UserTutorConnectionServices";

    public UserTutorConnectionServices(UserTutorConnectionInterface asyncResponse) {
        delegate = asyncResponse;
    }

    public void getUserTutorConnections(ParseQuery<UserTutorConnection> userTutorConnectionParseQuery) {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        if (userTutorConnectionParseQuery != null) {
            query = userTutorConnectionParseQuery;
        }
        query.include(UserTutorConnection.KEY_MESSAGE);
        query.include(UserTutorConnection.KEY_TUTOR);
        query.include(UserTutorConnection.KEY_STUDENT);
        query.include(UserTutorConnection.KEY_ACCEPTED);
        query.findInBackground(new FindCallback<UserTutorConnection>() {
            @Override
            public void done(List<UserTutorConnection> connections, ParseException e) {
                if (e != null) {
                    return;
                }
                delegate.getProcessFinish(connections);
            }
        });
    }

    public void sendUserTutorConnection(UserTutorConnection userTutorConnection) {

        userTutorConnection.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {} else {delegate.postProcessFinish(e);}
            }
        });
    }
}
