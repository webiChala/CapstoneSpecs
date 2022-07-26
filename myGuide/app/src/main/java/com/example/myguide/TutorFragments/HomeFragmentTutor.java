package com.example.myguide.TutorFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.Utils.SnackBarUtil;
import com.example.myguide.Utils.UserTutorConnectionUtils;
import com.example.myguide.adapters.HomeConnectedTutorsAdapter;
import com.example.myguide.databinding.FragmentHomeTutorBinding;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.ui.ChooseRoleActivity;
import com.example.myguide.ui.GetAllConnected;
import com.example.myguide.ui.StudentHomeActivity;
import com.example.myguide.ui.StudentSetupActivity;
import com.example.myguide.ui.TutorHomeActivity;
import com.example.myguide.ui.TutorSetupActivity;
import com.google.android.material.navigation.NavigationView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class HomeFragmentTutor extends Fragment {

    FragmentHomeTutorBinding binding;
    private HomeConnectedTutorsAdapter adapter;
    private List<UserTutorConnection> userTutorConnectionLists;
    User currentUser = (User) ParseUser.getCurrentUser();
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    SnackBarUtil snackBarUtil;
    AlertDialog.Builder builder;



    public HomeFragmentTutor() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userTutorConnectionLists = new ArrayList<>();


        adapter = new HomeConnectedTutorsAdapter(userTutorConnectionLists, getContext());
        binding.rvHomeStudents.setAdapter(adapter);
        binding.rvHomeStudents.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.tvSeeMoreHomeTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), GetAllConnected.class);
                startActivity(i);
            }
        });
        snackBarUtil = new SnackBarUtil(getContext(), binding.FragmentHomeTutor);

        builder = new AlertDialog.Builder(getContext());

        toolbar = binding.homeToolbarTutor;
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        configureLogoutDialog();



        // Find our drawer view
        mDrawer = binding.drawerLayout;
        nvDrawer = binding.nvView;
        View hView = nvDrawer.getHeaderView(0);
        ImageView nav_profile = (ImageView) hView.findViewById(R.id.profile_image_drawer);
        TextView nav_username = (TextView) hView.findViewById(R.id.drawer_username);
        TextView loggedAs = (TextView) hView.findViewById(R.id.loggedAs_drawer);
        if (currentUser.getImage() != null) {
            Glide.with(getContext()).load(currentUser.getImage().getUrl()).circleCrop().into(nav_profile);
        }
        if (currentUser.getName() != null) {
            nav_username.setText(currentUser.getName());
        }
        if (currentUser.isLoggedAsTutor()) {
            loggedAs.setText("Tutor");
        } else {loggedAs.setText("Student");}

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        getConnectedUsers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(getActivity(), mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.myprofile:
                goToProfile();
                break;
            case R.id.changepassword:
                setDialogForChangingPassword(currentUser);
                break;
            case R.id.ChangeRole:
                changeRole();
                break;
            case R.id.Logout:
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        //getActivity().getActionBar().setTitle("Tinder");
        mDrawer.closeDrawers();
    }

    private void getConnectedUsers() {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        if (currentUser.isLoggedAsTutor() == true) {
            query.whereEqualTo(UserTutorConnection.KEY_TUTOR, currentUser);
        } else {
            query.whereEqualTo(UserTutorConnection.KEY_STUDENT, currentUser);
        }
        query.whereEqualTo(UserTutorConnection.KEY_ACCEPTED, true);
        query.setLimit(4);

        UserTutorConnectionUtils newUserTutorConnectionUtils = new UserTutorConnectionUtils(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                userTutorConnectionLists.addAll(output);

                if (output.size() == 0) {
                    binding.rvHomeStudents.setVisibility(View.GONE);
                    binding.emptyViewTutor.setVisibility(View.VISIBLE);
                }
                binding.progressbarRvHomeStudents.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionUtils.getUserTutorConnections(query);

    }

    private void goToProfile() {
        if (currentUser.isLoggedAsTutor()) {
            Intent i = new Intent(getContext(), TutorSetupActivity.class);
            i.putExtra("ProfileFragmentTutor", true);
            startActivity(i);
        } else {
            Intent i = new Intent(getContext(), StudentSetupActivity.class);
            i.putExtra("ProfileFragmentTutor", true);
            startActivity(i);
        }
    }

    private void setDialogForChangingPassword(User user) {

        dialogBuilder = new AlertDialog.Builder(getContext());
        final View view = getLayoutInflater().inflate(R.layout.change_password_dialog, null);


        final EditText etOldPassword = (EditText) view.findViewById(R.id.etOldPassword);
        final EditText etNewPassword = (EditText) view.findViewById(R.id.etNewPassword);
        final EditText etConfirmNewPassword = (EditText) view.findViewById(R.id.etConfirmNewPassword);
        final TextView tvErrorMessage = (TextView) view.findViewById(R.id.tvErrorMessage);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancelPickTime);
        final Button btnOkay = (Button) view.findViewById(R.id.btnOkayPickTime);

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etOldPassword.getText().toString() == null || etOldPassword.getText().toString().length() == 0) {
                    tvErrorMessage.setText("Please enter a password!");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                if (etNewPassword.getText().toString() == null || etNewPassword.getText().toString().length() == 0) {
                    tvErrorMessage.setText("Please enter a password!");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                if (etConfirmNewPassword.getText().toString() == null || etConfirmNewPassword.getText().toString().length() == 0) {
                    tvErrorMessage.setText("Please confirm new password!");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                if (!etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
                    tvErrorMessage.setText("Password doesn't match");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), etOldPassword.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The password is correct
                            currentUser.setPassword(etNewPassword.getText().toString());
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null) {
                                        dialog.dismiss();
                                        snackBarUtil.setSnackBar("Password reset successfully!");
                                    } else {
                                        snackBarUtil.setSnackBar("Error saving new password!");
                                    }
                                }
                            });
                        } else {
                            // The password was incorrect
                            tvErrorMessage.setText("Password is incorrect!");
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                });

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

    }

    private void changeRole() {
        if (currentUser.isLoggedAsTutor()) {
            currentUser.setKeyLoggedastutor(false);
            currentUser.setKeyIsstudent(true);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null) {
                        Intent i = new Intent(getContext(), StudentHomeActivity.class);
                        startActivity(i);
                        getActivity().finishAffinity();
                    }
                }
            });
        } else {
            if (currentUser.isTutor()) {
                currentUser.setKeyLoggedastutor(true);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Intent i = new Intent(getContext(), TutorHomeActivity.class);
                            startActivity(i);
                            getActivity().finishAffinity();
                        }
                    }
                });
            }
            else {
                Intent i = new Intent(getContext(), TutorSetupActivity.class);
                i.putExtra("ChangeRole", true);
                startActivity(i);
            }

        }
    }

    private void configureLogoutDialog() {
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ParseUser.logOut();
                Intent i = new Intent(getContext(), ChooseRoleActivity.class);
                startActivity(i);
                getActivity().finishAffinity();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}