package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.StudentListAdapter;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.User;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClassViewActivity extends AppCompatActivity {
    private String classId;
    private String userId;
    private Classroom c;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Roster");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classId = getIntent().getExtras().getString("classId");

        administerQuiz();
        quizSessionList();
        authenticateStudents();
        getClassroom();
    }
    private void getClassroom() {
        ClassService classService = new ClassService();
        classService.getClass(classId, new OnSuccessListener<Classroom>() {
            @Override
            public void onSuccess(Classroom classroom) {
                c = classroom;

                Collections.sort(c.getStudents(), new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        return o1.getFirstName().compareTo(o2.getFirstName());
                    }
                });

                makeRV();
                makeSpinner();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get classes "+ e);
            }
        });
    }
    private void makeRV() {
        recyclerView = findViewById(R.id.studentListTeacherRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new StudentListAdapter(c.getStudents(), ClassViewActivity.this, classId);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.rosterSpinner);
        spinner.setVisibility(View.VISIBLE);
        final String option1 = "Sort By: First Name";
        final String option2 = "Sort By: Last Name";
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
                    Collections.sort(c.getStudents(), new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            return o1.getFirstName().compareTo(o2.getFirstName());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(c.getStudents(), new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            return o1.getLastName().compareTo(o2.getLastName());
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

    private void administerQuiz() {
        Button button = findViewById(R.id.administerQuizMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdministerQuizActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
        });
    }

    private void quizSessionList() {
        Button button = findViewById(R.id.quizSessionListMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizSessionListActivity.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
        });
    }

    private void authenticateStudents() {
        Button button = findViewById(R.id.authenticateTeacherButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BroadcastTeacherActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
        });
    }

    private void addStudents() {
        Intent intent = new Intent(getApplicationContext(), BroadcastClassTeacherActivity.class);
        intent.putExtra("classId", classId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add_students:
                addStudents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getClassroom();
    }
}
