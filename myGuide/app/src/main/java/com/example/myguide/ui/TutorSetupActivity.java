package com.example.myguide.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.bumptech.glide.Glide;
import com.example.myguide.adapters.EducationAdapter;
import com.example.myguide.databinding.ActivityTutorSetupBinding;
import com.example.myguide.models.Course;
import com.example.myguide.models.Education;
import com.example.myguide.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TutorSetupActivity extends AppCompatActivity {

    ActivityTutorSetupBinding tutorSetupBinding;
    public static final String TAG = "TutorSetupActivity";
    private EducationAdapter adapter;
    private List<Education> allEducations;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    MultiSpinnerSearch multiSelectSpinnerWithSearch;
    List<String> selectedCoursesId;
    User currentUser;
    String about;
    String price;
    String zipcode;
    boolean isOnlineTutor;
    boolean isInPersonTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorSetupBinding = ActivityTutorSetupBinding.inflate(getLayoutInflater());
        View view = tutorSetupBinding.getRoot();
        setContentView(view);

        selectedCoursesId = new ArrayList<>();

        tutorSetupBinding.ibAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TutorSetupActivity.this, AddEducationActivity.class);
                editActivityResultLauncher.launch(i);
            }
        });

        currentUser = (User) ParseUser.getCurrentUser();
        if (currentUser.getImage() != null) {
            Glide.with(this).load(currentUser.getImage().getUrl()).circleCrop().into(tutorSetupBinding.ibTutorProfileRegister);
        }


        allEducations = new ArrayList<>();
        adapter = new EducationAdapter(this, allEducations, true);
        tutorSetupBinding.rvEducation.setAdapter(adapter);
        tutorSetupBinding.rvEducation.setLayoutManager(new LinearLayoutManager(this));

        tutorSetupBinding.ibTutorProfileRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        tutorSetupBinding.btnSaveTutorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                about = tutorSetupBinding.etAbout.getText().toString();
                price = tutorSetupBinding.etPrice.getText().toString();
                zipcode = tutorSetupBinding.etZipcode.getText().toString();
                isOnlineTutor = tutorSetupBinding.isOnlineTutor.isChecked();
                isInPersonTutor = tutorSetupBinding.isInpersonTutor.isChecked();

                if (about.length() == 0 || price.length() == 0 || selectedCoursesId.size() == 0) {
                    Toast.makeText(TutorSetupActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isInPersonTutor==true && zipcode.length() != 5) {
                    Toast.makeText(TutorSetupActivity.this, "Zipcode not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isOnlineTutor == false && isInPersonTutor == false) {
                    Toast.makeText(TutorSetupActivity.this, "Please select at least one preference for tutoring options!", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentUser.setPrice(Integer.parseInt(price));
                currentUser.setAbout(about);
                currentUser.setKeyCoursestutored(selectedCoursesId);
                currentUser.setKeyZipcode(zipcode);
                currentUser.setKeyIsinpersontutor(isInPersonTutor);
                currentUser.setKeyIsonlinetutor(isOnlineTutor);
                if (zipcode.length() == 5) {
                    getGeoLocationFromZipcode();
                } else {
                    registerUser();
                }



            }
        });


        multiSelectSpinnerWithSearch = tutorSetupBinding.multipleItemSelectionSpinner;
        getAllCourses();
        getAllEducation();
    }

    ActivityResultLauncher<Intent> editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.hasExtra("newEducation")) {
                            Education education = data.getExtras().getParcelable("newEducation");
                            allEducations.add(0, education);
                            adapter.notifyItemInserted(0);
                            tutorSetupBinding.rvEducation.smoothScrollToPosition(0);
                        }

                    }
                }
            });

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
                    Glide.with(this).load(takenImage).circleCrop().into(tutorSetupBinding.ibTutorProfileRegister);
                    currentUser.setImage(new ParseFile(photoFile));
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null) {
                                Toast.makeText(TutorSetupActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }





            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);


    }
    private void getAllEducation() {
        ParseQuery<Education> query = ParseQuery.getQuery(Education.class);
        query.whereEqualTo("Owner", ParseUser.getCurrentUser());
        query.include(Education.KEY_OWNER);
        query.include(Education.KEY_FIELDOFSTUDY);
        query.include(Education.KEY_DEGREE);
        query.include(Education.KEY_SCHOOL);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Education>() {
            @Override
            public void done(List<Education> educations, ParseException e) {
                if (e != null) {
                    return;
                }
                allEducations.clear();
                allEducations.addAll(educations);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getAllCourses() {
        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.include(Course.KEY_TITLE);
        query.orderByAscending(Course.KEY_TITLE);
        query.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> courses, ParseException e) {
                if (e != null) {
                    return;
                }
                setUpCourseDropdown(courses);
            }
        });
    }

    private void setUpCourseDropdown(List<Course> courses) {
        multiSelectSpinnerWithSearch.setSearchEnabled(true);
        multiSelectSpinnerWithSearch.setSearchHint("Select your mood");
        multiSelectSpinnerWithSearch.setEmptyTitle("Not Data Found!");
        multiSelectSpinnerWithSearch.setClearText("Close & Clear");

        List<KeyPairBoolData> data = new ArrayList<>();
        for (Course c:courses
             ) {
            KeyPairBoolData d = new KeyPairBoolData(c.getTitle(), false);
            d.setObject(c);
            data.add(d);
        }

        multiSelectSpinnerWithSearch.setItems(data, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                selectedCoursesId.clear();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Course selectedCourse = (Course) items.get(i).getObject();
                        selectedCoursesId.add(selectedCourse.getObjectId());
//
                    }
                }
                currentUser.setKeyCoursestutored(selectedCoursesId);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(TutorSetupActivity.this, "Course list saved successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
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
                            registerUser();
                        } 
                    } finally {
                        urlConnection.disconnect();


                    }
                } catch (Exception e) {
                }
            }
        })).start();
    }

    private void registerUser() {
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    Toast.makeText(TutorSetupActivity.this, "Registration completed successfully!", Toast.LENGTH_SHORT).show();
                    currentUser.setKeyIsnew(false);
                    currentUser.saveInBackground();

                    Intent i = new Intent(TutorSetupActivity.this, TutorHomeActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        });
    }
}