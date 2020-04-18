package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.QuestionViewActivity;
import com.example.quiz.Model.Question;
import com.example.quiz.R;

import java.util.ArrayList;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QuestionHolder>{
    private ArrayList<Question> questions;
    private Context context;
    public QuestionListAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }
    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_question, parent, false);
        return new QuestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, final int position) {
        holder.questionText.setText(questions.get(position).getText());
        holder.category.setText(questions.get(position).getCategory());
        holder.questionInfoHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionViewActivity.class);
                intent.putExtra("questionId", questions.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuestionHolder extends RecyclerView.ViewHolder {
        public TextView questionText;
        public TextView category;
        public LinearLayout questionInfoHolder;
        public QuestionHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText1);
            category = itemView.findViewById(R.id.questionCategoy1);
            questionInfoHolder = itemView.findViewById(R.id.questionInfoHolder1);
        }
    }
}
