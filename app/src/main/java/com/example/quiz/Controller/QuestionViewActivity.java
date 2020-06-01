package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.ChoiceListAdapter;
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
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;

public class QuestionViewActivity extends AppCompatActivity {
    private String questionId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Questions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        questionId = getIntent().getExtras().getString("questionId");

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionChoicesInView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        QuizService quizService = new QuizService();
        quizService.getQuestion(questionId, new OnSuccessListener<Question>() {
            @Override
            public void onSuccess(Question question) {
                fillInfo("Question: "+question.getText(), "Category: "+question.getCategory(), "Points: "+question.getPointsWorth());

                recyclerViewAdapter = new ChoiceListAdapter(question.getChoices(), getApplicationContext());
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get question "+ e);
            }
        });
    }

    private void fillInfo(String text, String category, String points) {
        TextView textText = findViewById(R.id.questionTextInView);
        TextView categoryText = findViewById(R.id.questionCategoryInView);
        TextView pointsText = findViewById(R.id.questionPointsInView);

        textText.setText(text);
        categoryText.setText(category);
        pointsText.setText(points);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
