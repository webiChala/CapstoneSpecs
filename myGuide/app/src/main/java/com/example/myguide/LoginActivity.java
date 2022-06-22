package com.example.myguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.databinding.ActivityLoginBinding;
import com.example.myguide.ui.StudentHomeActivity;
import com.example.myguide.ui.TutorHomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.boltsinternal.Continuation;
import com.parse.boltsinternal.Task;
import com.shantanudeshmukh.linkedinsdk.LinkedInBuilder;
import com.shantanudeshmukh.linkedinsdk.helpers.LinkedInUser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding loginBinding;
    public static final String TAG = "LoginActivity";
    boolean isTutor;
    private String authorization = "Authorization: Bearer ";
    private Integer REQUEST_CODE = 1337;
    private String REDIRECT_URI = "https://myguide.com";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginBinding.getRoot();
        setContentView(view);



        isTutor =  getIntent().getExtras().getBoolean("isTutor");

        loginBinding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginBinding.username.getText().toString();
                String password = loginBinding.pwd.getText().toString();
                LoginUser(username, password);

            }
        });

        loginBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, GeneralSignupActivity.class);
                i.putExtra("isTutor", isTutor);
                startActivity(i);
            }
        });

        loginBinding.btnLinkedinSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedInBuilder.getInstance(LoginActivity.this)
                        .setClientID(getString(R.string.linkedin_client_id))
                        .setClientSecret(getString(R.string.linkedin_client_secret))
                        .setRedirectURI(REDIRECT_URI)
                        .authenticate(REQUEST_CODE);
                //WebStorage.getInstance().deleteAllData();
//                CookieManager.getInstance().removeAllCookies(null);
//                CookieManager.getInstance().flush();

            }
        });
    }

    private void loginWithLinkedin(String tokenString, String user ) {
        Map<String, String> authData = new HashMap<String, String>();
        authData.put("access_token", tokenString);
        authData.put("id", user);
        //authData.put("is_mobile_sdk", String.valueOf(true));
        Task<ParseUser> loggedinUser = ParseUser.logInWithInBackground("linkedin", authData);
        Log.i(TAG, String.valueOf(loggedinUser));
        loggedinUser.continueWith(new Continuation<ParseUser, Void>() {
            public Void then(Task task) throws Exception {
                if (task.isCancelled()) {
                    Log.w(TAG, "Task cancelled");
                } else if (task.isFaulted()) {
                    Log.w(TAG, "Save FAIL" + task.getError());
                    //Utilities.showToast(getResources().getString(R.string.errorLogin) + task.getError(), MainActivity.this);
                } else {
                    // the object was saved successfully.
                    ParseUser user = (ParseUser) task.getResult();
                    goToHomeActivity(isTutor);
//                    if ((!user.isTutor && isTutor) || (!user.isStudent && !isTutor)) {
//                        loginBinding.tvError.setVisibility(View.VISIBLE);
//                         return null;
//                     }
                    goToHomeActivity(isTutor);
                    Log.w(TAG, "Success " + user.getObjectId() + " " + user.getUsername() + " " + user.getEmail() + " " + user.getSessionToken());
                }
                return null;
            }
        });
    }

    private void LoginUser(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e!= null) {
                    loginBinding.tvError.setVisibility(View.VISIBLE);
                    return;
                }

//                if ((!user.isTutor && isTutor) || (!user.isStudent && !isTutor)) {
//                    loginBinding.tvError.setVisibility(View.VISIBLE);
//                    return;
//                }
                goToHomeActivity(isTutor);
                Toast.makeText(LoginActivity.this, "You have successfully loggedin!", Toast.LENGTH_SHORT).show();

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

                //acessing user info
                Log.i(TAG, user.getFirstName());
                Log.i(TAG, user.getAccessToken());
                Log.i(TAG, user.getProfileUrl());
                loginWithLinkedin(user.getAccessToken(), user.getId());


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


    private void goToHomeActivity(boolean isTutor) {
        if (isTutor) {
            //go to tutor home activity
            Intent i = new Intent(LoginActivity.this, TutorHomeActivity.class);
            startActivity(i);
            finish();
        } else{
            //go to student home activity
            Intent i = new Intent(LoginActivity.this, StudentHomeActivity.class);
            startActivity(i);
            finish();
        }
    }

}