package com.example.quiz.Controller.RecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.Model.Choice;
import com.example.quiz.R;

import java.util.ArrayList;

public class ChoiceListAdapter extends RecyclerView.Adapter<ChoiceListAdapter.ChoiceHolder>{
    private ArrayList<Choice> choices;
    public ChoiceListAdapter(ArrayList<Choice> choices) {
        this.choices = choices;
    }
    @NonNull
    @Override
    public ChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_choice, parent, false);
        return new ChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceHolder holder, int position) {
        holder.choiceText.setText(choices.get(position).getChoiceText());
        holder.correct.setText("Is Correct: "+choices.get(position).isCorrect());
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public class ChoiceHolder extends RecyclerView.ViewHolder {
        public TextView choiceText;
        public TextView correct;
        public ChoiceHolder(View itemView) {
            super(itemView);
            choiceText = itemView.findViewById(R.id.choiceTextinHolder);
            correct = itemView.findViewById(R.id.choiceCorrectInHolder);
        }
    }
}
