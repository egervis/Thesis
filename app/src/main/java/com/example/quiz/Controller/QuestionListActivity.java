package com.example.quiz.Controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.quiz.Controller.RecyclerViewAdapter.QuestionListAdapter;
import com.example.quiz.Model.Question;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
import android.widget.TextView;

import com.example.quiz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuestionListActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<Question> q;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //userId = getIntent().getExtras().getString("id");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSupportActionBar().setTitle("Questions");

        setOnClicks();
        getQuestions();
    }

    private void getQuestions() {
        QuizService quizService = new QuizService();
        quizService.getQuestions(userId, new OnSuccessListener<ArrayList<Question>>() {
            @Override
            public void onSuccess(ArrayList<Question> questions) {
                TextView text = findViewById(R.id.noQuestionsTeacher);
                if(questions.size() == 0)
                    text.setVisibility(View.VISIBLE);
                else
                    text.setVisibility(View.GONE);
                q = questions;

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

                makeRV();
                makeSpinner();
                enableSearch();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get questions "+ e);
            }
        });
    }

    private void makeRV() {
        recyclerView = findViewById(R.id.questionListBank);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuestionListAdapter(q, QuestionListActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void makeSpinner() {
        Spinner spinner = findViewById(R.id.questionBankListSpinner);
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
        recyclerView = findViewById(R.id.questionListBank);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new QuestionListAdapter(questionList, QuestionListActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void enableSearch() {
        findViewById(R.id.searchTextInputQuestionList).setVisibility(View.VISIBLE);
        findViewById(R.id.searchButtonQuestionList).setVisibility(View.VISIBLE);
        findViewById(R.id.clearSearchButtonQuestionList).setVisibility(View.VISIBLE);

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
        Button createClass = findViewById(R.id.createQuestionMenuButton);
        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, CreateQuestionActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button classMenu = findViewById(R.id.classMenuButtonQL);
        classMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, TeacherClassListActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        Button questionMenu = findViewById(R.id.questionMenuButtonQL);
        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        Button quizMenu = findViewById(R.id.quizMenuButtonQL);
        quizMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionListActivity.this, QuizListActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        ImageButton clearSearch = findViewById(R.id.clearSearchButtonQuestionList);
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = findViewById(R.id.searchTextInputQuestionList);
                searchText.setText("");
                clearSearch();
            }
        });

        final ImageButton search = findViewById(R.id.searchButtonQuestionList);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = findViewById(R.id.searchTextInputQuestionList);
                search(searchText.getText().toString());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getQuestions();
    }
}
