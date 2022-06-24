package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("FieldOfStudy")
public class FieldOfStudy extends ParseObject{
    public static final String KEY_TITLE = "title";

    public FieldOfStudy() {

    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }
}



