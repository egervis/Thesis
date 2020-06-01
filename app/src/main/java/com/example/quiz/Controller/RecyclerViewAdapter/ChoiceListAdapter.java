package com.example.quiz.Controller.RecyclerViewAdapter;

import android.content.Context;
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
    private Context context;
    public ChoiceListAdapter(ArrayList<Choice> choices, Context context) {
        this.choices = choices;
        this.context = context;
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
        holder.number.setText((position+1)+")  ");
        if(choices.get(position).isCorrect())
        {
            holder.choiceText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle_green_800_24dp,0);
        }
        else
        {
            holder.choiceText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_cancel_red_800_24dp,0);
        }
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public class ChoiceHolder extends RecyclerView.ViewHolder {
        public TextView choiceText;
        public TextView number;
        public ChoiceHolder(View itemView) {
            super(itemView);
            choiceText = itemView.findViewById(R.id.choiceTextinHolder);
            number = itemView.findViewById(R.id.choiceNumberInHolder);
        }
    }
}
