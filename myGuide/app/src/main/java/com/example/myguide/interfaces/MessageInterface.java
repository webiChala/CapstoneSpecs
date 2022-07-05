package com.example.myguide.interfaces;

import com.example.myguide.models.Message;
import com.example.myguide.models.UserTutorConnection;
import com.parse.ParseException;

import java.util.List;

public interface MessageInterface {

    void getProcessFinish(List<Message> output);
    void postProcessFinish(ParseException e);

}
