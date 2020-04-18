package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.StudentQuizSessionListActivity;
import com.example.quiz.Model.User;
import com.example.quiz.R;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentHolder> {
    private ArrayList<User> students;
    private Context context;
    private String classId;

    public StudentListAdapter(ArrayList<User> students, Context context, String classId) {
        this.students = students;
        this.context = context;
        this.classId = classId;
    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_student, parent, false);
        return new StudentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHolder holder, final int position) {
        holder.student.setText(students.get(position).getFirstName()+ " "+ students.get(position).getLastName());
        holder.student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentQuizSessionListActivity.class);
                intent.putExtra("userId", students.get(position).getId());
                intent.putExtra("userName", students.get(position).getFirstName());
                intent.putExtra("classId", classId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class StudentHolder extends RecyclerView.ViewHolder {
        public TextView student;
        public StudentHolder(View itemView) {
            super(itemView);
            student = itemView.findViewById(R.id.studentNameInHolder);
        }
    }
}
