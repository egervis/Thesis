package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.ClassViewActivity;
import com.example.quiz.Model.Classroom;
import com.example.quiz.R;

import java.util.ArrayList;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ClassHolder> {
    private ArrayList<Classroom> classes;
    private Context context;
    private String userId;
    public ClassListAdapter(ArrayList<Classroom> classes, Context context, String userId) {
        this.classes = classes;
        this.context = context;
        this.userId = userId;
    }
    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_class, parent, false);
        return new ClassHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassHolder holder, final int position) {
        final Classroom classroom = classes.get(position);
        holder.classSelection.setText(classroom.getName());
        holder.classSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassViewActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("classId", classes.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ClassHolder extends RecyclerView.ViewHolder {
        public Button classSelection;
        public ClassHolder(View itemView) {
            super(itemView);
            classSelection = itemView.findViewById(R.id.classSelectionButton);
        }
    }
}
