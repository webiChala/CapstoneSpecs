package com.example.myguide.interfaces;

import com.example.myguide.models.Education;
import com.example.myguide.models.UserTutorConnection;
import com.parse.ParseException;

import java.util.List;

public interface UserTutorConnectionInterface {
    void getProcessFinish(List<UserTutorConnection> output);
    void postProcessFinish(ParseException e);
}
