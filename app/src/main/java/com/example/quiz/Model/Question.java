package com.example.quiz.Model;

import java.util.ArrayList;

public class Question {
    private String id;
    private String text;
    private Double pointsWorth;
    private String createdBy;
    private String category;
    private boolean isMultiselect;
    private ArrayList<Choice> choices;


    public Question(String id, String text, Double pointsWorth, String createdBy, String category, boolean isMultiselect) {
        this.id = id;
        this.text = text;
        this.pointsWorth = pointsWorth;
        this.createdBy = createdBy;
        this.category = category;
        this.isMultiselect = isMultiselect;
        this.choices = null;
    }

    public void setChoices(ArrayList<Choice> choices) {
        this.choices = choices;
    }


    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Double getPointsWorth() {
        return pointsWorth;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCategory() {
        return category;
    }

    public boolean isMultiselect() {
        return isMultiselect;
    }

    public ArrayList<Choice> getChoices() {
        return choices;
    }
}
