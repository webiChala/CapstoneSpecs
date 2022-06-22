package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.myguide.databinding.ActivitySplashBinding;
import com.example.myguide.databinding.ActivityTutorHomeBinding;

public class TutorHomeActivity extends AppCompatActivity {

    ActivityTutorHomeBinding tutorHomeBinding;
    public static final String TAG = "TutorHomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorHomeBinding = ActivityTutorHomeBinding.inflate(getLayoutInflater());
        View view = tutorHomeBinding.getRoot();
        setContentView(view);
    }
}