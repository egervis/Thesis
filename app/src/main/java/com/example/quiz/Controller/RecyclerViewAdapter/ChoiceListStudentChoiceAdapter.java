package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Model.Choice;
import com.example.quiz.Model.StudentChoice;
import com.example.quiz.R;

import java.util.ArrayList;

public class ChoiceListStudentChoiceAdapter extends RecyclerView.Adapter<ChoiceListStudentChoiceAdapter.ChoiceStudentChoiceHolder> {
    private ArrayList<Choice> choices;
    private ArrayList<StudentChoice> studentChoices;

    public ChoiceListStudentChoiceAdapter(ArrayList<Choice> choices, ArrayList<StudentChoice> studentChoices) {
        this.choices = choices;
        this.studentChoices = studentChoices;
    }

    @NonNull
    @Override
    public ChoiceStudentChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_choice_student_choice, parent, false);
        return new ChoiceStudentChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceStudentChoiceHolder holder, int position) {
        holder.number.setText("   "+(position+1)+")  ");
        holder.text.setText(choices.get(position).getChoiceText());
        if(choices.get(position).isCorrect())
        {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle_green_800_24dp,0);
        }
        else
        {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_cancel_red_800_24dp,0);
        }

        boolean sc = false;
        for(StudentChoice s:studentChoices)
        {
            if(choices.get(position).getChoiceNum()==s.getChoiceNum())
                sc = true;
        }
        if(sc)
            holder.student.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_blue_800_24dp,0,0,0);
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public class ChoiceStudentChoiceHolder extends RecyclerView.ViewHolder {
        public TextView number;

        public TextView text;
        public TextView student;
        public ChoiceStudentChoiceHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.choiceNumberInHolderSC);
            text = itemView.findViewById(R.id.choiceTextInHolderSC);
            student = itemView.findViewById(R.id.choiceIsStudentInHolderSC);
        }
    }
}
