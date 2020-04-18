package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Model.Question;
import com.example.quiz.Model.StudentChoice;
import com.example.quiz.R;

import java.util.ArrayList;

public class QuestionListQuizAdapter extends RecyclerView.Adapter<QuestionListQuizAdapter.QuestionQuizHolder> {
    private ArrayList<Question> questions;
    private String quizSessionId;
    private String studentId;
    private Context context;

    private ArrayList<ChoiceListQuizAdapter> adapters;

    public QuestionListQuizAdapter(ArrayList<Question> questions, String quizSessionId, String studentId, Context context) {
        this.questions = questions;
        this.quizSessionId = quizSessionId;
        this.studentId = studentId;
        this.context = context;
        adapters = new ArrayList<>();
    }

    @NonNull
    @Override
    public QuestionQuizHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_question_quiz, parent, false);
        QuestionQuizHolder holder = new QuestionQuizHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionQuizHolder holder, int position) {
        String multi = "";
        if(questions.get(position).isMultiselect())
            multi = " (This question has multiple answers)";
        holder.text.setText(questions.get(position).getText()+multi);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ChoiceListQuizAdapter a = new ChoiceListQuizAdapter(questions.get(position).getChoices(), questions.get(position).getId(), quizSessionId, studentId, questions.get(position).isMultiselect());
        adapters.add(a);
        holder.recyclerView.setAdapter(a);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public boolean isValid() {
        for(ChoiceListQuizAdapter a:adapters)
        {
            if(!a.isValid())
                return false;
        }
        return true;
    }

    public ArrayList<StudentChoice> getStudentChoices() {
        ArrayList<StudentChoice> lst = new ArrayList<>();
        for(ChoiceListQuizAdapter a:adapters)
        {
            lst.addAll(a.getStudentChoices());
        }
        return lst;
    }

    public class QuestionQuizHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public RecyclerView recyclerView;
        public QuestionQuizHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.questionTextTQ);
            recyclerView = itemView.findViewById(R.id.choiceRvTQ);
        }
    }
}
