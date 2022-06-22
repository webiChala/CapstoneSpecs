package com.example.myguide;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.databinding.ActivityGeneralSignupBinding;

public class GeneralSignupActivity extends AppCompatActivity {

    ActivityGeneralSignupBinding generalSignupBinding;
    public static final String TAG = "GeneralSignupActivity";
    boolean isTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generalSignupBinding = ActivityGeneralSignupBinding.inflate(getLayoutInflater());
        View view = generalSignupBinding.getRoot();
        setContentView(view);

        isTutor =  getIntent().getExtras().getBoolean("isTutor");

    }
}