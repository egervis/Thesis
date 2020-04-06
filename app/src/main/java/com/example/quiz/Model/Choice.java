package com.example.quiz.Model;

public class Choice {
    private String questionId;
    private int choiceNum;
    private String choiceText;
    private boolean isCorrect;

    public Choice(String questionId, int choiceNum, String choiceText, boolean isCorrect) {
        this.questionId = questionId;
        this.choiceNum = choiceNum;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    public Choice(int choiceNum, String choiceText, boolean isCorrect) {
        this.choiceNum = choiceNum;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
        this.questionId = null;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public int getChoiceNum() {
        return choiceNum;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
