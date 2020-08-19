package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.ChoiceListAdapter;
import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
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

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CreateQuestionActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<Choice> choices;
    private boolean correct;
    private boolean multiselect;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        choices = new ArrayList<>(0);
        checkSwitches();
        addChoice();
        makeQuestion();
    }
    private void checkSwitches() {
        Switch correctSwitch = findViewById(R.id.correctSwitch);
        correctSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                correct = isChecked;
            }
        });

        Switch multiselectSwitch = findViewById(R.id.multiselectSwitch);
        multiselectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                multiselect = isChecked;
            }
        });
    }
    private void addChoice() {
        Button addChoice = findViewById(R.id.addChoiceButton);
        addChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText choiceText = findViewById(R.id.choiceTextInput);
                if(!choiceText.getText().toString().equals(""))
                {
                    choices.add(new Choice(choices.size()+1, choiceText.getText().toString(), correct));
                    makeRV();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
    private void makeRV() {
        recyclerView = findViewById(R.id.listOfCreatedChoices);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new ChoiceListAdapter(choices, getApplicationContext());
        recyclerView.setAdapter(recyclerViewAdapter);
    }
    private void makeQuestion() {
        final EditText questionText = findViewById(R.id.questionTextCreate);
        final EditText categoryText = findViewById(R.id.questionCategoryCreate);
        final EditText subcategoryText = findViewById(R.id.questionSubcategoryCreate);
        final EditText pointsText = findViewById(R.id.questionPointsCreate);

        Button createQuestion = findViewById(R.id.createQuestionButton);
        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionText.getText().toString();
                String category = categoryText.getText().toString();
                String subcategory = subcategoryText.getText().toString();
                String points = pointsText.getText().toString();

                boolean valid = true;
                if(question.equals("") || category.equals("") || points.equals(""))
                    valid = false;
                int ctr = 0;
                for(Choice c:choices)
                {
                    if(c.isCorrect())
                        ctr++;
                }
                if(ctr>1 && !multiselect)
                    valid = false;
                if(ctr == 0)
                    valid = false;

                if(valid)
                {
                    QuizService quizService = new QuizService();
                    quizService.createQuestion(question, Double.parseDouble(points), userId, category+" - "+subcategory, multiselect, choices, new OnSuccessListener<Question>() {
                        @Override
                        public void onSuccess(Question question) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Question created successfully", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(getApplicationContext(), "Failed to create new question", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
