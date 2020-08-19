package com.example.quiz.Controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
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

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdministerQuizActivity extends AppCompatActivity {
    private String userId;
    private String classId;
    private ArrayList<Quiz> q;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administer_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Administer Quiz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classId = getIntent().getExtras().getString("classId");

        getQuizzes();
        askWiFiPermissions();
    }

    private void askWiFiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            //    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    private void getQuizzes() {
        QuizService quizService = new QuizService();
        quizService.getQuizzes(userId, new OnSuccessListener<ArrayList<Quiz>>() {
            @Override
            public void onSuccess(ArrayList<Quiz> quizzes) {
                q = quizzes;

                Collections.sort(q, new Comparator<Quiz>() {
                    @Override
                    public int compare(Quiz o1, Quiz o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                Collections.sort(q, new Comparator<Quiz>() {
                    @Override
                    public int compare(Quiz o1, Quiz o2) {
                        return o1.getCategory().compareTo(o2.getCategory());
                    }
                });

                makeRV();
                makeSpinner();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quizzes " + e);
            }
        });
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.administerQuizListRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final AdministerQuizActivity activity = this;
        EditText editText = findViewById(R.id.durationInputAQ);
        recyclerViewAdapter = new QuizSelectAdapter(q, AdministerQuizActivity.this, classId, editText, activity, userId);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.administerQuizSpinner);
        spinner.setVisibility(View.VISIBLE);
        final String option1 = "Sort By: Category Ascending";
        final String option2 = "Sort By: Category Descending";
        final String option3 = "Sort By: Name";
        ArrayList<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        options.add(option3);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals(option1))
                {
                    Collections.sort(q, new Comparator<Quiz>() {
                        @Override
                        public int compare(Quiz o1, Quiz o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    Collections.sort(q, new Comparator<Quiz>() {
                        @Override
                        public int compare(Quiz o1, Quiz o2) {
                            return o1.getCategory().compareTo(o2.getCategory());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(q, new Comparator<Quiz>() {
                        @Override
                        public int compare(Quiz o1, Quiz o2) {
                            return o2.getName().compareTo(o1.getName());
                        }
                    });

                    Collections.sort(q, new Comparator<Quiz>() {
                        @Override
                        public int compare(Quiz o1, Quiz o2) {
                            return o2.getCategory().compareTo(o1.getCategory());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option3))
                {
                    Collections.sort(q, new Comparator<Quiz>() {
                        @Override
                        public int compare(Quiz o1, Quiz o2) {
                            return o1.getName().compareTo(o2.getName());
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
