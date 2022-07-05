package com.example.myguide.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.databinding.ActivityGeneralSignupBinding;
import com.example.myguide.databinding.ActivityStudentSetupBinding;
import com.example.myguide.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class StudentSetupActivity extends AppCompatActivity {

    ActivityStudentSetupBinding studentSetupBinding;
    User currentUser;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    public static final String TAG = "StudentSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentSetupBinding = ActivityStudentSetupBinding.inflate(getLayoutInflater());
        View view = studentSetupBinding.getRoot();
        setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        currentUser = (User) ParseUser.getCurrentUser();

        studentSetupBinding.ibAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });
        if (currentUser.getImage() != null) {
            Glide.with(this).load(currentUser.getImage().getUrl()).circleCrop().into(studentSetupBinding.ibProfile);
        }

        studentSetupBinding.btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber = studentSetupBinding.etPhonenumber.getText().toString();
                String zipcode = studentSetupBinding.etZipcode.getText().toString();

                if (phonenumber.length() == 0 || zipcode.length() == 0) {
                    Toast.makeText(StudentSetupActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentUser.setKeyPhonenumber(phonenumber);
                currentUser.setKeyZipcode(zipcode);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(StudentSetupActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            currentUser.setKeyIsnew(false);
                            currentUser.saveInBackground();
                            Intent i = new Intent(StudentSetupActivity.this, StudentHomeActivity.class);
                            startActivity(i);

                        }

                    }
                });



            }
        });


    }

    private  void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (photoFile != null) {
                    Glide.with(this).load(takenImage).circleCrop().into(studentSetupBinding.ibProfile);
                    currentUser.setImage(new ParseFile(photoFile));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null) {
                                Toast.makeText(StudentSetupActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }





            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);


    }
}