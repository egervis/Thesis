package com.example.quiz.Controller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.Model.Classroom;
import com.example.quiz.R;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateClassActivity extends AppCompatActivity {
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSupportActionBar().setTitle("Create Class");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        makeClass();
    }

    private void makeClass() {
        final EditText nameInput = findViewById(R.id.classNameInput);
        final EditText sectionInput = findViewById(R.id.classSectionInput);
        final EditText startInput = findViewById(R.id.classStartDateInput);
        final EditText endInput = findViewById(R.id.classEndDateInput);

        Button button = findViewById(R.id.createClassButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String section = sectionInput.getText().toString();
                String start = startInput.getText().toString();
                String end = endInput.getText().toString();

                boolean valid = true;
                if(name.equals("") || section.equals("") || start.equals("") || end.equals(""))
                    valid = false;

                if(valid)
                {
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date startDate = df.parse(start);
                        Date endDate = df.parse(end);

                        ClassService classService = new ClassService();
                        classService.createClass(name, section, startDate, endDate, userId, new OnSuccessListener<Classroom>() {
                            @Override
                            public void onSuccess(Classroom classroom) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Class created successfully", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast toast = Toast.makeText(getApplicationContext(), "Failed to create new class", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
