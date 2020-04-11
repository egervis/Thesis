package com.example.quiz.Model;

import java.util.ArrayList;
import java.util.Date;

//named Classroom to avoid confusion with keyword class
public class Classroom {
    private String id;
    private String name;
    private String sectionNumber;
    private Date startDate;
    private Date endDate;
    private String lastQuizSessionId;//can be null
    private ArrayList<User> students;
    private ArrayList<User> teachers;

    public Classroom(String id, String name, String sectionNumber, Date startDate, Date endDate, String lastQuizSessionId) {
        this.id = id;
        this.name = name;
        this.sectionNumber = sectionNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastQuizSessionId = lastQuizSessionId;
        this.students = null;
        this.teachers = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLastQuizSessionId() {
        return lastQuizSessionId;
    }

    public ArrayList<User> getStudents() {
        return students;
    }

    public ArrayList<User> getTeachers() {
        return teachers;
    }

    public void setStudents(ArrayList<User> students) {
        this.students = students;
    }

    public void setTeachers(ArrayList<User> teachers) {
        this.teachers = teachers;
    }
}
