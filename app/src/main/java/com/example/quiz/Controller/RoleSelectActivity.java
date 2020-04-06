package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.example.quiz.R;

public class RoleSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button teacherSelectButton = findViewById(R.id.teacherSelectButton);
        teacherSelectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectActivity.this, TeacherClassList.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

//        Button studentSelectButton = findViewById(R.id.studentSelectButton);
//        studentSelectButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(RoleSelectActivity.this, RoleSelectActivity.class);
//                intent.putExtra("id", getIntent().getStringExtra("id"));
//                startActivity(intent);
//                finish();
//            }
//        });
    }

}
