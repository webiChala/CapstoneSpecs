package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.myguide.R;
import com.example.myguide.databinding.ActivityGeneralSignupBinding;
import com.example.myguide.databinding.ActivityStudentSetupBinding;

public class StudentSetupActivity extends AppCompatActivity {

    ActivityStudentSetupBinding studentSetupBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentSetupBinding = ActivityStudentSetupBinding.inflate(getLayoutInflater());
        View view = studentSetupBinding.getRoot();
        setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


    }
}