package com.example.myguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.databinding.ItemEventsBinding;
import com.example.myguide.databinding.ItemTutorBinding;
import com.example.myguide.models.Event;
import com.example.myguide.models.User;
import com.example.myguide.ui.TutorDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventsBinding binding = ItemEventsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        ItemEventsBinding itemEventsBinding;

        public ViewHolder(ItemEventsBinding binding) {
            super(binding.getRoot());
            itemEventsBinding = binding;
            itemEventsBinding.getRoot().setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

        }

        public void bind(Event event) {
            if (event.getTitle() != null) {
                itemEventsBinding.tvEventTitle.setText(event.getTitle());
            }
            if (event.getDetail() != null) {
                itemEventsBinding.tvEventDetail.setText(event.getDetail());
            }
            if (event.getStartDate() != null && event.getEndDate() != null) {
                Calendar calStart = Calendar.getInstance();
                Calendar calEnd = Calendar.getInstance();
                calStart.setTime(event.getStartDate());
                calEnd.setTime(event.getEndDate());
                int hour1 = calStart.get(Calendar.HOUR_OF_DAY);
                int hour2 = calEnd.get(Calendar.HOUR_OF_DAY);
                int minute1 = calStart.get(Calendar.MINUTE);
                int minute2 = calEnd.get(Calendar.MINUTE);
                String startTime = ((hour1==12 || hour1==0) ? 12 : hour1%12) + ":" + ((minute1<10) ? "0"+minute1 : minute1) + " " + ((hour1>=12) ? "PM" : "AM");
                String endTime = ((hour2==12 || hour2==0) ? 12 : hour2%12) + ":" + ((minute2<10) ? "0"+minute2 : minute2) + " " + ((hour2>=12) ? "PM" : "AM");

                itemEventsBinding.tvTime.setText(startTime + "-" + endTime);
            }
        }
    }
}
