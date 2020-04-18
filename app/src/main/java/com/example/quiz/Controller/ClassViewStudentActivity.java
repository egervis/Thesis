package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.User;
import com.example.quiz.Service.ClassService;
import com.example.quiz.Service.QuizService;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ClassViewStudentActivity extends AppCompatActivity {
    private String userId;
    private String classId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");
        classId = getIntent().getExtras().getString("classId");

        setOnClicks();
    }

    private void setOnClicks() {
        Button quizzes = findViewById(R.id.quizSessionMenuButtonStudent);
        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = new UserService();
                userService.getUser(userId, new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(getApplicationContext(), StudentQuizSessionListActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", user.getFirstName());
                        intent.putExtra("classId", classId);
                        startActivity(intent);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to get user "+ e);
                    }
                });
            }
        });

        Button takeQuiz = findViewById(R.id.takeQuizButton);
        takeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ClassService classService = new ClassService();
                classService.getClass(classId, new OnSuccessListener<Classroom>() {
                    @Override
                    public void onSuccess(Classroom classroom) {
                        if(classroom.getLastQuizSessionId()!=null) {
                            final QuizService quizService = new QuizService();
                            quizService.getQuizSession(classroom.getLastQuizSessionId(), new OnSuccessListener<QuizSession>() {
                                @Override
                                public void onSuccess(final QuizSession quizSession) {
                                    Calendar calendar = Calendar.getInstance();
                                    if(calendar.getTime().getTime()<quizSession.getEndTime().getTime())
                                    {
                                        boolean valid = checkBluetooth();

                                        if(valid)
                                        {
                                            quizService.getStudentQuizSessions(userId, classId, new OnSuccessListener<ArrayList<QuizSession>>() {
                                                @Override
                                                public void onSuccess(ArrayList<QuizSession> quizSessions) {
                                                    boolean quizTaken = false;
                                                    for(QuizSession q:quizSessions)
                                                    {
                                                        if(q.getId().equals(quizSession.getId()))
                                                            quizTaken = true;
                                                    }
                                                    if(quizSessions.size()==0 || !quizTaken) {
                                                        Intent intent = new Intent(ClassViewStudentActivity.this, TakeQuizActivity.class);
                                                        intent.putExtra("id", userId);
                                                        intent.putExtra("quizSessionId", quizSession.getId());
                                                        intent.putExtra("quizId", quizSession.getQuizId());
                                                        startActivity(intent);
                                                    }
                                                    else {
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Quiz already taken.", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                }
                                            }, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("Failed to get quiz sessions "+ e);
                                                }
                                            });
                                        }
                                        else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Out of quiz range.", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                    else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "No available quizzes.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Failed to get quiz session "+ e);
                                }
                            });
                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "No available quizzes.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to get class "+ e);
                    }
                });
            }
        });
    }

    private boolean checkBluetooth(){
        return true;
    }
}
