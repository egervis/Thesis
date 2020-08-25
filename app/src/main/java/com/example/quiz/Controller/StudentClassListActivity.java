package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.ClassListStudentAdapter;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Service.ClassService;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentClassListActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<Classroom> c;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Classes");

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        joinClass();
        getClassrooms();
    }

    private void getClassrooms() {
        ClassService classService = new ClassService();
        classService.getClasses(userId, "student", new OnSuccessListener<ArrayList<Classroom>>() {
            @Override
            public void onSuccess(ArrayList<Classroom> classrooms) {
                TextView text = findViewById(R.id.noClassesStudent);
                if(classrooms.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
                c = classrooms;

                Collections.sort(c, new Comparator<Classroom>() {
                    @Override
                    public int compare(Classroom o1, Classroom o2) {
                        return o1.getName().compareTo(o2.getName());
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
        recyclerView = findViewById(R.id.classListStudentRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new ClassListStudentAdapter(c, StudentClassListActivity.this, userId);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.classListStudentSpinner);
        spinner.setVisibility(View.VISIBLE);
        final String option1 = "Sort By: Name Ascending";
        final String option2 = "Sort By: Name Descending";
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
                    Collections.sort(c, new Comparator<Classroom>() {
                        @Override
                        public int compare(Classroom o1, Classroom o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(c, new Comparator<Classroom>() {
                        @Override
                        public int compare(Classroom o1, Classroom o2) {
                            return o2.getName().compareTo(o1.getName());
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

    private void joinClass() {
        Button button = findViewById(R.id.joinClassMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentClassListActivity.this, JoinClassActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getClassrooms();
    }
}
