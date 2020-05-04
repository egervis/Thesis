package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuizSessionListAdapter;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.QuizSessionStudent;
import com.example.quiz.Service.QuizService;
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
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;

public class StudentQuizSessionListActivity extends AppCompatActivity {
    private String studentId;
    private String userName;
    private String classId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_session_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        studentId = getIntent().getExtras().getString("userId");
        userName = getIntent().getExtras().getString("userName");
        classId = getIntent().getExtras().getString("classId");

        TextView name = findViewById(R.id.studentNameInQuizSessionList);
        name.setText(userName+"'s Quiz Sessions:");

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.quizSessionListRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QuizService quizService = new QuizService();
        quizService.getStudentQuizSessions(studentId, classId, new OnSuccessListener<ArrayList<QuizSessionStudent>>() {
            @Override
            public void onSuccess(ArrayList<QuizSessionStudent> quizSessionsStudent) {
                recyclerViewAdapter = new QuizSessionListAdapter(quizSessionsStudent, StudentQuizSessionListActivity.this, studentId);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz sessions "+e);
            }
        });
    }
}
