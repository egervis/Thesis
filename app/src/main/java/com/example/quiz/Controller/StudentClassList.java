package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.ClassListStudentAdapter;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;

public class StudentClassList extends AppCompatActivity {
    private String userId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Classes");

        userId = getIntent().getExtras().getString("id");

        joinClass();
        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.classListStudentRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ClassService classService = new ClassService();
        classService.getClasses(userId, "student", new OnSuccessListener<ArrayList<Classroom>>() {
            @Override
            public void onSuccess(ArrayList<Classroom> classrooms) {
                TextView text = findViewById(R.id.noClassesStudent);
                if(classrooms.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
                recyclerViewAdapter = new ClassListStudentAdapter(classrooms, StudentClassList.this, userId);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get classes "+ e);
            }
        });
    }

    private void joinClass() {
        Button button = findViewById(R.id.joinClassMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentClassList.this, JoinClassActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        makeRV();
    }
}
