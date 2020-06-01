package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionListStudentChoiceAdapter;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.StudentChoice;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StudentChoiceViewActivity extends AppCompatActivity {
    private String studentId;
    private String quizId;
    private double quizGrade;
    private String quizSessionId;
    private Date startTime;
    private Date endTime;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choice_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quiz Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentId = getIntent().getExtras().getString("userId");
        quizId = getIntent().getExtras().getString("quizId");
        quizGrade = getIntent().getExtras().getDouble("quizGrade");
        quizSessionId = getIntent().getExtras().getString("quizSessionId");
        startTime = (Date) getIntent().getExtras().get("startTime");
        endTime = (Date) getIntent().getExtras().get("endTime");

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionsRVInSC);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final QuizService quizService = new QuizService();
        quizService.getQuiz(quizId, new OnSuccessListener<Quiz>() {
            @Override
            public void onSuccess(final Quiz quiz) {
                displayInfo(quiz.getName(), quizGrade, startTime, endTime);
                quizService.getStudentChoices(studentId, quizSessionId, new OnSuccessListener<ArrayList<StudentChoice>>() {
                    @Override
                    public void onSuccess(ArrayList<StudentChoice> studentChoices) {
                        recyclerViewAdapter = new QuestionListStudentChoiceAdapter(quiz.getQuestions(), studentChoices, StudentChoiceViewActivity.this);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to get student choices "+ e);
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz "+ e);
            }
        });
    }

    private void displayInfo(String name, double grade, Date startTime, Date endTime) {
        TextView nameText = findViewById(R.id.quizNameSC);
        TextView gradeText = findViewById(R.id.quizGradeSC);
        TextView dateText = findViewById(R.id.quizDateSC);
        TextView startTimeText = findViewById(R.id.quizStartTimeSC);
        TextView endTimeText = findViewById(R.id.quizEndTimeSC);
        nameText.setText(name);
        gradeText.setText("Grade: "+grade);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        dateText.setText("Date: " + formatter.format(startTime));
        formatter = new SimpleDateFormat("hh:mm aa");
        startTimeText.setText("Start Time: "+ formatter.format(startTime));
        long difference = endTime.getTime()-startTime.getTime();
        long duration = difference/(60*1000);
        if(duration<1) {
            duration = difference / 1000;
            endTimeText.setText("Duration: "+duration+" sec");
        }
        else
            endTimeText.setText("Duration: "+duration+" min");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
