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

public class QuestionListStudentChoiceAdapter extends RecyclerView.Adapter<QuestionListStudentChoiceAdapter.QuestionStudentChoiceHolder> {
    private ArrayList<Question> questions;
    private ArrayList<StudentChoice> studentChoices;
    private Context context;

    public QuestionListStudentChoiceAdapter(ArrayList<Question> questions, ArrayList<StudentChoice> studentChoices, Context context) {
        this.questions = questions;
        this.studentChoices = studentChoices;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionStudentChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_question_student_choice, parent, false);
        QuestionStudentChoiceHolder holder = new QuestionStudentChoiceHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionStudentChoiceHolder holder, int position) {
        holder.text.setText(questions.get(position).getText());
        ArrayList<StudentChoice> selected = new ArrayList<>();
        for(StudentChoice sc:studentChoices)
            if(sc.getQuestionId().equals(questions.get(position).getId())) {
                selected.add(sc);
            }
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(new ChoiceListStudentChoiceAdapter(questions.get(position).getChoices(), selected));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuestionStudentChoiceHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public RecyclerView recyclerView;
        public QuestionStudentChoiceHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.questionTextInHolderSC);
            recyclerView = itemView.findViewById(R.id.choiceListInHolderSC);
        }
    }
}
