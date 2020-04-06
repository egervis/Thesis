package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.User;
import com.example.quiz.R;
import com.example.quiz.Service.QuizService;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;

    //Temp vars
    private EditText idInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);

        Button signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signUp();
            }
        });

        //Testing
//        Choice choice1 = new Choice(1,"c",true);
//        Choice choice2 = new Choice(2,"d",false);
//        ArrayList<Choice> lst = new ArrayList<>();
//        lst.add(choice1);
//        lst.add(choice2);
//        QuizService service = new QuizService();
//        service.createQuestion("q2", 1, "temp", "temp", false, lst,
//                new OnSuccessListener<Question>() {
//                    @Override
//                    public void onSuccess(Question question) {
//
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//        final QuizService service = new QuizService();
//        service.getQuestions("temp", new OnSuccessListener<ArrayList<Question>>() {
//            @Override
//            public void onSuccess(ArrayList<Question> questions) {
//
//                service.createQuiz("quiz2", "none", "temp", "temp", questions,
//                        new OnSuccessListener<Quiz>() {
//                            @Override
//                            public void onSuccess(Quiz quiz) {
//                                System.out.println(quiz.getQuestions().get(0).getChoices().get(1).getChoiceText());
//                            }
//                        }, new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService service = new QuizService();
//        service.getQuiz("kB4qjHJw2bsE14RYhJKD", new OnSuccessListener<Quiz>() {
//            @Override
//            public void onSuccess(Quiz quiz) {
//                System.out.println(quiz.getQuestions().get(0).getChoices().get(0).getChoiceText());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService service = new QuizService();
//        service.getQuizzes("temp", new OnSuccessListener<ArrayList<Quiz>>() {
//            @Override
//            public void onSuccess(ArrayList<Quiz> quizzes) {
//                System.out.println(quizzes.get(1).getQuestions().get(0).getChoices().get(0).getChoiceText());
//                System.out.println(quizzes.get(0).getName());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

        //Temp code
        idInput = findViewById(R.id.idInput);
        Button signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RoleSelectActivity.class);
                intent.putExtra("id", idInput.getText().toString());
                startActivity(intent);
                finish();
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
