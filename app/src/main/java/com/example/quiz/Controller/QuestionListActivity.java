package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionListAdapter;
import com.example.quiz.Model.Question;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;

public class QuestionListActivity extends AppCompatActivity {
    private String userId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");
        getSupportActionBar().setTitle("Questions");

        setOnClicks();
        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionListBank);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QuizService quizService = new QuizService();
        quizService.getQuestions(userId, new OnSuccessListener<ArrayList<Question>>() {
            @Override
            public void onSuccess(ArrayList<Question> questions) {
                TextView text = findViewById(R.id.noQuestionsTeacher);
                if(questions.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
                recyclerViewAdapter = new QuestionListAdapter(questions, QuestionListActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get questions "+ e);
            }
        });
    }

    private void setOnClicks() {
        Button createClass = findViewById(R.id.createQuestionMenuButton);
        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, CreateQuestionActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button classMenu = findViewById(R.id.classMenuButtonQL);
        classMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, TeacherClassList.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button questionMenu = findViewById(R.id.questionMenuButtonQL);
        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        Button quizMenu = findViewById(R.id.quizMenuButtonQL);
        quizMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, QuizListActivity.class);
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
