package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuizListAdapter;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

public class QuizListActivity extends AppCompatActivity {
    private String userId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");

        setOnClicks();
        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.quizListTeacherRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QuizService quizService = new QuizService();
        quizService.getQuizzes(userId, new OnSuccessListener<ArrayList<Quiz>>() {
            @Override
            public void onSuccess(ArrayList<Quiz> quizzes) {
                TextView text = findViewById(R.id.noQuizzesTeacher);
                if(quizzes.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
                recyclerViewAdapter = new QuizListAdapter(quizzes, QuizListActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quizzes "+ e);
            }
        });
    }

    private void setOnClicks() {
        Button createClass = findViewById(R.id.createQuizMenuButton);
        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizListActivity.this, CreateQuizActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button classMenu = findViewById(R.id.classMenuButtonQZL);
        classMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizListActivity.this, TeacherClassList.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button questionMenu = findViewById(R.id.questionMenuButtonQZL);
        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizListActivity.this, QuestionListActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button quizMenu = findViewById(R.id.quizMenuButtonQZL);
        quizMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        makeRV();
    }
}
