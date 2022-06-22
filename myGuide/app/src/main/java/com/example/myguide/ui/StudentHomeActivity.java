package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.myguide.databinding.ActivitySplashBinding;
import com.example.myguide.databinding.ActivityStudentHomeBinding;

public class StudentHomeActivity extends AppCompatActivity {

    ActivityStudentHomeBinding studentHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentHomeBinding = ActivityStudentHomeBinding.inflate(getLayoutInflater());
        View view = studentHomeBinding.getRoot();
        setContentView(view);
    }
}