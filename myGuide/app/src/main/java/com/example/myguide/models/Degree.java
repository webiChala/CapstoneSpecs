package com.example.myguide.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ParseClassName("Degree")
public class Degree extends ParseObject{

    public static final String KEY_TITLE = "Title";


    public Degree() {

    }



    public String getTitle() {
        return getString(KEY_TITLE);
    }


}


