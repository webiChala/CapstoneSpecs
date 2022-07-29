package com.example.myguide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.Utils.SnackBarUtil;
import com.example.myguide.databinding.ActivityGeneralSignupBinding;
import com.example.myguide.databinding.ActivityLoginBinding;
import com.example.myguide.models.User;
import com.example.myguide.ui.StudentSetupActivity;
import com.example.myguide.ui.TutorSetupActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;
import com.shantanudeshmukh.linkedinsdk.LinkedInBuilder;
import com.shantanudeshmukh.linkedinsdk.helpers.LinkedInUser;

import java.util.HashMap;
import java.util.Map;

public class GeneralSignupActivity extends AppCompatActivity {

    ActivityGeneralSignupBinding generalSignupBinding;
    public static final String TAG = "GeneralSignupActivity";
    boolean isTutor;
    private Integer REQUEST_CODE = 1337;
    private String REDIRECT_URI = "https://myguide.com";
    SnackBarUtil snackBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generalSignupBinding = ActivityGeneralSignupBinding.inflate(getLayoutInflater());
        View view = generalSignupBinding.getRoot();
        setContentView(view);

        isTutor =  getIntent().getExtras().getBoolean("isTutor");
        snackBarUtil = new SnackBarUtil(this, generalSignupBinding.GeneralSignupActivity);

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

        generalSignupBinding.btnLinkedinSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalSignupBinding.btnLinkedinSignup.setBackgroundColor(Color.DKGRAY);
                generalSignupBinding.btnLinkedinSignup.setClickable(false);


                LinkedInBuilder.getInstance(GeneralSignupActivity.this)
                        .setClientID(getString(R.string.linkedin_client_id))
                        .setClientSecret(getString(R.string.linkedin_client_secret))
                        .setRedirectURI(REDIRECT_URI)
                        .authenticate(REQUEST_CODE);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                LinkedInUser user = data.getParcelableExtra("social_login");
                signUpWithLinkedin(user.getAccessToken(), user.getId());


            } else {

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {
                }
            }
        }

    }

    void checkIfLinkedinSignupSuccess(boolean wasSuccessful) {
        if (!wasSuccessful) {
            snackBarUtil.setSnackBar("Account already exists!");
        }
        generalSignupBinding.btnLinkedinSignup.setBackgroundColor(getResources().getColor(R.color.blue));
        generalSignupBinding.btnLinkedinSignup.setClickable(true);

    }

    private void signUpWithLinkedin(String tokenString, String user ) {
        Map<String, String> authData = new HashMap<String, String>();
        authData.put("access_token", tokenString);
        authData.put("id", user);
        Task<ParseUser> loggedinUser = ParseUser.logInWithInBackground("linkedin", authData);
        loggedinUser.continueWith(new Continuation<ParseUser, Void>() {
            public Void then(Task task) throws Exception {
                if (task.isCancelled()) {
                    checkIfLinkedinSignupSuccess(false);
                } else if (task.isFaulted()) {
                    checkIfLinkedinSignupSuccess(false);
                } else {
                    ParseUser user = (ParseUser) task.getResult();
                    User loggedUser = (User) user;

                    if (isTutor) {
                        if (loggedUser.isNew()) {
                            Intent i = new Intent(GeneralSignupActivity.this, TutorSetupActivity.class);
                            startActivity(i);
                            loggedUser.setKeyIstutor(true);
                            loggedUser.setKeyLoggedastutor(true);
                            loggedUser.saveInBackground();

                        } else {
                            checkIfLinkedinSignupSuccess(false);

                        }

                    } else {
                        if (loggedUser.isNew()) {
                            Intent i = new Intent(GeneralSignupActivity.this, StudentSetupActivity.class);
                            startActivity(i);
                            loggedUser.setKeyIsstudent(true);
                            loggedUser.setKeyLoggedastutor(false);
                            loggedUser.saveInBackground();
                        } else {
                            checkIfLinkedinSignupSuccess(false);
                        }

                    }





                }
                return null;
            }
        });
    }

    private void SignUp(String username, String pwd, String email, String name ) {

        generalSignupBinding.btnSignUp.setBackgroundColor(Color.DKGRAY);
        generalSignupBinding.btnSignUp.setClickable(false);

        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);
        user.setName(name);
        user.setEmail(email);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    if (isTutor) {
                        user.setKeyIstutor(true);
                        user.setKeyLoggedastutor(true);
                        user.saveInBackground();
                        Intent gotoregister = new Intent(GeneralSignupActivity.this, TutorSetupActivity.class);
                        startActivity(gotoregister);
                        finishAffinity();
                    } else {
                        user.setKeyIsstudent(true);
                        user.setKeyLoggedastutor(false);
                        user.saveInBackground();
                        Intent gotoregister = new Intent(GeneralSignupActivity.this, StudentSetupActivity.class);
                        startActivity(gotoregister);
                        finishAffinity();
                    }
                } else {
                    snackBarUtil.setSnackBar("Error signing up!");
                    //generalSignupBinding.tvErrorSignUp.setVisibility(View.VISIBLE);
                    generalSignupBinding.btnSignUp.setBackgroundColor(getResources().getColor(R.color.Mint_green));
                    generalSignupBinding.btnSignUp.setClickable(true);
                }
            }
        });
    }


}