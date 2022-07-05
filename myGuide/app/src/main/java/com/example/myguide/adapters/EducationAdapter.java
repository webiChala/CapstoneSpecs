package com.example.myguide.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myguide.databinding.ItemEducationBinding;
import com.example.myguide.models.Education;
import com.example.myguide.ui.AddEducationActivity;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder> {

    private Context context;
    private List<Education> educations;
    private boolean isCurrentUser;

    public EducationAdapter(Context context, List<Education> educations, boolean isCurrentUser) {
        this.context = context;
        this.educations = educations;
        this.isCurrentUser = isCurrentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEducationBinding binding = ItemEducationBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Education education = educations.get(position);
        holder.bind(education);
    }

    @Override
    public int getItemCount() {
        return educations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemEducationBinding itemEducationBinding;
        AlertDialog.Builder builder;

        public ViewHolder(ItemEducationBinding binding) {
            super(binding.getRoot());
            itemEducationBinding = binding;
            itemEducationBinding.getRoot().setOnClickListener(this);

            if (isCurrentUser == false) {
                itemEducationBinding.ibEditEducation.setVisibility(View.GONE);
                itemEducationBinding.btnDeleteEducation.setVisibility(View.GONE);
            }
            builder = new AlertDialog.Builder(context);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    Education deletedEducation = educations.get(getAdapterPosition());
                    deletedEducation.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    educations.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        }

        @Override
        public void onClick(View v) {

        }

        public void bind(Education education) {
            itemEducationBinding.tvSchoolItemEducation.setText(education.getSchool().toString());
            itemEducationBinding.tvDegreeItemEducation.setText(education.getDegree().toString());
            itemEducationBinding.tvFieldOfStudyItemEducation.setText(education.getFieldofStudy().toString());

            itemEducationBinding.btnDeleteEducation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            itemEducationBinding.ibEditEducation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AddEducationActivity.class);
                    i.putExtra("editEducation", educations.get(getAdapterPosition()));
                    i.putExtra("adapterPosition", getAdapterPosition());
                    context.startActivity(i);
                }
            });


        }
    }
}
