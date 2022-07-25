package com.example.myguide.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.databinding.ActivityGeneralSignupBinding;
import com.example.myguide.databinding.ActivityStudentSetupBinding;
import com.example.myguide.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class StudentSetupActivity extends AppCompatActivity {

    ActivityStudentSetupBinding studentSetupBinding;
    User currentUser;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    public static final String TAG = "StudentSetupActivity";
    View viewSnack;
    ContextThemeWrapper ctw;
    Snackbar snack;
    String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentSetupBinding = ActivityStudentSetupBinding.inflate(getLayoutInflater());
        View view = studentSetupBinding.getRoot();
        setContentView(view);

        ctw = new ContextThemeWrapper(StudentSetupActivity.this, R.style.CustomSnackbarTheme);
        snack = Snackbar.make(ctw, studentSetupBinding.studentSetup, "Zipcode is incorrect", Snackbar.LENGTH_LONG);
        viewSnack = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)viewSnack.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 40, 0, 0);
        viewSnack.setLayoutParams(params);

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

        if (currentUser.getPhonenumber() != null) {
            studentSetupBinding.etPhonenumber.setText(currentUser.getPhonenumber());
        }

        if (currentUser.getKeyZipcode() != null) {
            studentSetupBinding.etZipcode.setText(currentUser.getKeyZipcode());
        }

        studentSetupBinding.btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber = studentSetupBinding.etPhonenumber.getText().toString();
                zipcode = studentSetupBinding.etZipcode.getText().toString();


                if (phonenumber.length() == 0 || zipcode.length() == 0) {
                    snack.setText("Please fill out all the field!");
                    snack.show();
                    return;
                }
                if (zipcode.length() != 5) {
                    snack.setText("Zipcode is incorrect!");
                    snack.show();
                    return;
                }
                currentUser.setKeyPhonenumber(phonenumber);
                currentUser.setKeyZipcode(zipcode);
                getGeoLocationFromZipcode();

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
                                snack.setText("Profile updated successfully!");
                                snack.show();
                            }
                        }
                    });

                }
            } else {
                snack.setText("Picture wasn't taken!");
                snack.show();
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

    private void getGeoLocationFromZipcode() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String where = URLEncoder.encode("{" +
                            "    \"US_Zip_Code\": " + zipcode +
                            "}", "utf-8");
                    URL url = new URL("https://parseapi.back4app.com/classes/Uszipcode_US_Zip_Code?limit=1&where=" + where);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestProperty("X-Parse-Application-Id", "r9pjIpWlLiO3gfmU6ZKPZPa0OPi5jrknUOlsx24g"); // This is your app's application id
                    urlConnection.setRequestProperty("X-Parse-REST-API-Key", "IZUWWVVGXfs0Kk7aUqWcHMfRo5RzQ3DkhXZ7dZvL"); // This is your app's REST API key
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        JSONObject data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
                        Double Latitude = data.getJSONArray("results").getJSONObject(0).getDouble("Latitude");
                        Double Longitude = data.getJSONArray("results").getJSONObject(0).getDouble("Longitude");

                        ParseGeoPoint currentUserLocation = new ParseGeoPoint(Latitude, Longitude);

                        if (currentUser != null) {
                            currentUser.put("Location", currentUserLocation);
                            saveUser();
                        }
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                }
            }
        })).start();
    }
    private void saveUser() {
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    currentUser.setKeyIsnew(false);
                    currentUser.saveInBackground();
                    Intent i = new Intent(StudentSetupActivity.this, StudentHomeActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}