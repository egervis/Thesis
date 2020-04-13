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

import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.StudentChoice;
import com.example.quiz.Model.User;
import com.example.quiz.R;

import com.example.quiz.Service.ClassService;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

//Testing imports
import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Service.QuizService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

//        Testing
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
//
//        UserService userService = new UserService();
//        ArrayList<String> uIds = new ArrayList<>();
//        uIds.add("qfrZy6uEHLO150Ixi6F3");
//        uIds.add("QPcWHJCcnI9HSSHckdR2");
//        userService.getUsers(uIds, new OnSuccessListener<ArrayList<User>>() {
//            @Override
//            public void onSuccess(ArrayList<User> users) {
//                System.out.println(users.get(0).getFirstName());
//                System.out.println(users.get(1).getFirstName());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        ClassService classService = new ClassService();
//        long currentTime = System.currentTimeMillis();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(currentTime);
//        Date start = calendar.getTime();
//        calendar.add(Calendar.MONTH, 3);
//        final Date end = calendar.getTime();
//        classService.createClass("class2", "1", start, end, "qfrZy6uEHLO150Ixi6F3",
//                new OnSuccessListener<Classroom>() {
//                    @Override
//                    public void onSuccess(Classroom classroom) {
//                        System.out.println(end);
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//        ClassService classService = new ClassService();
//        classService.addUserToClass("tmE0EUHgwjICvq0Qubl6", "QPcWHJCcnI9HSSHckdR2", "student",
//                new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//        classService.addUserToClass("tmE0EUHgwjICvq0Qubl6", "XRDCGWl1CPUTJZ8aAWMm", "student",
//                new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//        ClassService classService = new ClassService();
//        classService.getClassWithUsers("Qc2BeOW6tqJ6gSIhdrPO", new OnSuccessListener<Classroom>() {
//            @Override
//            public void onSuccess(Classroom classroom) {
//                System.out.println(classroom.getStudents().get(0).getFirstName());
//                System.out.println(classroom.getStudents().get(1).getFirstName());
//                System.out.println(classroom.getTeachers().get(0).getFirstName());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        ClassService classService = new ClassService();
//        classService.getClassesWithoutUsers("QPcWHJCcnI9HSSHckdR2", new OnSuccessListener<ArrayList<Classroom>>() {
//            @Override
//            public void onSuccess(ArrayList<Classroom> classrooms) {
//                System.out.println(classrooms.get(0).getName());
//                System.out.println(classrooms.get(1).getName());
//                System.out.println(classrooms.get(0).getStudents());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService quizService = new QuizService();
//        long currentTime = System.currentTimeMillis();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(currentTime);
//        Date start = calendar.getTime();
//        calendar.add(Calendar.MINUTE, 30);
//        final Date end = calendar.getTime();
//        quizService.createQuizSession("Qc2BeOW6tqJ6gSIhdrPO", "6yzf3xAMMw2PUBT2eDjT", start, end,
//                new OnSuccessListener<QuizSession>() {
//                    @Override
//                    public void onSuccess(QuizSession quizSession) {
//                        System.out.println(quizSession.getEndTime());
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//        QuizService quizService = new QuizService();
//        quizService.getQuizSession("x8O2At9qMDtxEjPrgkJV", new OnSuccessListener<QuizSession>() {
//            @Override
//            public void onSuccess(QuizSession quizSession) {
//                System.out.println(quizSession.getEndTime());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        StudentChoice studentChoice = new StudentChoice("QPcWHJCcnI9HSSHckdR2", "zVqKOJb2IkU4GLuDuYYF", "lFBRXkyXVLxYandfL5Mg", 1);
//        ArrayList<StudentChoice> lst = new ArrayList<>();
//        lst.add(studentChoice);
//        QuizService quizService = new QuizService();
//        quizService.recordStudentChoices("QPcWHJCcnI9HSSHckdR2", "zVqKOJb2IkU4GLuDuYYF", 100, lst, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService quizService = new QuizService();
//        quizService.getStudentQuizSessions("QPcWHJCcnI9HSSHckdR2", new OnSuccessListener<ArrayList<QuizSession>>() {
//            @Override
//            public void onSuccess(ArrayList<QuizSession> quizSessions) {
//                System.out.println(quizSessions.get(0).getGrade());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        final QuizService quizService = new QuizService();
//        quizService.getQuestion("lFBRXkyXVLxYandfL5Mg", new OnSuccessListener<Question>() {
//            @Override
//            public void onSuccess(final Question question) {
//                quizService.getStudentChoice("QPcWHJCcnI9HSSHckdR2", "zVqKOJb2IkU4GLuDuYYF", "lFBRXkyXVLxYandfL5Mg", new OnSuccessListener<StudentChoice>() {
//                    @Override
//                    public void onSuccess(StudentChoice studentChoice) {
//                        try {
//                            System.out.println(studentChoice.isCorrect(question.getChoices()));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService quizService = new QuizService();
//        quizService.getStudentChoices("QPcWHJCcnI9HSSHckdR2", "zVqKOJb2IkU4GLuDuYYF", new OnSuccessListener<ArrayList<StudentChoice>>() {
//            @Override
//            public void onSuccess(ArrayList<StudentChoice> studentChoices) {
//                System.out.println(studentChoices.get(0).getChoiceNum());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        final QuizService quizService = new QuizService();
//        quizService.getQuestions("temp", new OnSuccessListener<ArrayList<Question>>() {
//            @Override
//            public void onSuccess(ArrayList<Question> questions) {
//                quizService.createQuiz("guiz3", "none", "temp", "temp", questions, new OnSuccessListener<Quiz>() {
//                    @Override
//                    public void onSuccess(Quiz quiz) {
//                        System.out.println(quiz.getName());
//                        System.out.println(quiz.getQuestions());
//                    }
//                }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        QuizService quizService = new QuizService();
//        quizService.getQuizzes("temp", new OnSuccessListener<ArrayList<Quiz>>() {
//            @Override
//            public void onSuccess(ArrayList<Quiz> quizzes) {
//                System.out.println(quizzes.get(0).getName());
//                System.out.println(quizzes.get(0).getQuestions());
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
