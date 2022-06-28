package com.example.myguide.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.LoginActivity;
import com.example.myguide.databinding.ActivityChooseroleBinding;
import com.example.myguide.models.User;
import com.parse.ParseUser;

public class ChooseRoleActivity extends AppCompatActivity {

    ActivityChooseroleBinding chooseRoleBinding;
    public static final String TAG = "ChooseRoleActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chooseRoleBinding = ActivityChooseroleBinding.inflate(getLayoutInflater());
        View view = chooseRoleBinding.getRoot();
        setContentView(view);
        ParseUser.logOut();

        if (ParseUser.getCurrentUser() != null) {

            User currentUser = (User) ParseUser.getCurrentUser();

            if (currentUser.isLoggedAsTutor())
            {
                if (currentUser.isNew()) {
                    Intent gotoregister = new Intent(ChooseRoleActivity.this, TutorSetupActivity.class);
                    startActivity(gotoregister);
                    finish();

                } else {
                    Intent i = new Intent(ChooseRoleActivity.this, TutorHomeActivity.class);
                    startActivity(i);
                    finish();
                }


            } else
            {
                if (currentUser.isNew()) {
                    Intent gotoregister = new Intent(ChooseRoleActivity.this, StudentSetupActivity.class);
                    startActivity(gotoregister);
                    finish();

                } else {
                    Intent i = new Intent(ChooseRoleActivity.this, StudentHomeActivity.class);
                    startActivity(i);
                    finish();
                }


            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        chooseRoleBinding.cardviewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseRoleActivity.this, LoginActivity.class);
                i.putExtra("isTutor", false);
                startActivity(i);
            }
        });

        chooseRoleBinding.cardViewTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseRoleActivity.this, LoginActivity.class);
                i.putExtra("isTutor", true);
                startActivity(i);
            }
        });
    }
}