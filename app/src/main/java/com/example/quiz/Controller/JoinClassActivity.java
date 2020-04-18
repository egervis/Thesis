package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.R;

public class JoinClassActivity extends AppCompatActivity {
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");
        joinClass();
    }
    private void joinClass() {
        Button join = findViewById(R.id.joinClassButton);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.idClassJoinInput);
                boolean valid = true;

                if(text.getText().toString().equals(""))
                    valid = false;

                if(valid)
                {
                    ClassService classService = new ClassService();
                    classService.addUserToClass(text.getText().toString(), userId, "student", new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Added to class", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failed to join class "+ e);
                        }
                    });
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
