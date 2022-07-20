package com.example.myguide.CommonFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myguide.R;
import com.example.myguide.Utils.UserTutorConnectionUtils;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.Event;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;


public class BottomSheetFragment extends BottomSheetDialogFragment {
    Calendar c = Calendar.getInstance();
    int year1 = c.get(Calendar.YEAR);
    int year2 = c.get(Calendar.YEAR);

    int month1 = c.get(Calendar.MONTH);
    int month2 = c.get(Calendar.MONTH);

    int day1 = c.get(Calendar.DAY_OF_MONTH);
    int day2 = c.get(Calendar.DAY_OF_MONTH);

    int hour1  = c.get(Calendar.HOUR_OF_DAY);;
    int hour2 = c.get(Calendar.HOUR_OF_DAY);

    int minute1 = c.get(Calendar.MINUTE);
    int minute2 = c.get(Calendar.MINUTE);

    int second1 = c.get(Calendar.SECOND);
    int second2 = c.get(Calendar.SECOND);

    String repetition = "Does not repeat";
    List<User> connectionLists;
    User currentUser = (User) ParseUser.getCurrentUser();

    Date startDate;
    Date endDate;
    User addedUser;
    SimpleDateFormat sdf;
    public static Button btnSaveEvent;
    public BottomSheetFragment b;

    com.example.myguide.databinding.FragmentBottomSheetBinding binding;
    public static final String TAG = "BottomSheetFragment";

    public BottomSheetFragment() {
    }

    public void setFragment(BottomSheetFragment bsf) {
        b = bsf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> repetitionDropdownList = new ArrayList<>();
        connectionLists = new ArrayList<>();
        btnSaveEvent = binding.btnSaveEvent;
        addedUser = new User();
        addedUser.setName("None");
        connectionLists.add(addedUser);



        repetitionDropdownList.add("Does not repeat");
        repetitionDropdownList.add("Every day");
        repetitionDropdownList.add("Every week");
        ArrayAdapter<String> repetitionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, repetitionDropdownList);
        ArrayAdapter<User> connectionListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_expandable_list_item_1, connectionLists);
        binding.repetitionDropdown.setAdapter(repetitionAdapter);
        binding.searchableAddTutor.setAdapter(connectionListAdapter);

        binding.btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPassedRequirments()) {
                    saveEvent();
                } else {
                    return;
                }
            }
        });

        binding.searchableAddTutor.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, long id) {
                addedUser = connectionLists.get(position);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        binding.btnSaveEvent.setBackgroundColor(Color.BLUE);

        binding.repetitionDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                repetition = (String) parent.getItemAtPosition(position);
            }
        });

        sdf = new SimpleDateFormat("EE, MMM dd");
        binding.tvStartDate.setText(sdf.format(c.getTime()));
        binding.tvEndDate.setText(sdf.format(c.getTime()));

        binding.tvStartTime.setText(((hour1==12 || hour1==0) ? 12 : hour1%12) + ":" + ((minute1<10) ? "0"+minute1 : minute1) + " " + ((hour1>=12) ? "PM" : "AM"));
        binding.tvEndTime.setText(((hour2==12 || hour2==0) ? 12 : hour2%12) + ":" + ((minute2<10) ? "0"+minute2 : minute2) + " " + ((hour2>=12) ? "PM" : "AM"));

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
        binding.tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDatePicker(binding.tvStartDate);
            }
        });
        binding.tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDatePicker(binding.tvEndDate);
            }
        });


        getAllConnectedUsers();



    }
    public static long getDateDiff(SimpleDateFormat format, Date oldDate, Date newDate) {
        try {
            return TimeUnit.DAYS.convert(oldDate.getTime() - newDate.getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public Button getSaveButton() {
        return btnSaveEvent;
    }
    public Event createEvent(Date eventDate) {
        Event newEvent = new Event();
        if (binding.etEventTitle != null && binding.etEventTitle.getText().toString().length() > 0) {
            newEvent.setTitle(binding.etEventTitle.getText().toString());
        }
        if (!addedUser.getName().equals("None")) {
            newEvent.setAddedUser(addedUser);
        }
        newEvent.setUser((User) ParseUser.getCurrentUser());
        newEvent.setRepetition(repetition);
        newEvent.setStartDate(startDate);
        newEvent.setEndDate(endDate);
        newEvent.setEventDate(eventDate);
        if (binding.etLocation!=null && binding.etLocation.getText().toString().length()>0) {
            newEvent.setLocation(binding.etLocation.getText().toString());
        }
        if (binding.etDetail!=null && binding.etDetail.getText().toString().length()>0) {
            newEvent.setDetail(binding.etDetail.getText().toString());
        }
        return newEvent;
    }

    private void saveEvent() {
        List<Event> listOfEvent = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int dateDifference = (int) getDateDiff(sdf, startDate, endDate);

        if (repetition.equals("Every week")) {
            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 7), date = start.getTime()) {
                if (!date.after(endDate)) {
                    Event newEvent = createEvent(date);
                    listOfEvent.add(newEvent);
                }

            }
        } else {
            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                if (!date.after(endDate)) {
                    Event newEvent = createEvent(date);
                    listOfEvent.add(newEvent);
                }
            }
        }

        binding.btnSaveEvent.setClickable(false);
        binding.btnSaveEvent.setBackgroundColor(Color.DKGRAY);


        ParseObject.saveAllInBackground(listOfEvent, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    Toast.makeText(getContext(), "Event/s saved successfully!", Toast.LENGTH_SHORT).show();
                    b.dismiss();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, new ScheduleFragment()).commit();
                    return;
                }
            }
        });


    }

    private boolean hasPassedRequirments() {
        startDate = getDate(year1, month1, day1, hour1, minute1, second1);
        endDate = getDate(year2, month2, day2, hour2, minute2, second2);
        if (endDate.before(startDate) || endDate.equals(startDate)) {
            Toast.makeText(getContext(), "End date should be after the start date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getAllConnectedUsers() {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        if (currentUser.isLoggedAsTutor() == true) {
            query.whereEqualTo(UserTutorConnection.KEY_TUTOR, currentUser);
        } else {
            query.whereEqualTo(UserTutorConnection.KEY_STUDENT, currentUser);
        }
        query.whereEqualTo(UserTutorConnection.KEY_ACCEPTED, true);

        UserTutorConnectionUtils newUserTutorConnectionUtils = new UserTutorConnectionUtils(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                for (UserTutorConnection connection : output) {
                    if (currentUser.isLoggedAsTutor() == true) {
                        connectionLists.add(connection.getStudent());
                    } else {
                        connectionLists.add(connection.getTutor());
                    }
                }
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionUtils.getUserTutorConnections(query);


    }

    private void popDatePicker(TextView tv) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (tv == binding.tvStartDate) {

                    year1 = year;
                    month1 = month;
                    day1 = dayOfMonth;
                    binding.tvStartDate.setText(new SimpleDateFormat("EE, MMM dd, YYYY").format(c.getTime()));
                } else {
                    year2 = year;
                    month2 = month;
                    day2 = dayOfMonth;
                    binding.tvEndDate.setText(new SimpleDateFormat("EE, MMM dd, YYYY").format(c.getTime()));
                }
            }
        }, day1, month1, year1);
        datePickerDialog.show();
        datePickerDialog.updateDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

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

    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}