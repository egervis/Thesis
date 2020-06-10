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

import com.example.quiz.Controller.QuizSessionStatsActivity;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.R;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class QuizSessionListAdapter extends RecyclerView.Adapter<QuizSessionListAdapter.QuizSessionHolder> {
    private ArrayList<QuizSession> quizSessions;
    private Context context;

    public QuizSessionListAdapter(ArrayList<QuizSession> quizSessions, Context context) {
        this.quizSessions = quizSessions;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_quiz_session, parent, false);
        return new QuizSessionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuizSessionHolder holder, final int position) {
        final QuizService quizService = new QuizService();
        quizService.getQuizName(quizSessions.get(position).getQuizId(), new OnSuccessListener<String>() {
            @Override
            public void onSuccess(final String s) {
                holder.name.setText(s);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                holder.date.setText("Administered on "+formatter.format(quizSessions.get(position).getStartTime()));
                holder.info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, QuizSessionStatsActivity.class);
                        intent.putExtra("quizSessionId", quizSessions.get(position).getId());
                        intent.putExtra("quizName", s);
                        intent.putExtra("quizSessionStartTime", quizSessions.get(position).getStartTime());
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
        public TextView date;
        public LinearLayout info;
        public QuizSessionHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.quizSessionName);
            date = itemView.findViewById(R.id.quizSessionDate);
            info = itemView.findViewById(R.id.quizSessionInfo);
        }
    }

}
