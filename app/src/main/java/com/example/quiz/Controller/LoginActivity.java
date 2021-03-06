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

import com.example.quiz.Model.User;
import com.example.quiz.R;

import com.example.quiz.Service.UserService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

//Testing imports
import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.StudentChoice;
import com.example.quiz.Service.QuizService;
import com.example.quiz.Service.ClassService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import android.Manifest;
import android.content.IntentFilter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import android.content.pm.PackageManager;
import android.os.Build;

public class LoginActivity extends AppCompatActivity {



    private static final int RC_SIGN_IN = 773;
    private static final String TAG = LoginActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        File cacheFile = new File(getCacheDir(), "class_code.tmp");
        System.out.println(cacheFile.delete());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            checkSignIn();
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "Request Code: " + requestCode);
        Log.v(TAG, "Result Code: " + resultCode);

        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Successfully logged in");
                checkSignIn();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.d(TAG, "User did not sign in");
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.d(TAG, "No internet connection");
                    return;
                }

                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void checkSignIn() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            final UserService userService = new UserService();

            userService.getUser(auth.getCurrentUser().getUid(), new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user == null) {
                        Log.v(TAG, "First time student signs in to this app");

                        final String userId, userFirstName, userLastName, userEmail;
                        String[] userName = auth.getCurrentUser().getDisplayName().split(" ");
                        userId = auth.getCurrentUser().getUid();
                        userEmail = auth.getCurrentUser().getEmail();

                        // TODO THIS DOESN'T TAKE INTO ACCOUNT AN OTHER RANDOM STUFF THEY MAY
                        // HAVE WRITTEN AS THEIR NAME
                        if (userName.length == 1) {
                            userFirstName = userName[0];
                            userLastName = "";
                        } else {
                            userFirstName = userName[0];
                            userLastName = userName[1];
                        }

                        userService.createUser(userId,
                                userFirstName,
                                userLastName,
                                userEmail,
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(LoginActivity.this, RoleSelectActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error creating the user");
                                    }
                                });

                    } else {
                        Intent intent = new Intent(LoginActivity.this, RoleSelectActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Couldn't check if student exists", e);
                    // todo tell them we couldn't sign them in and try again later?
                }
            });

        } else {
            // This should never happen
            NullPointerException exc = new NullPointerException("User signed in but null user");
            Log.e(TAG, "User attempted to sign in but auth wasn't initialized", exc);
            throw exc;
        }
    }









    /*

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;

    //Temp vars
    private EditText idInput;
    private static final int REQUEST_ENABLE_BT = 0;
    private BroadcastReceiver receiver;

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
        tempTesting();
        bluetoothTesting();

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

    public void bluetoothTesting()
    {
//        boolean hasBluetooth = true;
//        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            hasBluetooth = false;
//            System.out.println("no bluetooth");
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//
//        if(hasBluetooth)
//        {
//            if (!bluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//            if (bluetoothAdapter.isEnabled()) {
//                receiver = new BroadcastReceiver() {
//                    public void onReceive(Context context, Intent intent) {
//                        String action = intent.getAction();
//                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                            String deviceName = device.getName();
//                            String deviceHardwareAddress = device.getAddress();
//                            Log.v("bluetooth test", "Device with mac address " + deviceHardwareAddress + " found.");
//                        }
//                    }
//                };
//                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                registerReceiver(receiver, filter);
//                bluetoothAdapter.startDiscovery();
//                Timer mTimer = new Timer();
//                TimerTask mTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (bluetoothAdapter.isDiscovering()) {
//                                    // Cancel followed by a start searches for bluetooth devices again
//                                    // Not doing this would result in the search not being updated
//                                    bluetoothAdapter.cancelDiscovery();
//                                    bluetoothAdapter.startDiscovery();
//                                } else {
//                                    bluetoothAdapter.startDiscovery();
//                                }
//                            }
//                        });
//                    }
//                };
//
//                mTimer.schedule(mTimerTask, 0, 10000);
//
//            }
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[],
//                                           int[] grantResults) {
//        if(requestCode == 1)
//        {
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                Log.v("bluetooth test","location granted");
//            else
//            {
//                Toast toast = Toast.makeText(getApplicationContext(), "location denied, bluetooth inactive", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
    //used for manual testing until firebase is fully set up and input/blank checks are in place
    public void tempTesting()
    {
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
//        classService.createClass("class3", "1", start, end, "qfrZy6uEHLO150Ixi6F3",
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
//        QuizService quizService = new QuizService();
//        quizService.getQuestions("temp", new OnSuccessListener<ArrayList<Question>>() {
//            @Override
//            public void onSuccess(ArrayList<Question> questions) {
//                System.out.println(questions.size());
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("bad");
//            }
//        });
    }

    */
}
