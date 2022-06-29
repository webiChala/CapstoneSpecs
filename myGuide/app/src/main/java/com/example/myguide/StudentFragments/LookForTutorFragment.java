package com.example.myguide.StudentFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SingleSpinnerListener;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.example.myguide.R;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.models.Course;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class LookForTutorFragment extends Fragment {

    private FragmentLookForTutorBinding binding;
    ArrayList<String> courseList;
    Dialog dialog;
    public static final String TAG = "LookForTutorFragment";
    SingleSpinnerSearch singleSpinnerSearch;
    String selectedCourseId;

    public LookForTutorFragment() {
        // Required empty public constructor
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

        courseList = new ArrayList<String>();
        singleSpinnerSearch = binding.singleItemSelectionSpinner;
        selectedCourseId = null;

        binding.btnFindTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCourseId==null || selectedCourseId.length() == 0) {
                    Toast.makeText(getContext(), "Please select a course!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String zipcode = binding.etZipcode.getText().toString();
                String price = binding.etPriceFind.getText().toString();

                SearchResultFragment searchResultFragment = new SearchResultFragment();
                Bundle args = new Bundle();
                args.putString("selectedCourseId", selectedCourseId);
                args.putString("zipcode", zipcode);
                args.putString("price", price);

                searchResultFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, searchResultFragment, "SearchResult").addToBackStack("SearchResult").commit();

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
                    Log.i(TAG, "not working!", e);
                    return;
                }
                for (Course c:courses
                ) {
                    Log.i(TAG, c.getTitle());

                }

                setUpCourseDropdown(courses);
//                spinner();
            }
        });
    }

    private void setUpCourseDropdown (List<Course> courses) {

        // Pass true, If you want color separation. Otherwise false. default = false.
        singleSpinnerSearch.setColorseparation(true);
        // Pass true If you want searchView above the list. Otherwise false. default = true.
        singleSpinnerSearch.setSearchEnabled(true);
        // A text that will display in search hint.
        singleSpinnerSearch.setSearchHint("Select course");

        List<KeyPairBoolData> data = new ArrayList<>();
        for (Course c:courses
        ) {
            Log.i(TAG, c.getTitle());
            KeyPairBoolData d = new KeyPairBoolData(c.getTitle(), false);
            d.setObject(c);
            data.add(d);
        }

        singleSpinnerSearch.setItems(data, new SingleSpinnerListener() {
            @Override
            public void onItemsSelected(KeyPairBoolData selectedItem) {
                Course selectedCourse = (Course) selectedItem.getObject();
                selectedCourseId = selectedCourse.getObjectId();
                Toast.makeText(getContext(), "Selected: " + selectedCourse.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClear() {
                selectedCourseId = null;
                Toast.makeText(getContext(), "Cleared Selected Item", Toast.LENGTH_SHORT).show();
            }
        });

    }


}




