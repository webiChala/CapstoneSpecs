package com.example.myguide.TutorFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.databinding.ChangePasswordDialogBinding;
import com.example.myguide.databinding.FragmentHomeTutorBinding;
import com.example.myguide.databinding.FragmentProfileTutorBinding;
import com.example.myguide.models.User;
import com.example.myguide.ui.ChooseRoleActivity;
import com.example.myguide.ui.StudentHomeActivity;
import com.example.myguide.ui.StudentSetupActivity;
import com.example.myguide.ui.TutorHomeActivity;
import com.example.myguide.ui.TutorSetupActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ProfileFragmentTutor extends Fragment {

    FragmentProfileTutorBinding binding;
    User currentUser = (User) ParseUser.getCurrentUser();
    AlertDialog.Builder builder;
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;



    public ProfileFragmentTutor() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentUser.getImage() != null) {
            Glide.with(getContext()).load(currentUser.getImage().getUrl()).circleCrop().into(binding.ivProfilePictureTutor);
        }

        if (!currentUser.isLoggedAsTutor()) {
            binding.tvChangeRoleProfileTutor.setText("Change to Tutor");
        }

        builder = new AlertDialog.Builder(getContext());
        binding.tvUsernameProfileTutor.setText(currentUser.getName());

        configureLogoutDialog();
        binding.profileTutorLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        if (currentUser.getUsername() == null) {
            binding.ivPasswordKeyProfileTutor.setVisibility(View.INVISIBLE);
            binding.tvChangePasswordProfileTutor.setVisibility(View.INVISIBLE);
        }
        binding.tvMyProfileTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });
        binding.tvChangeRoleProfileTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRole();
            }
        });
        binding.tvChangePasswordProfileTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDialogForChangingPassword(currentUser);
            }
        });


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
                                        Toast.makeText(getContext(), "Password reset successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Error saving new password!", Toast.LENGTH_SHORT).show();
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
}