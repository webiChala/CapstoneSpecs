package com.example.myguide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.Utils.SnackBarUtil;
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
    SnackBarUtil snackBarUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginBinding.getRoot();
        setContentView(view);
        snackBarUtil = new SnackBarUtil(this, loginBinding.LoginActivity);



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
                if (password == null || username == null || password.equals("") || username.equals("")) {
                    snackBarUtil.setSnackBar("Please fill all the fields!");
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
                loginBinding.progressBar2.setVisibility(View.VISIBLE);
                loginBinding.ibLinkedinSignin.setClickable(false);
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
        loggedinUser.continueWith(new Continuation<ParseUser, Void>() {
            public Void then(Task task) throws Exception {
                if (task.isCancelled()) {
                    snackBarUtil.setSnackBar("Error logging in!");
                    loginBinding.ibLinkedinSignin.setClickable(true);
                    loginBinding.progressBar2.setVisibility(View.GONE);
                } else if (task.isFaulted()) {
                    snackBarUtil.setSnackBar("Error logging in!");
                    loginBinding.ibLinkedinSignin.setClickable(true);
                    loginBinding.progressBar2.setVisibility(View.GONE);

                } else {
                    ParseUser user = (ParseUser) task.getResult();
                    User loggedUser = (User) user;

                    if (loggedUser.isTutor() == false && loggedUser.isStudent() == false) {
                        goToRegistration(loggedUser);
                        return null;
                    }

                    if ((loggedUser.isTutor() != true && isTutor == true) || (loggedUser.isStudent() != true && isTutor != true ) ) {
                        snackBarUtil.setSnackBar("Error logging in!");
                        loginBinding.ibLinkedinSignin.setClickable(true);
                        loginBinding.progressBar2.setVisibility(View.GONE);
                        return null;
                    } else {
                        if (loggedUser.isNew()) {
                            goToRegistration(loggedUser);
                        } else {
                            goToHomeActivity(loggedUser);
                        }
                    }
                }
                return null;
            }
        });
    }


    private void LoginUser(String username, String password) {
        loginBinding.btnLogIn.setBackgroundColor(Color.DKGRAY);
        loginBinding.btnLogIn.setClickable(false);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e!= null) {
                    //loginBinding.tvError.setVisibility(View.VISIBLE);
                    snackBarUtil.setSnackBar("Error logging in");
                    loginBinding.btnLogIn.setBackgroundColor(getResources().getColor(R.color.Mint_green));
                    loginBinding.btnLogIn.setClickable(true);
                    return;
                }

                User loggedUser = (User) user;

                if ((loggedUser.isTutor() == false && isTutor) || (loggedUser.isStudent() == false && !isTutor)) {
                    snackBarUtil.setSnackBar("Error logging in");
                    loginBinding.btnLogIn.setBackgroundColor(getResources().getColor(R.color.Mint_green));
                    loginBinding.btnLogIn.setClickable(true);
                    ParseUser.logOut();
                    //loginBinding.tvError.setVisibility(View.VISIBLE);
                    return;
                }
                changeLogInStatus(loggedUser);
                if (loggedUser.isNew() == true) {
                    goToRegistration(loggedUser);
                } else {
                    goToHomeActivity(loggedUser);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                LinkedInUser user = data.getParcelableExtra("social_login");
                loginWithLinkedin(user.getAccessToken(), user.getId());
            } else {

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {
                }
                snackBarUtil.setSnackBar("Error logging in!");
                loginBinding.ibLinkedinSignin.setClickable(true);
                loginBinding.progressBar2.setVisibility(View.GONE);
            }
        } else {
            snackBarUtil.setSnackBar("Error logging in!");
            loginBinding.ibLinkedinSignin.setClickable(true);
            loginBinding.progressBar2.setVisibility(View.GONE);
        }

    }

    private void changeLogInStatus(User currentUser) {
        if (isTutor == true) {
            currentUser.setKeyLoggedastutor(true);
            currentUser.setKeyIstutor(true);
            currentUser.saveInBackground();

        } else{
            currentUser.setKeyLoggedastutor(false);
            currentUser.setKeyIsstudent(true);
            currentUser.saveInBackground();
        }

    }

    private void goToRegistration(User loggedUser) {
        changeLogInStatus(loggedUser);
        if (isTutor) {

            Intent i = new Intent(LoginActivity.this, TutorSetupActivity.class);
            startActivity(i);
            finish();
        } else{
            Intent i = new Intent(LoginActivity.this, StudentSetupActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void goToHomeActivity(User loggedUser) {
        changeLogInStatus(loggedUser);
        if (isTutor) {
            Intent i = new Intent(LoginActivity.this, TutorHomeActivity.class);
            startActivity(i);
            finish();
        } else{
            Intent i = new Intent(LoginActivity.this, StudentHomeActivity.class);
            startActivity(i);
            finish();
        }
    }

}