package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myguide.R;
import com.example.myguide.databinding.ActivityAddEducationBinding;
import com.example.myguide.databinding.ActivityChooseroleBinding;
import com.example.myguide.models.Degree;
import com.example.myguide.models.Education;
import com.example.myguide.models.FieldOfStudy;
import com.example.myguide.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class AddEducationActivity extends AppCompatActivity {

    ActivityAddEducationBinding addEducationBinding;
    ArrayList<String> degreeTitleArrayList;
    ArrayList<String> fieldOfStudyTitleArrayList;
    ArrayList<Degree> degrees;
    ArrayList<FieldOfStudy> fieldOfStudy;
    Dialog dialog;
    public static final String TAG = "AddEducationActivity";
    Education toBeEditedEducation;
    Integer adapterPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addEducationBinding = ActivityAddEducationBinding.inflate(getLayoutInflater());
        View view = addEducationBinding.getRoot();
        setContentView(view);

        degreeTitleArrayList =new ArrayList<>();
        fieldOfStudyTitleArrayList = new ArrayList<>();

        degrees =new ArrayList<>();
        fieldOfStudy = new ArrayList<>();

        if (getIntent().hasExtra("editEducation")) {
            toBeEditedEducation = getIntent().getParcelableExtra("editEducation");
            adapterPosition = getIntent().getIntExtra("adapterPosition", 0);
            addEducationBinding.etFieldOfStudy.setText(toBeEditedEducation.getFieldofStudy());
            addEducationBinding.etDegree.setText(toBeEditedEducation.getDegree());
            addEducationBinding.etSchool.setText(toBeEditedEducation.getSchool());
        }


        addEducationBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addEducationBinding.etSchool.getText().toString().length() == 0) {
                    Toast.makeText(AddEducationActivity.this, "Please insert a school", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addEducationBinding.etDegree.getText().toString().length() == 0) {
                    Toast.makeText(AddEducationActivity.this, "Please insert a degree", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (addEducationBinding.etFieldOfStudy.getText().length() == 0) {
                    Toast.makeText(AddEducationActivity.this, "Please insert field of study", Toast.LENGTH_SHORT).show();
                    return;

                }
                String school = addEducationBinding.etSchool.getText().toString();
                String degree = addEducationBinding.etDegree.getText().toString();
                String field = addEducationBinding.etFieldOfStudy.getText().toString();

                if (getIntent().hasExtra("editEducation")) {
                    toBeEditedEducation.setSchool(school);
                    toBeEditedEducation.setDegree(degree);
                    toBeEditedEducation.setFieldofstudy(field);
                    toBeEditedEducation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(AddEducationActivity.this, "Edit saved successfully", Toast.LENGTH_SHORT).show();
                                Intent data = new Intent(AddEducationActivity.this, TutorSetupActivity.class);
                                data.putExtra("toBeEditedEducation", toBeEditedEducation);
                                data.putExtra("adapterPosition", adapterPosition);
                                startActivity(data);
//                                setResult(RESULT_OK, data); // set result code and bundle data for response
                                finish();
                            }
                        }
                    });
                } else {
                    saveEducation(school, degree, field);

                }
            }
        });

        getAllFieldOfStudies();
        getAllDegrees();

    }

    private void saveEducation(String school, String degree, String field) {
        Education education = new Education();
        User currentUser = (User) ParseUser.getCurrentUser();

        education.setSchool(school);
        education.setDegree(degree);
        education.setOwner(currentUser);
        education.setFieldofstudy(field);
        education.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving education", e);
                } else {
                    Toast.makeText(AddEducationActivity.this, "Successfully saved education!", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("newEducation", education);
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                }
            }
        });
    }

    private void getAllFieldOfStudies() {

        ParseQuery<FieldOfStudy> query = ParseQuery.getQuery(FieldOfStudy.class);
        query.include(FieldOfStudy.KEY_TITLE);
        query.orderByAscending("title");
        query.findInBackground(new FindCallback<FieldOfStudy>() {
            @Override
            public void done(List<FieldOfStudy> field, ParseException e) {
                if (e != null) {
                    return;
                }
                fieldOfStudy.addAll(field);
                for (FieldOfStudy f: fieldOfStudy) {
                    Log.i(TAG, f.getTitle());
                    fieldOfStudyTitleArrayList.add(f.getTitle().toString());

                }
                dropdownAction(addEducationBinding.ivSelectedField, false);
            }
        });

    }

    private void dropdownAction(View view, boolean isDegree) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initialize dialog
                dialog=new Dialog(AddEducationActivity.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(950,1200);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter;
                if (isDegree) {
                    adapter =new ArrayAdapter<>(AddEducationActivity.this, android.R.layout.simple_list_item_1,degreeTitleArrayList);

                } else {

                    adapter=new ArrayAdapter<>(AddEducationActivity.this, android.R.layout.simple_list_item_1,fieldOfStudyTitleArrayList);

                }
                //set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
//
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        if (isDegree) {
                            addEducationBinding.etDegree.setText(adapter.getItem(position));

                        } else {
                            addEducationBinding.etFieldOfStudy.setText(adapter.getItem(position));

                        }

                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private void getAllDegrees() {
        //instantiate a query variable that has methods to grab all posts
        ParseQuery<Degree> query = ParseQuery.getQuery(Degree.class);
        query.include(Degree.KEY_TITLE);
        query.orderByAscending("Title");
        query.findInBackground(new FindCallback<Degree>() {
            @Override
            public void done(List<Degree> degree, ParseException e) {
                if (e != null) {
                    return;
                }
                degrees = (ArrayList<Degree>) degree;
                for (Degree d: degrees) {
                    Log.i(TAG, d.getTitle());
                    degreeTitleArrayList.add(d.getTitle().toString());

                }
                dropdownAction(addEducationBinding.ivSelectedDegree, true);
            }
        });
    }
}





