package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.QuestionViewActivity;
import com.example.quiz.Model.Question;
import com.example.quiz.R;

import java.util.ArrayList;

public class QuestionSelectAdapter extends RecyclerView.Adapter<QuestionSelectAdapter.QuestionSelectHolder> {
    private ArrayList<Question> questions;
    private Context context;
    private ArrayList<Integer> selected;
    public QuestionSelectAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
        selected = new ArrayList<>(0);
    }
    @NonNull
    @Override
    public QuestionSelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_question_select, parent, false);
        return new QuestionSelectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionSelectHolder holder, final int position) {
        holder.question.setText(questions.get(position).getText());
        holder.question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionViewActivity.class);
                intent.putExtra("questionId", questions.get(position).getId());
                context.startActivity(intent);
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selected.add(position);
                else
                    selected.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
    public ArrayList<Integer> getSelected() {
        return selected;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public class QuestionSelectHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView question;
        public QuestionSelectHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxQuestions);
            question = itemView.findViewById(R.id.questionSelectText);
        }
    }
}
