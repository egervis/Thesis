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

import com.example.quiz.Controller.StudentChoiceViewActivity;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.R;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class QuizSessionListAdapter extends RecyclerView.Adapter<QuizSessionListAdapter.QuizSessionHolder> {
    private ArrayList<QuizSession> quizSessions;
    private Context context;
    private String userId;

    public QuizSessionListAdapter(ArrayList<QuizSession> quizSessions, Context context, String userId) {
        this.quizSessions = quizSessions;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public QuizSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_quiz_session, parent, false);
        return new QuizSessionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuizSessionHolder holder, final int position) {
        QuizService quizService = new QuizService();
        quizService.getQuizName(quizSessions.get(position).getQuizId(), new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                holder.name.setText(s);
                holder.grade.setText("Grade: "+quizSessions.get(position).getGrade());
                holder.info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      Intent intent = new Intent(context, StudentChoiceViewActivity.class);
                      intent.putExtra("userId", userId);
                      intent.putExtra("quizId", quizSessions.get(position).getQuizId());
                      intent.putExtra("quizGrade", quizSessions.get(position).getGrade());
                      intent.putExtra("quizSessionId", quizSessions.get(position).getId());
                      context.startActivity(intent);
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to get quiz name "+e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizSessions.size();
    }

    public class QuizSessionHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView grade;
        public LinearLayout info;
        public QuizSessionHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.quizNameSessionHolder);
            grade = itemView.findViewById(R.id.quizGradeSessionHolder);
            info = itemView.findViewById(R.id.quizSessionInfoHolder);
        }
    }
}
