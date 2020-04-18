package com.example.quiz.Controller;

import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionSelectAdapter;
import com.example.quiz.Model.Question;
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

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.R;

import java.util.ArrayList;

public class CreateQuizActivity extends AppCompatActivity {
    private String userId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getExtras().getString("id");

        makeRV();
    }
    private void makeRV() {
        recyclerView = findViewById(R.id.listOfCreatedQuestions);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        QuizService quizService = new QuizService();
        quizService.getQuestions(userId, new OnSuccessListener<ArrayList<Question>>() {
            @Override
            public void onSuccess(ArrayList<Question> questions) {
                if(questions.size()==0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "No questions to create quizzes with.", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
                else
                {
                    recyclerViewAdapter = new QuestionSelectAdapter(questions, CreateQuizActivity.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    createQuiz();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed to get questions "+e);
            }
        });
    }

    private void createQuiz() {
        final EditText quizNameText = findViewById(R.id.quizNameCreate);
        final EditText quizInstructionsText = findViewById(R.id.quizInstructionsCreate);
        final EditText quizCategoryText = findViewById(R.id.quizCategoryCreate);

        Button createQuiz = findViewById(R.id.createQuizButton);
        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = quizNameText.getText().toString();
                String instructions = quizInstructionsText.getText().toString();
                String category = quizCategoryText.getText().toString();

                boolean valid = true;
                if(name.equals("") || instructions.equals("") || category.equals(""))
                    valid = false;
                QuestionSelectAdapter adapter = (QuestionSelectAdapter) recyclerViewAdapter;
                if(adapter.getSelected().size()==0)
                    valid = false;

                if(valid)
                {
                    ArrayList<Question> questions = adapter.getQuestions();
                    ArrayList<Question> selected = new ArrayList<>();
                    for(Integer i:adapter.getSelected())
                        selected.add(questions.get(i));
                    QuizService quizService = new QuizService();
                    quizService.createQuiz(name, instructions, userId, category, selected, new OnSuccessListener<Quiz>() {
                        @Override
                        public void onSuccess(Quiz quiz) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Quiz created successfully.", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("failed to create quiz "+e);
                        }
                    });
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid or empty inputs.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
