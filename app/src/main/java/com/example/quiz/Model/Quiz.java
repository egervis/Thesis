package com.example.quiz.Model;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
            if(question.isMultiselect())
                if(multiselectCtr!=multiselectNum)
                    correct = false;
            if (correct)
                received += question.getPointsWorth();
        }System.out.println(received + "/" + total);
        double score = (received/total)*100;
        return score;
    }

    public void createCSV(ArrayList<Quiz> quizzes) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "Quizzes.csv";
        String filePath = baseDir + File.separator + "Download" + File.separator + fileName;
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        for(Quiz quiz:quizzes)
        {
            String[] quizName = {quiz.getName(), "Category: "+quiz.getCategory(), "Instructions: "+quiz.getInstructions()};
            writer.writeNext(quizName);
            for(Question question:quiz.getQuestions())
            {
                ArrayList<Choice> choices = question.getChoices();
                String correctChoices = "Correct Choices: ";
                String[] line = {"Category: "+question.getCategory(), question.getText(), "Points Worth: "+question.getPointsWorth()+"", correctChoices};
                List<String> list = Arrays.asList(line);
                list = new ArrayList<>(list);
                for(int i=0; i<choices.size(); i++)
                {
                    String s = (i+1)+") "+ choices.get(i).getChoiceText();
                    list.add(s);
                    if(choices.get(i).isCorrect())
                        correctChoices+=(i+1)+",";
                }
                correctChoices = correctChoices.substring(0, correctChoices.length()-1);
                line = list.toArray(new String[list.size()]);
                line[3] = correctChoices;
                writer.writeNext(line);
            }
            String[] blankLine={""};
            writer.writeNext(blankLine);
        }
        writer.close();
    }
}