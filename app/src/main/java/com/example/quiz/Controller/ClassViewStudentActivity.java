package com.example.quiz.Controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.QuizSessionStudent;
import com.example.quiz.Model.User;
import com.example.quiz.Service.BluetoothService;
import com.example.quiz.Service.ClassService;
import com.example.quiz.Service.QuizService;
import com.example.quiz.Service.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ClassViewStudentActivity extends AppCompatActivity {
    private boolean BLUETOOTH_CHECK_ON = false;//toggle off (false) for emulators without bluetooth

    private String userId;
    private String classId;
    private String className;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");
        classId = getIntent().getExtras().getString("classId");
        className = getIntent().getExtras().getString("className");

        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                if(BLUETOOTH_CHECK_ON)
                    checkBluetooth();
                else
                    startQuiz();
            }
        });
    }

    private void startQuiz() {
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
                                quizService.getStudentQuizSessions(userId, classId, new OnSuccessListener<ArrayList<QuizSessionStudent>>() {
                                    @Override
                                    public void onSuccess(ArrayList<QuizSessionStudent> quizSessions) {
                                        boolean quizTaken = false;
                                        for(QuizSessionStudent q:quizSessions)
                                        {
                                            if(q.getQuizSessionId().equals(quizSession.getId()))
                                                quizTaken = true;
                                        }
                                        if(quizSessions.size()==0 || !quizTaken) {
                                            long duration = quizSession.getEndTime().getTime() - quizSession.getStartTime().getTime();

                                            Intent intent = new Intent(ClassViewStudentActivity.this, TakeQuizActivity.class);
                                            intent.putExtra("id", userId);
                                            intent.putExtra("quizSessionId", quizSession.getId());
                                            intent.putExtra("quizId", quizSession.getQuizId());
                                            intent.putExtra("duration", duration);
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

    private void checkBluetooth(){
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(hasBluetooth(bluetoothAdapter))
        {
            askPermissions(bluetoothAdapter);
            getTeacherMacAddresses(bluetoothAdapter);
        }
    }

    private boolean hasBluetooth(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    private void askPermissions(BluetoothAdapter bluetoothAdapter) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void startBluetoothDiscovery(final BluetoothAdapter bluetoothAdapter, final BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
        final Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                bluetoothAdapter.cancelDiscovery();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Bluetooth Search Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                mTimer.cancel();
            }
        };
        mTimer.schedule(mTimerTask, 10000, 1);
    }

    private BroadcastReceiver createBluetoothReceiver(final ArrayList<String> macs) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Log.v("bluetooth test", "Device with mac address " + deviceHardwareAddress + " found.");

                    boolean valid = macAddressCheck(deviceHardwareAddress, macs);
                    if(valid)
                    {
                        startQuiz();
                    }
                }
            }
        };
        return receiver;
    }

    private void getTeacherMacAddresses(final BluetoothAdapter bluetoothAdapter) {
        ClassService classService = new ClassService();
        classService.getClass(classId, new OnSuccessListener<Classroom>() {
            @Override
            public void onSuccess(Classroom classroom) {
                ArrayList<String> ids = new ArrayList<>();
                for(User u:classroom.getTeachers())
                    ids.add(u.getId());
                BluetoothService bluetoothService = new BluetoothService();
                bluetoothService.getUsersMacAddresses(ids, new OnSuccessListener<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> strings) {
                        receiver = createBluetoothReceiver(strings);
                        Toast.makeText(getApplicationContext(), "Bluetooth Search Started", Toast.LENGTH_SHORT).show();
                        startBluetoothDiscovery(bluetoothAdapter, receiver);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to user macs "+ e);
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get class "+ e);
            }
        });
    }

    private boolean macAddressCheck(String mac, ArrayList<String> macs) {
        for(String s:macs)
            if(s.equals(mac))
                return true;
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.v("bluetooth","location granted");
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "location denied, bluetooth inactive", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)
            unregisterReceiver(receiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
