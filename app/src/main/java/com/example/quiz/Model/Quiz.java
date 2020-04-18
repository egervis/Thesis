package com.example.quiz.Model;

import java.util.ArrayList;

public class Quiz {
    private String id;
    private String name;
    private String instructions;
    private String createdBy;
    private String category;
    private ArrayList<Question> questions;

    public Quiz(String id, String name, String instructions, String createdBy, String category) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.createdBy = createdBy;
        this.category = category;
        this.questions = null;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    //compute and return grade. parameters tbd
    public double computeGrade() {
        return 100;
    }
}