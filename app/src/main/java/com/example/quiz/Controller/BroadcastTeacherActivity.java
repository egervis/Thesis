package com.example.quiz.Controller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.quiz.Controller.ConnectionManager.NearbyConnectionsManagerStar;
import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.User;
import com.example.quiz.Service.ClassService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import static com.example.quiz.Controller.StringEncryption.StringEncryption.encryptString;

public class BroadcastTeacherActivity extends AppCompatActivity {

    private String userId;
    private String classId;
    private NearbyConnectionsManagerStar nearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_teacher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classId = getIntent().getExtras().getString("classId");

        getSupportActionBar().setTitle("Broadcast");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkCache();
    }

    private void checkCache() {
        File cacheFile = new File(getApplicationContext().getCacheDir(), "class_code_teacher.tmp");
        if(cacheFile.exists())
        {
            CheckBox checkBox = findViewById(R.id.checkBoxTeacherBroadcast);
            checkBox.setVisibility(View.VISIBLE);
        }
        setOnClicks();
    }

    private void setNearby(String serviceId, String password, ArrayList<String> userIds, HashMap<String, User> map) {
        TextView textView = findViewById(R.id.connectionStatusTeacher);
        this.nearby = new NearbyConnectionsManagerStar(getApplicationContext(), userId, password, serviceId, userIds, map, textView);
    }
    private void setClassCode() {
        final UUID uuid = UUID.randomUUID();
        try {
            String e = encryptString(uuid.toString());
            ClassService classService = new ClassService();
            classService.setClassCode(classId, Calendar.getInstance().getTime(), e, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    File cacheFile = new File(getApplicationContext().getCacheDir(), "class_code_teacher.tmp");
                    cacheFile.delete();
                    try{
                        File.createTempFile("class_code_teacher", null, getCacheDir());
                        cacheFile = new File(getApplicationContext().getCacheDir(), "class_code_teacher.tmp");
                        FileOutputStream fos = new FileOutputStream(cacheFile);
                        fos.write(uuid.toString().getBytes());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        getClassroom(uuid.toString());
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private void getClassroom(final String pass) {
        ClassService classService = new ClassService();
        classService.getClass(classId, new OnSuccessListener<Classroom>() {
            @Override
            public void onSuccess(Classroom classroom) {
                ArrayList<String> ids = new ArrayList<>();
                HashMap<String, User> map = new HashMap<>();
                for (User u:classroom.getStudents()) {
                    ids.add(u.getId());
                    map.put(u.getId(), u);
                }
                String code = pass+"~"+Calendar.getInstance().getTimeInMillis()+"~"+classId;
                setNearby(classId, code, ids, map);
                nearby.restart();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get class: "+ e);
            }
        });
    }

    private void setOnClicks() {
        final Button restart = findViewById(R.id.restartBroadcastingTeacherButton);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = findViewById(R.id.checkBoxTeacherBroadcast);
                if(checkBox.isChecked())
                {
                    File cacheFile = new File(getApplicationContext().getCacheDir(), "class_code_teacher.tmp");
                    try{
                        FileInputStream fis = new FileInputStream(cacheFile);
                        InputStreamReader inputStreamReader = new InputStreamReader(fis);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        final String code = reader.readLine();
                        getClassroom(code);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                    setClassCode();
                checkBox.setVisibility(View.GONE);


                Drawable drawable = restart.getBackground();
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.parseColor("#AAAAAA"));
                restart.setBackground(drawable);
                restart.setClickable(false);
                TextView textView = findViewById(R.id.connectionStatusTeacher);
                textView.setText("Broadcasting...");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
