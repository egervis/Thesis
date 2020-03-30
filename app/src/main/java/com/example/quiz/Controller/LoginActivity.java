package com.example.quiz.Controller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.Model.User;
import com.example.quiz.R;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoginActivity extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);

        Button exportAttendance = findViewById(R.id.signUpButton);
        exportAttendance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp()
    {
        boolean valid = true;
        String first = firstNameInput.getText().toString();
        String last = lastNameInput.getText().toString();
        String email = emailInput.getText().toString();
        if(first.equals(""))
            valid = false;
        if(last.equals(""))
            valid = false;
        if(email.equals(""))
            valid = false;
        if(valid)
        {
            UserService userService = new UserService();
            userService.createUser(first, last, email, new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Hello "+user.getFirstName(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to create new user", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Inputs", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
