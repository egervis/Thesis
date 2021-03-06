package com.example.quiz.Model;

import java.util.Date;

public class QuizSession {
    private String id;
    private String classId;
    private String quizId;
    private Date startTime;
    private Date endTime;

    public QuizSession(String id, String classId, String quizId, Date startTime, Date endTime) {
        this.id = id;
        this.classId = classId;
        this.quizId = quizId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public String getQuizId() {
        return quizId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
