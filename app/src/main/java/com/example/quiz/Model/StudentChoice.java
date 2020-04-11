package com.example.quiz.Model;

import java.util.ArrayList;

public class StudentChoice {
    private String studentId;
    private String quizSessionId;
    private String questionId;
    private int choiceNum;

    public StudentChoice(String studentId, String quizSessionId, String questionId, int choiceNum) {
        this.studentId = studentId;
        this.quizSessionId = quizSessionId;
        this.questionId = questionId;
        this.choiceNum = choiceNum;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getQuizSessionId() {
        return quizSessionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public int getChoiceNum() {
        return choiceNum;
    }

    public boolean isCorrect(ArrayList<Choice> choices) throws Exception {
        int i = 0;
        for(Choice choice:choices)
        {
            if(choice.getQuestionId().equals(this.questionId))
            {
                if(choice.getChoiceNum() == this.choiceNum)
                    return choice.isCorrect();
                i++;
            }
            else
                throw new ChoiceNotInQuestionException("Choice at index " +i+ " is not from the same question.");
        }
        throw new ChoiceNumOutOfBoundsException("The choice num chosen by the student was not in the list of provided choices.");
    }

    class ChoiceNotInQuestionException extends Exception
    {
        public ChoiceNotInQuestionException(String s)
        {
            super(s);
        }
    }

    class ChoiceNumOutOfBoundsException extends Exception
    {
        public ChoiceNumOutOfBoundsException(String s)
        {
            super(s);
        }
    }
}
