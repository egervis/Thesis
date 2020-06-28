package com.example.quiz.Controller;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CreateQuizActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<Question> q;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Create Quiz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getExtras().getString("id");

        getQuestions();
        setOnClicks();
    }

    private void getQuestions() {
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
                    q = questions;
                    makeRV();
                    makeSpinner();
                    enableSearch();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed to get questions "+e);
            }
        });
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.listOfCreatedQuestions);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuestionSelectAdapter(q, CreateQuizActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        createQuiz();
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.createQuizSpinner);
        spinner.setVisibility(View.VISIBLE);
        final String option1 = "Sort By: Category Ascending";
        final String option2 = "Sort By: Category Descending";
        ArrayList<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals(option1))
                {
                    Collections.sort(q, new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o1.getText().compareTo(o2.getText());
                        }
                    });

                    Collections.sort(q, new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o1.getCategory().compareTo(o2.getCategory());
                        }
                    });
                }
                else if(parent.getItemAtPosition(position).toString().equals(option2))
                {
                    Collections.sort(q, new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o2.getText().compareTo(o1.getText());
                        }
                    });

                    Collections.sort(q, new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o2.getCategory().compareTo(o1.getCategory());
                        }
                    });
                }
                makeRV();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void makeRV(ArrayList<Question> questionList) {
        recyclerView = findViewById(R.id.listOfCreatedQuestions);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuestionSelectAdapter(questionList, CreateQuizActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void enableSearch() {
        findViewById(R.id.searchTextInputCreateQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.searchButtonCreateQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.clearSearchButtonCreateQuiz).setVisibility(View.VISIBLE);

    }

    private void search(String text) {
        ArrayList<Integer> indexList = new ArrayList<>();
        for(int i=0; i<q.size(); i++)
            if(q.get(i).getText().toLowerCase().contains(text.toLowerCase()))
                indexList.add(i);
        ArrayList<Question> questionList = new ArrayList<>();
        for(Integer i:indexList)
            questionList.add(q.get(i));
        makeRV(questionList);
    }

    private void clearSearch() {
        makeRV();
    }

    private void setOnClicks() {
        ImageButton clearSearch = findViewById(R.id.clearSearchButtonCreateQuiz);
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = findViewById(R.id.searchTextInputCreateQuiz);
                searchText.setText("");
                clearSearch();
            }
        });

        final ImageButton search = findViewById(R.id.searchButtonCreateQuiz);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = findViewById(R.id.searchTextInputCreateQuiz);
                search(searchText.getText().toString());
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
