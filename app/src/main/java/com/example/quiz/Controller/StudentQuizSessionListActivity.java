package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuizSessionStudentListAdapter;
import com.example.quiz.Model.QuizSessionStudent;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentQuizSessionListActivity extends AppCompatActivity {
    private String studentId;
    private String userName;
    private String classId;
    private ArrayList<QuizSessionStudent> sessions;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_session_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quiz Sessions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentId = getIntent().getExtras().getString("userId");
        userName = getIntent().getExtras().getString("userName");
        classId = getIntent().getExtras().getString("classId");

        TextView name = findViewById(R.id.studentNameInQuizSessionList);
        name.setText(userName+"'s Quiz Sessions:");

        getQuizSesions();
    }

    private void getQuizSesions() {
        QuizService quizService = new QuizService();
        quizService.getStudentQuizSessions(studentId, classId, new OnSuccessListener<ArrayList<QuizSessionStudent>>() {
            @Override
            public void onSuccess(ArrayList<QuizSessionStudent> quizSessionsStudent) {
                sessions = quizSessionsStudent;

                Collections.sort(sessions, new Comparator<QuizSessionStudent>() {
                    @Override
                    public int compare(QuizSessionStudent o1, QuizSessionStudent o2) {
                        return o2.getStartTime().compareTo(o1.getStartTime());
                    }
                });

                makeRV();
                makeSpinner();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz sessions "+e);
            }
        });
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.quizSessionListRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuizSessionStudentListAdapter(sessions, StudentQuizSessionListActivity.this, studentId);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.quizSessionStudentSpinner);
        spinner.setVisibility(View.VISIBLE);
        final String option1 = "Sort By: Date Descending";
        final String option2 = "Sort By: Date Ascending";
        ArrayList<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals(option1))
                {
                    Collections.sort(sessions, new Comparator<QuizSessionStudent>() {
                        @Override
                        public int compare(QuizSessionStudent o1, QuizSessionStudent o2) {
                            return o2.getStartTime().compareTo(o1.getStartTime());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(sessions, new Comparator<QuizSessionStudent>() {
                        @Override
                        public int compare(QuizSessionStudent o1, QuizSessionStudent o2) {
                            return o1.getStartTime().compareTo(o2.getStartTime());
                        }
                    });
                }
                makeRV();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
