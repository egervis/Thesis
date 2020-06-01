package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionListQuizAdapter;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TakeQuizActivity extends AppCompatActivity {
    private String userId;
    private String quizSessionId;
    private String quizId;
    private long duration;
    private Date startTime;

    private long ctr;
    private CountDownTimer countDownTimer;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Quiz");

        userId = getIntent().getExtras().getString("id");
        quizSessionId = getIntent().getExtras().getString("quizSessionId");
        quizId = getIntent().getExtras().getString("quizId");
        duration = getIntent().getExtras().getLong("duration");

        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTime();

        makeRV();
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionRvTQ);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        QuizService quizService = new QuizService();
        quizService.getQuiz(quizId, new OnSuccessListener<Quiz>() {
            @Override
            public void onSuccess(Quiz quiz) {
                TextView textView = findViewById(R.id.quizInstructionsTQ);
                textView.setText("Instructions: "+quiz.getInstructions());
                recyclerViewAdapter = new QuestionListQuizAdapter(quiz.getQuestions(), quizSessionId, userId, TakeQuizActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
                makeTimer(quiz);
                submitButton(quiz);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz "+ e);
            }
        });
    }

    private void submitButton(final Quiz quiz) {
        Button button = findViewById(R.id.submitQuizButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(quiz);
            }
        });
    }

    private void submit(Quiz quiz) {
        QuestionListQuizAdapter a = (QuestionListQuizAdapter)recyclerViewAdapter;
        boolean valid = a.isValid();
        if(valid) {
            QuizService quizService = new QuizService();
            Calendar calendar = Calendar.getInstance();
            quizService.recordStudentChoices(userId, quizSessionId, quiz.computeGrade(), startTime, calendar.getTime(), a.getStudentChoices(), new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Quiz submitted.", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Failed to record choice "+ e);
                }
            });
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void makeTimer(final Quiz quiz) {
        ctr = duration/1000;
        countDownTimer = new CountDownTimer(duration, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long min = ctr/60;
                long sec = ctr%60;
                getSupportActionBar().setSubtitle(min+" min and "+sec+" sec left");
                ctr--;
                System.out.println(ctr);
            }

            @Override
            public void onFinish() {
                submit(quiz);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }
}
