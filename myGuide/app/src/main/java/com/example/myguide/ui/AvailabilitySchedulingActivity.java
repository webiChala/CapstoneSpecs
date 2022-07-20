package com.example.myguide.ui;

/*
 * Copyright 2019 LinkedIn Corporation
 * All Rights Reserved.
 *
 * Licensed under the BSD 2-Clause License (the "License").  See License in the project root for
 * license information.
 */

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//import com.linkedin.android.tachyon.DayView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myguide.R;
import com.example.myguide.models.Availability;
import com.example.myguide.models.User;
import com.linkedin.android.tachyon.DayView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This sample activity demonstrates how to populate the day view with events.
 */
public class AvailabilitySchedulingActivity extends AppCompatActivity {

    private Calendar day;
    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private Calendar editEventDate;
    private Calendar editEventStartTime;
    private Calendar editEventEndTime;
    private Availability editEventDraft;

    private ViewGroup content;
    private TextView dateTextView;
    private ScrollView scrollView;
    private DayView dayView;
    List<String> AllWeekDays;
    int WhatWeekDayItIs = 0;
    HashMap<String, List<Availability>> map;

    private Button btnNext;
    private Button btnPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AllWeekDays = new ArrayList<>();
        AllWeekDays.add("Monday");
        AllWeekDays.add("Tuesday");
        AllWeekDays.add("Wednesday");
        AllWeekDays.add("Thursday");
        AllWeekDays.add("Friday");
        AllWeekDays.add("Saturday");
        AllWeekDays.add("Sunday");

        day = Calendar.getInstance();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        // Populate today's entry in the map with a list of example events

        map = new HashMap<>();

        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        setContentView(R.layout.sample_activity);

