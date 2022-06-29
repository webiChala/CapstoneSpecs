package com.example.myguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        generalSignupBinding.btnLinkedinSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                //Successfully signed in
                LinkedInUser user = data.getParcelableExtra("social_login");
                signUpWithLinkedin(user.getAccessToken(), user.getId());


            } else {

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //Handle : user denied access to account

                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {

                    //Handle : Error in API : see logcat output for details
                    Log.e("LINKEDIN ERROR", data.getStringExtra("err_message"));
                }
            }
        }

    }

    private void signUpWithLinkedin(String tokenString, String user ) {
        Map<String, String> authData = new HashMap<String, String>();
        authData.put("access_token", tokenString);
        authData.put("id", user);
        Task<ParseUser> loggedinUser = ParseUser.logInWithInBackground("linkedin", authData);
        Log.i(TAG, String.valueOf(loggedinUser));
        loggedinUser.continueWith(new Continuation<ParseUser, Void>() {
            public Void then(Task task) throws Exception {
                if (task.isCancelled()) {
                    Log.w(TAG, "Task cancelled");
                } else if (task.isFaulted()) {
                    Log.w(TAG, "Save FAIL" + task.getError());
                } else {
                    // the object was saved successfully.
                    ParseUser user = (ParseUser) task.getResult();
                    User loggedUser = (User) user;

                    if (isTutor) {
                        Intent i = new Intent(GeneralSignupActivity.this, TutorSetupActivity.class);
                        startActivity(i);
                        loggedUser.setKeyIstutor(true);
                        loggedUser.setKeyLoggedastutor(true);
                        loggedUser.saveInBackground();
                    } else {
                        Intent i = new Intent(GeneralSignupActivity.this, StudentSetupActivity.class);
                        startActivity(i);
                        loggedUser.setKeyIsstudent(true);
                        loggedUser.setKeyLoggedastutor(false);
                        loggedUser.saveInBackground();
                    }



                }
                return null;
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
                    Log.e(
                            TAG, "Signup error:", e
                    );
                    generalSignupBinding.tvErrorSignUp.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}