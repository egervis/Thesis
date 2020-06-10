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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuizListActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<Quiz> q;

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
        getSupportActionBar().setTitle("Quizzes");

        setOnClicks();
        getQuizzes();
    }

    private void getQuizzes() {
        QuizService quizService = new QuizService();
        quizService.getQuizzes(userId, new OnSuccessListener<ArrayList<Quiz>>() {
            @Override
            public void onSuccess(ArrayList<Quiz> quizzes) {
                TextView text = findViewById(R.id.noQuizzesTeacher);
                if(quizzes.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
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
                System.out.println("Failed to get quizzes "+ e);
            }
        });
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.quizListTeacherRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuizListAdapter(q, QuizListActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.quizBankListSpinner);
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
}
