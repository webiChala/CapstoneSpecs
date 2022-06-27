package com.example.myguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.databinding.ActivityLoginBinding;
import com.example.myguide.models.User;
import com.example.myguide.ui.StudentHomeActivity;
import com.example.myguide.ui.StudentSetupActivity;
import com.example.myguide.ui.TutorHomeActivity;
import com.example.myguide.ui.TutorSetupActivity;
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

        loginBinding.ibGoBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBinding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginBinding.username.getText().toString();
                String password = loginBinding.pwd.getText().toString();
                if (password == null || username == null || password == "" || username == "") {
                    loginBinding.tvError.setVisibility(View.VISIBLE);
                    return;
                }
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

        loginBinding.ibLinkedinSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedInBuilder.getInstance(LoginActivity.this)
                        .setClientID(getString(R.string.linkedin_client_id))
                        .setClientSecret(getString(R.string.linkedin_client_secret))
                        .setRedirectURI(REDIRECT_URI)
                        .authenticate(REQUEST_CODE);

            }
        });
    }

    private void loginWithLinkedin(String tokenString, String user ) {
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
                    Log.i(TAG, "Wala");




                    if (loggedUser.isNew() == false) {

                        if ((loggedUser.isTutor() == false && isTutor) || (loggedUser.isStudent() == false && !isTutor)) {
                            Toast.makeText(LoginActivity.this, "Please signin with different role.", Toast.LENGTH_SHORT).show();
                            loginBinding.tvError.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Not successs");
                            return null;
                        }
                    }

                    changeLogInStatus(loggedUser);
                    if (loggedUser.isNew()) {
                        if (isTutor) {
                            loggedUser.setKeyIstutor(true);
                            Intent gotoregister = new Intent(LoginActivity.this, TutorSetupActivity.class);
                            startActivity(gotoregister);
                            finish();
                        } else {
                            loggedUser.setKeyIsstudent(true);

                            Intent gotoregister = new Intent(LoginActivity.this, StudentSetupActivity.class);
                            startActivity(gotoregister);
                            finish();
                        }

                    } else {
                        goToHomeActivity(isTutor, loggedUser);

                    }



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

                User loggedUser = (User) user;

                if ((loggedUser.isTutor() == false && isTutor) || (loggedUser.isStudent() == false && !isTutor)) {
                    Toast.makeText(LoginActivity.this, "Please signin with different role.", Toast.LENGTH_SHORT).show();
                    loginBinding.tvError.setVisibility(View.VISIBLE);
                    return;
                }
                changeLogInStatus(loggedUser);
                goToHomeActivity(isTutor, loggedUser);
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

    private void changeLogInStatus(User currentUser) {
        if (isTutor) {
            //go to tutor home activity
            currentUser.setKeyLoggedastutor(true);

            currentUser.saveInBackground();

        } else{
            //go to student home activity
            currentUser.setKeyLoggedastutor(false);
            currentUser.saveInBackground();

        }

    }

    private void goToHomeActivity(boolean isTutor, User currentUser) {
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