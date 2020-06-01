package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.ClassViewStudentActivity;
import com.example.quiz.Model.Classroom;
import com.example.quiz.R;

import java.util.ArrayList;

public class ClassListStudentAdapter  extends RecyclerView.Adapter<ClassListStudentAdapter.ClassHolder> {
    private ArrayList<Classroom> classes;
    private Context context;
    private String userId;
    public ClassListStudentAdapter(ArrayList<Classroom> classes, Context context, String userId) {
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
                Intent intent = new Intent(context, ClassViewStudentActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("classId", classes.get(position).getId());
                intent.putExtra("className", classes.get(position).getName());
                context.startActivity(intent);
            }
        });

        int a = Color.parseColor("#ffff4444");//red
        int b = Color.parseColor("#ffff8800");//orange
        int c = Color.parseColor("#ff99cc00");//green
        int f = Color.parseColor("#ffaa66cc");//purple
        int g = Color.parseColor("#ffffbb33");//yellow
        int h = Color.parseColor("#ff000000");//black

        int color = h;
        char letter = classes.get(position).getName().charAt(0);
        if((letter>='A' && letter<='E')||(letter>='a' && letter<='e'))
            color = a;
        else if((letter>='F' && letter<='J')||(letter>='f' && letter<='j'))
            color = b;
        else if((letter>='K' && letter<='O')||(letter>='k' && letter<='o'))
            color = c;
        else if((letter>='P' && letter<='T')||(letter>='p' && letter<='t'))
            color = f;
        else if((letter>='U' && letter<='Z')||(letter>='u' && letter<='z'))
            color = g;

        holder.classSelection.setBackgroundTintList(ColorStateList.valueOf(color));
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
