package com.example.myguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.databinding.ActivityGeneralSignupBinding;
import com.example.myguide.models.User;
import com.example.myguide.ui.StudentSetupActivity;
import com.example.myguide.ui.TutorSetupActivity;
import com.parse.ParseException;
import com.parse.SignUpCallback;

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

        generalSignupBinding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = generalSignupBinding.email.getText().toString();
                String name = generalSignupBinding.name.getText().toString();
                String username = generalSignupBinding.username.getText().toString();
                String pwd = generalSignupBinding.pwdSignUp.getText().toString();

                if (pwd == null || username == null || pwd == "" || username == "" || email == "" || email == null || name == "" || name == null) {
                    generalSignupBinding.tvErrorSignUp.setVisibility(View.VISIBLE);
                    return;
                }

                SignUp(username, pwd, email, name);
            }
        });

    }

    private void SignUp(String username, String pwd, String email, String name ) {

        // Create the ParseUser
        User user = new User();
        // Set core properties
        user.setUsername(username);
        user.setPassword(pwd);
        user.setName(name);
        user.setEmail(email);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    if (isTutor) {
                        Intent gotoregister = new Intent(GeneralSignupActivity.this, TutorSetupActivity.class);
                        startActivity(gotoregister);
                        finish();
                    } else {
                        Intent gotoregister = new Intent(GeneralSignupActivity.this, StudentSetupActivity.class);
                        startActivity(gotoregister);
                        finish();
                    }
                } else {
                    generalSignupBinding.tvErrorSignUp.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}