package com.example.quiz.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public double computeGrade(ArrayList<StudentChoice> studentChoices) {
        double total = 0;
        double received = 0;
        for(Question question:questions)
        {
            total+=question.getPointsWorth();
            boolean correct = true;
            int multiselectCtr = 0;
            int multiselectNum = 0;
            for(Choice choice:question.getChoices())
            {
                if(choice.isCorrect())
                    multiselectNum++;
            }
            for(StudentChoice studentChoice:studentChoices)
            {
                if(question.isMultiselect())
                {
                    if(question.getId().equals(studentChoice.getQuestionId())) {
                        multiselectCtr++;
                        try {
                            if(!studentChoice.isCorrect(question.getChoices())) {
                                correct = false;
                            }
                        }
                        catch (Exception e) {
                            System.out.println(e);
                        }

                    }
                }
                else
                {
                    if(question.getId().equals(studentChoice.getQuestionId())) {
                        try {
                            if(!studentChoice.isCorrect(question.getChoices())) {
                                correct = false;
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
            if(multiselectCtr!=multiselectNum)
                correct = false;
            if (correct)
                received += question.getPointsWorth();
        }System.out.println(received + "/" + total);
        double score = (received/total)*100;
        return score;
    }
}