        content = findViewById(R.id.sample_content);
        dateTextView = findViewById(R.id.sample_date);
        scrollView = findViewById(R.id.sample_scroll);
        dayView = findViewById(R.id.sample_day);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousClick();
            }
        });

        // Inflate a label view for each hour the day view will display
        Calendar hour = (Calendar) day.clone();
        List<View> hourLabelViews = new ArrayList<>();
        for (int i = dayView.getStartHour(); i <= dayView.getEndHour(); i++) {
            hour.set(Calendar.HOUR_OF_DAY, i);

            TextView hourLabelView = (TextView) getLayoutInflater().inflate(R.layout.hour_label, dayView, false);
            hourLabelView.setText(timeFormat.format(hour.getTime()));
            hourLabelViews.add(hourLabelView);
        }
        dayView.setHourLabelViews(hourLabelViews);
        dateTextView.setText(AllWeekDays.get(WhatWeekDayItIs));
        getAvailabilty();

    }

    public void onPreviousClick() {
        if (WhatWeekDayItIs==0) {
            WhatWeekDayItIs = 6;
        }
        else {
            WhatWeekDayItIs -= 1;
        }
        onDayChange();
    }

    public void onNextClick() {
        //day.add(Calendar.DAY_OF_YEAR, 1);
        if (WhatWeekDayItIs==6) {
            WhatWeekDayItIs = 0;
        } else {
            WhatWeekDayItIs += 1;
        }
        dateTextView.setText(AllWeekDays.get(WhatWeekDayItIs));
        onDayChange();
    }

    public void onAddEventClick(View v) {
        editEventDate = (Calendar) day.clone();

        editEventStartTime = (Calendar) day.clone();

        editEventEndTime = (Calendar) day.clone();
        editEventEndTime.add(Calendar.MINUTE, 30);

        showEditEventDialog(false, null, android.R.color.holo_red_dark);
    }

    public void onScrollClick(View v) {
        showScrollTargetDialog();
    }

    private void onDayChange() {
        dateTextView.setText(AllWeekDays.get(WhatWeekDayItIs));
        onEventsChange();
    }

    private void onEventsChange() {
        // The day view needs a list of event views and a corresponding list of event time ranges
        List<View> eventViews = null;
        List<DayView.EventTimeRange> eventTimeRanges = null;
        List<Availability> availabilities = map.get(AllWeekDays.get(WhatWeekDayItIs));

        if (availabilities != null) {
            // Sort the events by start time so the layout happens in correct order
            Collections.sort(availabilities, new Comparator<Availability>() {
                @Override
                public int compare(Availability o1, Availability o2) {
                    return o1.getHour().intValue() < o2.getHour().intValue() ? -1 : (o1.getHour().intValue() == o2.getHour().intValue() ? (o1.getMinute().intValue() < o2.getMinute().intValue() ? -1 : (o1.getMinute().intValue() == o2.getMinute().intValue() ? 0 : 1)) : 1);
                }
            });

            eventViews = new ArrayList<>();
            eventTimeRanges = new ArrayList<>();

            // Reclaim all of the existing event views so we can reuse them if needed, this process
            // can be useful if your day view is hosted in a recycler view for example
            List<View> recycled = dayView.removeEventViews();
            int remaining = recycled != null ? recycled.size() : 0;

            for (final Availability availability : availabilities) {
                // Try to recycle an existing event view if there are enough left, otherwise inflate
                // a new one
                View eventView = remaining > 0 ? recycled.get(--remaining) : getLayoutInflater().inflate(R.layout.event, dayView, false);

                ((TextView) eventView.findViewById(R.id.event_title)).setText(availability.getTitle());
                ((TextView) eventView.findViewById(R.id.event_location)).setText((availability.getAvailability() ? "Available" : "Not Available"));
                if (availability.getColor()!=null) {
                    eventView.setBackgroundColor(getResources().getColor(availability.getColor().intValue()));
                } else {
                    eventView.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                }

                // When an event is clicked, start a new draft event and show the edit event dialog
                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editEventDraft = availability;

                        editEventDate = (Calendar) day.clone();

                        editEventStartTime = Calendar.getInstance();
                        editEventStartTime.set(Calendar.HOUR_OF_DAY, editEventDraft.getHour().intValue());
                        editEventStartTime.set(Calendar.MINUTE, editEventDraft.getMinute().intValue());
                        editEventStartTime.set(Calendar.SECOND, 0);
                        editEventStartTime.set(Calendar.MILLISECOND, 0);

                        editEventEndTime = (Calendar) editEventStartTime.clone();
                        editEventEndTime.add(Calendar.MINUTE, editEventDraft.getDuration().intValue());

                        showEditEventDialog(true, editEventDraft.getTitle(), editEventDraft.getColor().intValue());
                    }
                });

                eventViews.add(eventView);

                // The day view needs the event time ranges in the start minute/end minute format,
                // so calculate those here
                int startMinute = 60 * availability.getHour().intValue() + availability.getMinute().intValue();
                int endMinute = startMinute + availability.getDuration().intValue();
                eventTimeRanges.add(new DayView.EventTimeRange(startMinute, endMinute));
            }
        }

        // Update the day view with the new events
        dayView.setEventViews(eventViews, eventTimeRanges);
        dayView.setOnTouchListener(new MyOnTouchListener(this));
    }

    private void showEditEventDialog(boolean eventExists, String eventTitle, @ColorRes int eventColor) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_dialog, content, false);
        final TextView titleTextView = view.findViewById(R.id.edit_event_title);
        final Button startTimeButton = view.findViewById(R.id.edit_event_start_time);
        final Button endTimeButton = view.findViewById(R.id.edit_event_end_time);
        final RadioButton redRadioButton = view.findViewById(R.id.edit_event_red);
        final RadioButton blueRadioButton = view.findViewById(R.id.edit_event_blue);
        final RadioButton orangeRadioButton = view.findViewById(R.id.edit_event_orange);
        final RadioButton greenRadioButton = view.findViewById(R.id.edit_event_green);
        final RadioButton purpleRadioButton = view.findViewById(R.id.edit_event_purple);
        final RadioButton isAvailable = view.findViewById(R.id.isAvailable);
        final RadioButton isNotAvailable = view.findViewById(R.id.isNotAvailable);

        if (isAvailable.isChecked()==false && isNotAvailable.isChecked() == false) {
            isAvailable.setChecked(true);
        }

        titleTextView.setText(eventTitle);

        startTimeButton.setText(timeFormat.format(editEventStartTime.getTime()));
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editEventStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        editEventStartTime.set(Calendar.MINUTE, minute);

                        startTimeButton.setText(timeFormat.format(editEventStartTime.getTime()));

                        if (!editEventEndTime.after(editEventStartTime)) {
                            editEventEndTime = (Calendar) editEventStartTime.clone();
                            editEventEndTime.add(Calendar.MINUTE, 30);

                            endTimeButton.setText(timeFormat.format(editEventEndTime.getTime()));
                        }
                    }
                };

                new TimePickerDialog(AvailabilitySchedulingActivity.this, listener, editEventStartTime.get(Calendar.HOUR_OF_DAY), editEventStartTime.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(AvailabilitySchedulingActivity.this)).show();

            }
        });

        endTimeButton.setText(timeFormat.format(editEventEndTime.getTime()));
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editEventEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        editEventEndTime.set(Calendar.MINUTE, minute);

                        if (!editEventEndTime.after(editEventStartTime)) {
                            editEventEndTime = (Calendar) editEventStartTime.clone();
                            editEventEndTime.add(Calendar.MINUTE, 30);
                        }

                        endTimeButton.setText(timeFormat.format(editEventEndTime.getTime()));
                    }
                };

                new TimePickerDialog(AvailabilitySchedulingActivity.this, listener, editEventEndTime.get(Calendar.HOUR_OF_DAY), editEventEndTime.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(AvailabilitySchedulingActivity.this)).show();

            }
        });

        if (eventColor == android.R.color.holo_blue_dark) {
            blueRadioButton.setChecked(true);
        } else if (eventColor == android.R.color.holo_orange_dark) {
            orangeRadioButton.setChecked(true);
        } else if (eventColor == android.R.color.holo_green_dark) {
            greenRadioButton.setChecked(true);
        } else if (eventColor == android.R.color.holo_purple) {
            purpleRadioButton.setChecked(true);
        } else {
            redRadioButton.setChecked(true);
        }

        if (eventExists) {
            if (editEventDraft != null && !editEventDraft.getAvailability()) {
                isNotAvailable.setChecked(true);
            } else {
                isAvailable.setChecked(true);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // If the event already exists, we are editing it, otherwise we are adding a new event
        builder.setTitle(eventExists ? R.string.edit_event : R.string.add_event);

//         When the event changes are confirmed, read the new values from the dialog and then add
//         this event to the list
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Availability> availabilities = map.get(AllWeekDays.get(WhatWeekDayItIs));

                if (availabilities == null) {
                    availabilities = new ArrayList<>();
                    map.put(AllWeekDays.get(WhatWeekDayItIs), availabilities);
                }

                String title = titleTextView.getText().toString();
                //String location = locationTextView.getText().toString();
                int hour = editEventStartTime.get(Calendar.HOUR_OF_DAY);
                int minute = editEventStartTime.get(Calendar.MINUTE);
                int duration = (int) (editEventEndTime.getTimeInMillis() - editEventStartTime.getTimeInMillis()) / 60000;

                @ColorRes int color;
                if (blueRadioButton.isChecked()) {
                    color = android.R.color.holo_blue_dark;
                } else if (orangeRadioButton.isChecked()) {
                    color = android.R.color.holo_orange_dark;
                } else if (greenRadioButton.isChecked()) {
                    color = android.R.color.holo_green_dark;
                } else if (purpleRadioButton.isChecked()) {
                    color = android.R.color.holo_purple;
                } else {
                    color = android.R.color.holo_red_dark;
                }


                if (editEventDraft==null) {
                    editEventDraft = new Availability();

                } else {
                    List<Availability> availabilitiy = map.get(AllWeekDays.get(WhatWeekDayItIs));
                    if (availabilitiy != null) {
                        availabilitiy.remove(editEventDraft);
                    }
                }
                editEventDraft.setColor(color);
                editEventDraft.setDuration(duration);
                editEventDraft.setTitle(title);
                editEventDraft.setUser((User) ParseUser.getCurrentUser());
                editEventDraft.setHour(hour);
                editEventDraft.setMinute(minute);
                editEventDraft.setAvailability(isAvailable.isChecked());
                editEventDraft.setWeekDay(AllWeekDays.get(WhatWeekDayItIs));
                editEventDraft.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                        }
                    }
                });
                availabilities.add(editEventDraft);

                onEditEventDismiss(true, false);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onEditEventDismiss(false, false);
            }
        });

        // If the event already exists, provide a delete option
        if (eventExists) {
            builder.setNeutralButton(R.string.edit_event_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onEditEventDismiss(true, true);
                }
            });
        }

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onEditEventDismiss(false, false);
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void showScrollTargetDialog() {
        View view = getLayoutInflater().inflate(R.layout.scroll_target_dialog, content, false);
        final Button timeButton = view.findViewById(R.id.scroll_target_time);
        final Button firstEventTopButton = view.findViewById(R.id.scroll_target_first_event_top);
        final Button firstEventBottomButton = view.findViewById(R.id.scroll_target_first_event_bottom);
        final Button lastEventTopButton = view.findViewById(R.id.scroll_target_last_event_top);
        final Button lastEventBottomButton = view.findViewById(R.id.scroll_target_last_event_bottom);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scroll_to);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setView(view);

        final AlertDialog dialog = builder.show();

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int top = dayView.getHourTop(hourOfDay);
                        int bottom = dayView.getHourBottom(hourOfDay);
                        int y = top + (bottom - top) * minute / 60;
                        scrollView.smoothScrollTo(0, y);
                        dialog.dismiss();
                    }
                };

                new TimePickerDialog(AvailabilitySchedulingActivity.this, listener, 0, 0, android.text.format.DateFormat.is24HourFormat(AvailabilitySchedulingActivity.this)).show();

            }
        });

        firstEventTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, dayView.getFirstEventTop());
                dialog.dismiss();
            }
        });

        firstEventBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, dayView.getFirstEventBottom());
                dialog.dismiss();
            }
        });

        lastEventTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, dayView.getLastEventTop());
                dialog.dismiss();
            }
        });

        lastEventBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, dayView.getLastEventBottom());
                dialog.dismiss();

            }
        });
    }

    private void onEditEventDismiss(boolean modified, boolean deleted) {
        if (modified && editEventDraft != null && deleted) {
            editEventDraft.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null) {
                        List<Availability> availabilitiy = map.get(AllWeekDays.get(WhatWeekDayItIs));
                        if (availabilitiy != null) {
                            availabilitiy.remove(editEventDraft);
                        }

                        Toast.makeText(AvailabilitySchedulingActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                        editEventDraft = null;
                        onEventsChange();
                    }
                }
            });
        } else {
            editEventDraft = null;
            onEventsChange();
        }

    }


    public void getAvailabilty() {
        ParseQuery<Availability> query = ParseQuery.getQuery(Availability.class);
        query.include(Availability.KEY_WEEKDAY);
        query.include(Availability.KEY_AVAILABLE);
        query.include(Availability.KEY_COLOR);
        query.include(Availability.KEY_USER);
        query.include(Availability.KEY_DURATION);
        query.include(Availability.KEY_MINUTE);
        query.include(Availability.KEY_HOUR);
        query.include(Availability.KEY_TITLE);

        query.whereEqualTo(Availability.KEY_USER, (User) ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Availability>() {
            @Override
            public void done(List<Availability> objects, ParseException e) {
                if (e==null) {
                    for (Availability a:objects) {
                        if (map.containsKey(a.getWeekDay())) {
                            List<Availability> value = map.get(a.getWeekDay());
                            value.add(a);
                            map.put(a.getWeekDay(), value);
                        } else {
                            List<Availability> newValue = new ArrayList<>();
                            newValue.add(a);
                            map.put(a.getWeekDay(), newValue);
                        }
                    }
                    onDayChange();
                }
            }
        });

    }

    public class MyOnTouchListener implements View.OnTouchListener {
        final GestureDetector gesture;

        public MyOnTouchListener(Activity activity) {
            gesture = new GestureDetector(activity, new MyGestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gesture.onTouchEvent(event);
        }
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final int SWIPE_MIN_DISTANCE = 120;
            final int SWIPE_MAX_OFF_PATH = 250;
            final int SWIPE_THRESHOLD_VELOCITY = 200;

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }

                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // TODO action for right to left swipe
                    onNextClick();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // TODO action for left to right swipe
                    onPreviousClick();
                }
            } catch (Exception e) {
                // nothing
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
