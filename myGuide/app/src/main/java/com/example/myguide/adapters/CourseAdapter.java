package com.example.myguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myguide.databinding.ItemCourseBinding;
import com.example.myguide.databinding.ItemEducationBinding;
import com.example.myguide.models.Course;
import com.example.myguide.models.Education;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private Context context;
    private List<Course> courses;

    public CourseAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCourseBinding itemCourseBinding;

        public ViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            itemCourseBinding = binding;
        }

        public void bind(Course course) {
            itemCourseBinding.tvCourseTitle.setText(course.getTitle());
        }
    }
}
