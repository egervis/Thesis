package com.example.quiz.Controller.RecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Model.Choice;
import com.example.quiz.Model.StudentChoice;
import com.example.quiz.R;

import java.util.ArrayList;

public class ChoiceListQuizAdapter extends RecyclerView.Adapter<ChoiceListQuizAdapter.ChoiceHolder>{
    private ArrayList<Choice> choices;
    private String questionId;
    private String quizSessionId;
    private String studentId;
    private boolean isMulti;

    private ArrayList<StudentChoice> studentChoices;

    public ChoiceListQuizAdapter(ArrayList<Choice> choices, String questionId, String quizSessionId, String studentId, boolean isMulti) {
        this.choices = choices;
        this.questionId = questionId;
        this.quizSessionId = quizSessionId;
        this.studentId = studentId;
        this.isMulti = isMulti;
        studentChoices = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_choice_quiz, parent, false);
        return new ChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceHolder holder, final int position) {
        holder.choiceText.setText(choices.get(position).getChoiceText());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    StudentChoice studentChoice = new StudentChoice(studentId, quizSessionId, questionId, choices.get(position).getChoiceNum());
                    studentChoices.add(studentChoice);
                }
                else
                {
                    for(int i=0; i<studentChoices.size(); i++)
                    {
                        if(studentChoices.get(i).getChoiceNum() == choices.get(position).getChoiceNum())
                            studentChoices.remove(i);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public ArrayList<StudentChoice> getStudentChoices() {
        return studentChoices;
    }

    public boolean isValid() {
        if(studentChoices.size() == 0)
            return false;
        if(!isMulti && studentChoices.size() >1)
            return false;
        return true;
    }
    public class ChoiceHolder extends RecyclerView.ViewHolder {
        public TextView choiceText;
        public CheckBox checkBox;
        public ChoiceHolder(View itemView) {
            super(itemView);
            choiceText = itemView.findViewById(R.id.choiceTextTQ);
            checkBox = itemView.findViewById(R.id.checkBoxTQ);
        }
    }
}
