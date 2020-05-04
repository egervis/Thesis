package com.example.quiz.Model;

import java.util.Date;

public class QuizSessionStudent {
    private String quizSessionId;
    private String userId;
    private Date startTime;
    private Date endTime;
    private Double grade;

    public QuizSessionStudent(String quizSessionId, String userId, Date startTime, Date endTime, Double grade) {
        this.quizSessionId = quizSessionId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.grade = grade;
    }

    public String getQuizSessionId() {
        return quizSessionId;
    }

    public String getUserId() {
        return userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Double getGrade() {
        return grade;
    }
}
