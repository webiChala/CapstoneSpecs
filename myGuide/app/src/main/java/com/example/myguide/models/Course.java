package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Course")
public class Course extends ParseObject {
    public static final String KEY_TITLE = "title";

    public Course(){

    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

}

