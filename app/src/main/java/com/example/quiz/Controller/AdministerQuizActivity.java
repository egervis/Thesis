package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuizSelectAdapter;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;

import com.example.quiz.R;

import java.util.ArrayList;

public class AdministerQuizActivity extends AppCompatActivity {
    private String userId;
    private String classId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administer_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");
        classId = getIntent().getExtras().getString("classId");

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.administerQuizListRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final AdministerQuizActivity activity = this;
        QuizService quizService = new QuizService();
        quizService.getQuizzes(userId, new OnSuccessListener<ArrayList<Quiz>>() {
            @Override
            public void onSuccess(ArrayList<Quiz> quizzes) {
                EditText editText = findViewById(R.id.durationInputAQ);
                recyclerViewAdapter = new QuizSelectAdapter(quizzes, AdministerQuizActivity.this, classId, editText, activity);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quizzes " + e);
            }
        });
    }
}
