package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Controller.AdministerQuizActivity;
import com.example.quiz.Controller.BroadcastTeacherActivity;
import com.example.quiz.Controller.QuizViewActivity;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.R;
import com.example.quiz.Service.QuizService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QuizSelectAdapter extends RecyclerView.Adapter<QuizSelectAdapter.QuizSelectHolder> {
    private ArrayList<Quiz> quizzes;
    private Context context;
    private String classId;
    private EditText editText;
    private AdministerQuizActivity activity;
    private String userId;

    public QuizSelectAdapter(ArrayList<Quiz> quizzes, Context context, String classId, EditText editText, AdministerQuizActivity activity, String userId) {
        this.quizzes = quizzes;
        this.context = context;
        this.classId = classId;
        this.editText = editText;
        this.activity = activity;
        this.userId = userId;
    }

    @NonNull
    @Override
    public QuizSelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_quiz_select, parent, false);
        return new QuizSelectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizSelectHolder holder, final int position) {
        holder.textView.setText(quizzes.get(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuizViewActivity.class);
                intent.putExtra("quizId", quizzes.get(position).getId());
                context.startActivity(intent);
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if(editText.getText().toString().equals(""))
                    valid = false;
                int duration = 0;
                try {
                    duration = Integer.parseInt(editText.getText().toString());
                } catch (Exception e) {
                    valid = false;
                }
                if(!valid)
                {
                    Toast toast = Toast.makeText(context, "Invalid or empty inputs", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    final QuizService quizService = new QuizService();
                    Calendar calendar = Calendar.getInstance();
                    Date start = calendar.getTime();
                    calendar.add(Calendar.MINUTE, duration);
                    Date end = calendar.getTime();
                    quizService.createQuizSession(classId, quizzes.get(position).getId(), start, end, new OnSuccessListener<QuizSession>() {
                        @Override
                        public void onSuccess(QuizSession quizSession) {
                            quizService.administerQuiz(classId, quizSession.getId(), new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast toast = Toast.makeText(context, "Quiz Administered", Toast.LENGTH_SHORT);
                                    toast.show();
//                                    Intent intent = new Intent(context, BroadcastTeacherActivity.class);
//                                    intent.putExtra("id", userId);
//                                    intent.putExtra("classId", classId);
//                                    context.startActivity(intent);
//                                    activity.finish();
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Failed to administer quiz " + e);
                                }
                            });
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Failed to create quiz session " + e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class QuizSelectHolder extends RecyclerView.ViewHolder {
        public Button button;
        public TextView textView;
        public QuizSelectHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.administerQuizButton);
            textView = itemView.findViewById(R.id.quizNameInHolderQS);
        }
    }
}
