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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.bumptech.glide.Glide;
import com.example.myguide.EducationAdapter;
import com.example.myguide.R;
import com.example.myguide.databinding.ActivityAddEducationBinding;
import com.example.myguide.databinding.ActivityTutorSetupBinding;
import com.example.myguide.models.Course;
import com.example.myguide.models.Degree;
import com.example.myguide.models.Education;
import com.example.myguide.models.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pchmn.materialchips.ChipsInput;

import java.io.File;
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
        adapter = new EducationAdapter(this, allEducations);
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
                String about = tutorSetupBinding.etAbout.getText().toString();
                String price = tutorSetupBinding.etPrice.getText().toString();
                if (about.length() == 0 || price.length() ==0 || selectedCoursesId.size() == 0) {
                    Toast.makeText(TutorSetupActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentUser.setPrice(price);
                currentUser.setAbout(about);
                currentUser.setKeyCoursestutored(selectedCoursesId);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Toast.makeText(TutorSetupActivity.this, "Registration completed successgully!", Toast.LENGTH_SHORT).show();
                            currentUser.setKeyIsnew(false);
                            currentUser.saveInBackground();

                            Intent i = new Intent(TutorSetupActivity.this, TutorHomeActivity.class);
                            startActivity(i);
                            finish();

                        }
                    }
                });
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
                    // If the user comes back to this activity from EditActivity
                    // with no error or cancellation
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Get the data passed from EditActivity
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
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
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
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP
                // Load the taken image into a preview
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
}