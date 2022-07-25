package com.example.myguide.StudentFragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SingleSpinnerListener;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.example.myguide.R;
import com.example.myguide.Utils.SnackBarUtil;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.models.Course;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LookForTutorFragment extends Fragment {

    private FragmentLookForTutorBinding binding;
    ArrayList<Course> courseList;
    Dialog dialog;
    public static final String TAG = "LookForTutorFragment";
    SingleSpinnerSearch singleSpinnerSearch;
    Course selectedCourse;
    List<String> WeekDays;
    ArrayAdapter weekDaysAdapter;
    String selectedWeekDay;
    private SmartMaterialSpinner<Course> courseSpinner;
    Calendar c = Calendar.getInstance();
    int hour1  = c.get(Calendar.HOUR_OF_DAY);;
    int hour2 = c.get(Calendar.HOUR_OF_DAY);

    int minute1 = c.get(Calendar.MINUTE);
    int minute2 = c.get(Calendar.MINUTE);
    Date startDate = null;
    Date endDate = null;
    SnackBarUtil snackBarUtil;


    public LookForTutorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLookForTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseList = new ArrayList<Course>();
        Course starterCourse = new Course();
        starterCourse.setTitle("Loading...");
        courseList.add(starterCourse);
        selectedCourse = null;
        courseSpinner = binding.singleItemSelectionSpinner;
        courseSpinner.setItem(courseList);

        snackBarUtil = new SnackBarUtil(getContext(), binding.lookForTutor);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCourse = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.tvStartTime.setText(((hour1==12 || hour1==0) ? 12 : hour1%12) + ":" + ((minute1<10) ? "0"+minute1 : minute1) + " " + ((hour1>=12) ? "PM" : "AM"));
        binding.tvEndTime.setText(((hour2==12 || hour2==0) ? 12 : hour2%12) + ":" + ((minute2<10) ? "0"+minute2 : minute2) + " " + ((hour2>=12) ? "PM" : "AM"));

        WeekDays = Arrays.asList(getResources().getStringArray(R.array.WeekDays));
        weekDaysAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_weekdays_search_fragment, WeekDays);
        binding.AvailabilityDropDown.setAdapter(weekDaysAdapter);
        selectedWeekDay = WeekDays.get(0);

        binding.tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(binding.tvStartTime);
            }
        });
        binding.tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(binding.tvEndTime);
            }
        });

        binding.AllDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.AllDay.isChecked()) {
                    binding.TimeRange.setVisibility(View.GONE);
                } else {
                    binding.TimeRange.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.AvailabilityDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedWeekDay = WeekDays.get(position);
                if (position==0) {
                    binding.radioGroup.setVisibility(View.GONE);
                    binding.TimeRange.setVisibility(View.GONE);
                } else {
                    binding.radioGroup.setVisibility(View.VISIBLE);
                    binding.TimeRange.setVisibility(View.VISIBLE);
                }

            }
        });

        binding.btnFindTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean specificTimeSearch = false;
                if (selectedCourse==null) {
                    snackBarUtil.setSnackBar("Please select a course!");
                    return;
                }
                String zipcode = binding.etZipcode.getText().toString();
                String minPrice = binding.etMinPrice.getText().toString();
                String maxPrice = binding.etMaxPrice.getText().toString();
                String rangeInMiles = binding.etRangeInMiles.getText().toString();

                if (zipcode.length() > 0 && zipcode.length() != 5) {
                    snackBarUtil.setSnackBar("Invalid zipcode");
                    return;
                }

                if (rangeInMiles.length() > 0 && zipcode.length() == 0) {
                    snackBarUtil.setSnackBar("Please enter zipcode!");
                    return;
                }

                if (selectedWeekDay.equals(WeekDays.get(0))) {
                    selectedWeekDay = null;
                } else {
                    if (!binding.AllDay.isChecked()) {
                        if (!hasPassedRequirement()) {
                            return;
                        }
                        specificTimeSearch = true;
                    }
                }



                SearchResultFragment searchResultFragment = new SearchResultFragment();
                Bundle args = new Bundle();
                args.putString("selectedCourseId", selectedCourse.getObjectId());
                args.putString("zipcode", zipcode);
                args.putString("minPrice", minPrice);
                args.putString("maxPrice", maxPrice);
                args.putString("rangeInMiles", rangeInMiles);

                args.putString("Availability", selectedWeekDay);
                args.putInt("hour1", hour1);
                args.putInt("hour2", hour2);
                args.putInt("minute1", minute1);
                args.putInt("minute2", minute2);
                args.putBoolean("specificTimeSearch", specificTimeSearch);

                searchResultFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, searchResultFragment, "SearchResult").addToBackStack("SearchResult").commit();
            }
        });
        getAllCourses();
    }

    private boolean hasPassedRequirement() {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date());
        startTime.set(Calendar.HOUR_OF_DAY, hour1);
        startTime.set(Calendar.MINUTE, minute1);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date());
        endTime.set(Calendar.HOUR_OF_DAY, hour2);
        endTime.set(Calendar.MINUTE, minute2);
        endTime.set(Calendar.SECOND, 0);

        if (endTime.before(startTime) || endTime.equals(startTime)) {
            snackBarUtil.setSnackBar("End date should be after the start date");
            return false;
        }
        startDate = startTime.getTime();
        endDate = endTime.getTime();

        return true;
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
                if (courses.size()>0) {
                    courseList.clear();
                    courseList.addAll(courses);
                }

            }
        });
    }

    public void popTimePicker(TextView tv)  {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                if (tv == binding.tvStartTime) {
                    hour1 = selectedHour;
                    minute1 = selectedMinute;
                    binding.tvStartTime.setText(((hour1==12 || hour1==0) ? 12 : hour1%12) + ":" + ((minute1<10) ? "0"+minute1 : minute1) + " " + ((hour1>=12) ? "PM" : "AM"));
                } else {
                    hour2 = selectedHour;
                    minute2 = selectedMinute;
                    binding.tvEndTime.setText(((hour2==12 || hour2==0) ? 12 : hour2%12) + ":" + ((minute2<10) ? "0"+minute2 : minute2) + " " + ((hour2>=12) ? "PM" : "AM"));
                }
            }
        };
        TimePickerDialog timePickerDialog;
        if (tv == binding.tvStartTime) {
            timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour1, minute1, false);

        } else {
            timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour2, minute2, false);
        }
        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }
}




