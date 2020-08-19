package com.example.quiz.Controller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.quiz.Controller.ConnectionManager.NearbyConnectionsManagerClassroom;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Callable;

public class JoinClassActivity extends AppCompatActivity {
    private String userId;
    private NearbyConnectionsManagerClassroom nearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Join Class");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        joinClass();
    }

    private void joinClass() {
        final Button join = findViewById(R.id.joinClassButton);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearby = new NearbyConnectionsManagerClassroom(JoinClassActivity.this, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        finish();
                        return null;
                    }
                });
                nearby.start();
                TextView textView = findViewById(R.id.joinClassSearchingTextView);
                textView.setVisibility(View.VISIBLE);

                Drawable drawable = join.getBackground();
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.parseColor("#AAAAAA"));
                join.setBackground(drawable);
                join.setClickable(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
