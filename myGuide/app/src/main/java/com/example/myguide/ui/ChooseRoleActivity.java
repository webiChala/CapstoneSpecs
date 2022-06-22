package com.example.myguide.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.LoginActivity;
import com.example.myguide.databinding.ActivityChooseroleBinding;
import com.parse.ParseUser;

public class ChooseRoleActivity extends AppCompatActivity {

    ActivityChooseroleBinding chooseRoleBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chooseRoleBinding = ActivityChooseroleBinding.inflate(getLayoutInflater());
        View view = chooseRoleBinding.getRoot();
        setContentView(view);

        if (ParseUser.getCurrentUser() != null) {
            //if (tutor) { got to home tutor} else {home student}
            Intent i = new Intent(ChooseRoleActivity.this, StudentHomeActivity.class);
            startActivity(i);
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        chooseRoleBinding.ibOptionStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseRoleActivity.this, LoginActivity.class);
                i.putExtra("isTutor", false);
                startActivity(i);
            }
        });

        chooseRoleBinding.ibOptionTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseRoleActivity.this, LoginActivity.class);
                i.putExtra("isTutor", true);
                startActivity(i);
            }
        });
    }
}