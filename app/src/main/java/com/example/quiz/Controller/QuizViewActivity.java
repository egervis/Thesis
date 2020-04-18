package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionListAdapter;
import com.example.quiz.Model.Quiz;
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

public class QuizViewActivity extends AppCompatActivity {
    private String quizId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        quizId = getIntent().getExtras().getString("quizId");

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionListInQuizView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QuizService quizService = new QuizService();
        quizService.getQuiz(quizId, new OnSuccessListener<Quiz>() {
            @Override
            public void onSuccess(Quiz quiz) {
                fillInfo(quiz.getName(), "Instructions: "+quiz.getInstructions(), "Category: "+quiz.getCategory());
                recyclerViewAdapter = new QuestionListAdapter(quiz.getQuestions(), QuizViewActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz "+ e);
            }
        });
    }

    private void fillInfo(String name, String instructions, String category) {
        TextView nameText = findViewById(R.id.quizNameInView);
        TextView instructionsText = findViewById(R.id.quizInstructionsInView);
        TextView categoryText = findViewById(R.id.quizCategoryInView);

        nameText.setText(name);
        instructionsText.setText(instructions);
        categoryText.setText(category);
    }

}
