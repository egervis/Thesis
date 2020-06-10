package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.example.quiz.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class QuizSessionStatsActivity extends AppCompatActivity {
    private String quizSessionId;
    private String quizName;
    private Date startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_session_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quiz Session");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        quizSessionId = getIntent().getExtras().getString("quizSessionId");
        quizName = getIntent().getExtras().getString("quizName");
        startTime = (Date)getIntent().getExtras().get("quizSessionStartTime");

        getStudentScores();
    }

    private void getStudentScores() {
        QuizService quizService = new QuizService();
        quizService.getQuizSessionGrades(quizSessionId, new OnSuccessListener<ArrayList<Double>>() {
            @Override
            public void onSuccess(ArrayList<Double> doubles) {
                displayInfo(doubles);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz grades "+e);
            }
        });
    }

    private void displayInfo(ArrayList<Double> grades) {
        TextView nameText = findViewById(R.id.quizNameStats);
        TextView dateText = findViewById(R.id.quizDateStats);
        TextView averageText = findViewById(R.id.quizAverageStats);
        TextView medianText = findViewById(R.id.quizMedianStats);
        TextView sdText = findViewById(R.id.quizStandardDeviationStats);

        nameText.setText(quizName);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        dateText.setText("Administered on " + formatter.format(startTime));

        if(grades.size()>0) {
            double average = 0;
            for (double d : grades)
                average += d;
            average = average / grades.size();
            averageText.setText("Average: " + average);
            ArrayList<Double> lst = grades;
            Collections.sort(lst);
            int mid = lst.size() / 2;
            if (lst.size() % 2 == 1)
                medianText.setText("Median: " + lst.get(mid));
            else
                medianText.setText("Median: " + ((lst.get(mid - 1) + lst.get(mid)) / 2));
            double standardDeviation = 0;
            for (double d : grades)
                standardDeviation += Math.pow(d - average, 2);
            standardDeviation = Math.sqrt(standardDeviation / grades.size());
            sdText.setText("Standard Deviation: " + standardDeviation);
        }
        else {
            averageText.setText("Average: N/A");
            medianText.setText("Median: N/A");
            sdText.setText("Standard Deviation: N/A");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
