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

import com.example.quiz.Controller.QuizViewActivity;
import com.example.quiz.Model.Quiz;
import com.example.quiz.R;

import java.util.ArrayList;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizHolder> {
    private ArrayList<Quiz> quizzes;
    private Context context;

    public QuizListAdapter(ArrayList<Quiz> quizzes, Context context) {
        this.quizzes = quizzes;
        this.context = context;
    }
    @NonNull
    @Override
    public QuizHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_quiz, parent, false);
        return new QuizHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizHolder holder, final int position) {
        holder.quizName.setText(quizzes.get(position).getName());
        holder.category.setText(quizzes.get(position).getCategory());
        holder.quizInfoHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuizViewActivity.class);
                intent.putExtra("quizId", quizzes.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class QuizHolder extends RecyclerView.ViewHolder {
        public TextView quizName;
        public TextView category;
        public LinearLayout quizInfoHolder;
        public QuizHolder(View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quizName1);
            category = itemView.findViewById(R.id.quizCategory1);
            quizInfoHolder = itemView.findViewById(R.id.quizInfoHolder1);
        }
    }
}
