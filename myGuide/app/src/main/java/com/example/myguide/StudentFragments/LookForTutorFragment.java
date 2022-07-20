package com.example.myguide.StudentFragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SingleSpinnerListener;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.example.myguide.R;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.models.Course;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LookForTutorFragment extends Fragment {

    private FragmentLookForTutorBinding binding;
    //ArrayList<String> courseList;
    ArrayList<Course> courseList;
    Dialog dialog;
    public static final String TAG = "LookForTutorFragment";
    SingleSpinnerSearch singleSpinnerSearch;
    Course selectedCourse;
    List<String> WeekDays;
    ArrayAdapter weekDaysAdapter;
    String selectedWeekDay;
    private SmartMaterialSpinner<Course> courseSpinner;

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

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCourse = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        WeekDays = Arrays.asList(getResources().getStringArray(R.array.WeekDays));
        weekDaysAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_weekdays_search_fragment, WeekDays);
        binding.AvailabilityDropDown.setAdapter(weekDaysAdapter);
        selectedWeekDay = WeekDays.get(0);

        binding.AvailabilityDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), WeekDays.get(position), Toast.LENGTH_SHORT).show();
                selectedWeekDay = WeekDays.get(position);
            }
        });

        binding.btnFindTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCourse==null) {
                    Toast.makeText(getContext(), "Please select a course!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String zipcode = binding.etZipcode.getText().toString();
                String minPrice = binding.etMinPrice.getText().toString();
                String maxPrice = binding.etMaxPrice.getText().toString();
                String rangeInMiles = binding.etRangeInMiles.getText().toString();

                if (zipcode.length() > 0 && zipcode.length() != 5) {
                    Toast.makeText(getContext(), "Invalid zipcode", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rangeInMiles.length() > 0 && zipcode.length() == 0) {
                    Toast.makeText(getContext(), "Please enter zipcode!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SearchResultFragment searchResultFragment = new SearchResultFragment();
                Bundle args = new Bundle();
                args.putString("selectedCourseId", selectedCourse.getObjectId());
                args.putString("zipcode", zipcode);
                args.putString("minPrice", minPrice);
                args.putString("maxPrice", maxPrice);
                args.putString("rangeInMiles", rangeInMiles);
                if (selectedWeekDay.equals(WeekDays.get(0))) {
                    selectedWeekDay = null;
                }
                args.putString("Availability", selectedWeekDay);
                searchResultFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, searchResultFragment, "SearchResult").addToBackStack("SearchResult").commit();
            }
        });
        getAllCourses();
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
}




