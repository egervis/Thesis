package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.ConnectionManager.NearbyConnectionsManagerClassroom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class BroadcastClassTeacherActivity extends AppCompatActivity {

    private String userId;
    private String classId;
    private NearbyConnectionsManagerClassroom nearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_class_teacher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classId = getIntent().getExtras().getString("classId");

        getSupportActionBar().setTitle("Broadcast");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setOnClicks();

    }

    private void setOnClicks() {
        Button broadcast = findViewById(R.id.startBroadcastingClassTeacherButton);
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                String pin = "";
                int pinLength = 6;
                for(int i=0; i<pinLength; i++)
                    pin+=""+random.nextInt(10);
                nearby = new NearbyConnectionsManagerClassroom(getApplicationContext(), classId, pin);
                TextView textView = findViewById(R.id.classPinTextViewTeacher);
                textView.setText("Pin: "+pin);
                nearby.restart();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
