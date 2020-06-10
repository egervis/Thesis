package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuizSessionListAdapter;
import com.example.quiz.Model.QuizSession;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuizSessionListActivity extends AppCompatActivity {
    private String classId;
    private ArrayList<QuizSession> sessions;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_session_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quiz Sessions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        classId = getIntent().getExtras().getString("classId");

        getQuizSessions();
    }
    private void getQuizSessions() {
        QuizService quizService = new QuizService();
        quizService.getQuizSessions(classId, new OnSuccessListener<ArrayList<QuizSession>>() {
            @Override
            public void onSuccess(ArrayList<QuizSession> quizSessions) {
                Collections.sort(quizSessions, new Comparator<QuizSession>() {
                    @Override
                    public int compare(QuizSession o1, QuizSession o2) {
                        return o2.getStartTime().compareTo(o1.getStartTime());
                    }
                });
                sessions = quizSessions;
                makeRV();
                makeSpinner();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz sessions "+ e);
            }
        });
    }
    private void makeRV() {
        recyclerView = findViewById(R.id.quizSessionsTeacherViewRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuizSessionListAdapter(sessions, QuizSessionListActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.quizSessionSortSpinnerTeacherView);
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
                    Collections.sort(sessions, new Comparator<QuizSession>() {
                        @Override
                        public int compare(QuizSession o1, QuizSession o2) {
                            return o2.getStartTime().compareTo(o1.getStartTime());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(sessions, new Comparator<QuizSession>() {
                        @Override
                        public int compare(QuizSession o1, QuizSession o2) {
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
