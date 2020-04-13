package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
import com.example.quiz.Model.Quiz;
import com.example.quiz.Model.QuizSession;
import com.example.quiz.Model.StudentChoice;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuizService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public final SnapshotParser<Quiz> SNAPSHOTPARSER_QUIZ = new SnapshotParser<Quiz>() {
        @NonNull
        @Override
        public Quiz parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new Quiz(snapshot.getId(),
                    snapshot.getString("name"),
                    snapshot.getString("instructions"),
                    snapshot.getString("createdBy"),
                    snapshot.getString("category"));
        }
    };

    public final SnapshotParser<Question> SNAPSHOTPARSER_QUESTION = new SnapshotParser<Question>() {
        @NonNull
        @Override
        public Question parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new Question(snapshot.getId(),
                    snapshot.getString("text"),
                    snapshot.getDouble("pointsWorth"),
                    snapshot.getString("createdBy"),
                    snapshot.getString("category"),
                    snapshot.getBoolean("isMultiselect"));
        }
    };

    public final SnapshotParser<Choice> SNAPSHOTPARSER_CHOICE = new SnapshotParser<Choice>() {
        @NonNull
        @Override
        public Choice parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new Choice(snapshot.getString("questionId"),
                    snapshot.getDouble("choiceNum").intValue(),
                    snapshot.getString("choiceText"),
                    snapshot.getBoolean("isCorrect"));
        }
    };

    public final SnapshotParser<QuizSession> SNAPSHOTPARSER_QUIZ_SESSION = new SnapshotParser<QuizSession>() {
        @NonNull
        @Override
        public QuizSession parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new QuizSession(snapshot.getId(),
                    snapshot.getString("classId"),
                    snapshot.getString("quizId"),
                    snapshot.getDate("startTime"),
                    snapshot.getDate("endTime"));
        }
    };

    public final SnapshotParser<StudentChoice> SNAPSHOTPARSER_STUDENT_CHOICE = new SnapshotParser<StudentChoice>() {
        @NonNull
        @Override
        public StudentChoice parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new StudentChoice(snapshot.getString("studentId"),
                    snapshot.getString("quizSessionId"),
                    snapshot.getString("questionId"),
                    snapshot.getDouble("choiceNum").intValue());
        }
    };

    /**
     * Creates a question with the provided information
     * Note that an array list of choices must be provided
     * @param text the text for the question
     * @param pointsWorth the amount of points the question is worth
     * @param createdBy the user id of the creator of the question
     * @param category the category of the question
     * @param isMultiselect does the question have multiple answers
     * @param choices the choices for the question
     * @param onSuccessListener the callback if successful. Returns the question that was created.
     * @param onFailureListener the callback if there was a failure.
     */
    public void createQuestion(final String text, final double pointsWorth, final String createdBy,
                               final String category, final boolean isMultiselect, final ArrayList<Choice> choices,
                               final OnSuccessListener<Question> onSuccessListener,
                               final OnFailureListener onFailureListener) {

        //TODO: blank checks

        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("text", text);
        questionMap.put("pointsWorth", pointsWorth);
        questionMap.put("createdBy", createdBy);
        questionMap.put("category", category);
        questionMap.put("isMultiselect", isMultiselect);

        db.collection("question").add(questionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final Question question = new Question(documentReference.getId(),text,pointsWorth,createdBy,category,isMultiselect);
                WriteBatch batch = db.batch();
                for(Choice c: choices)
                {
                    c.setQuestionId(documentReference.getId());
                    DocumentReference choice = db.collection("choice").document();
                    Map<String, Object> choiceMap = new HashMap<>();
                    choiceMap.put("questionId", c.getQuestionId());
                    choiceMap.put("choiceNum", c.getChoiceNum());
                    choiceMap.put("choiceText", c.getChoiceText());
                    choiceMap.put("isCorrect", c.isCorrect());
                    batch.set(choice, choiceMap);
                }
                question.setChoices(choices);
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onSuccessListener.onSuccess(question);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a question based on the provided question id
     * @param questionId the id of the question
     * @param onSuccessListener the callback if successful. Returns the question that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getQuestion(final String questionId,
                            final OnSuccessListener<Question> onSuccessListener,
                            final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("question").document(questionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Question question = SNAPSHOTPARSER_QUESTION.parseSnapshot(documentSnapshot);
                db.collection("choice").whereEqualTo("questionId", questionId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Choice> choices = new ArrayList<>();
                        for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                        {
                            Choice choice = SNAPSHOTPARSER_CHOICE.parseSnapshot(snap);
                            choices.add(choice);
                        }
                        question.setChoices(choices);
                        onSuccessListener.onSuccess(question);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a list of questions (without their choices) based on the provided user id of the creator
     * @param createdBy the user id of the creator of the questions
     * @param onSuccessListener the callback if successful. Returns the list of questions that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getQuestions(final String createdBy,
                            final OnSuccessListener<ArrayList<Question>> onSuccessListener,
                            final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("question").whereEqualTo("createdBy", createdBy).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<Question> questions = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    Question question = SNAPSHOTPARSER_QUESTION.parseSnapshot(docSnap);
                    questions.add(question);
                }
                onSuccessListener.onSuccess(questions);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Creates a quiz with the provided information
     * Note that an array list of questions must be provided
     * @param name the name of the quiz
     * @param instructions the instructions for the quiz
     * @param createdBy the user id of the creator of the quiz
     * @param category the category of the quiz
     * @param questions the questions (choices for questions inside the arraylist are not mandatory) for the quiz.
     * @param onSuccessListener the callback if successful. Returns the quiz that was created.
     * @param onFailureListener the callback if there was a failure.
     */
    public void createQuiz(final String name, final String instructions, final String createdBy, final String category,
                           final ArrayList<Question> questions,
                           final OnSuccessListener<Quiz> onSuccessListener,
                           final OnFailureListener onFailureListener) {
        //TODO: blank checks

        Map<String, Object> quizMap = new HashMap<>();
        quizMap.put("name", name);
        quizMap.put("instructions", instructions);
        quizMap.put("createdBy", createdBy);
        quizMap.put("category", category);

        db.collection("quiz").add(quizMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final Quiz quiz = new Quiz(documentReference.getId(), name, instructions, createdBy, category);
                WriteBatch batch = db.batch();
                for(Question q:questions)
                {
                    DocumentReference entry = db.collection("quiz_question").document();
                    Map<String, Object> map = new HashMap<>();
                    map.put("quizId", documentReference.getId());
                    map.put("questionId", q.getId());
                    batch.set(entry, map);
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onSuccessListener.onSuccess(quiz);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a quiz based on the provided quiz id
     * @param quizId the id of the quiz
     * @param onSuccessListener the callback if successful. Returns the quiz that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getQuiz(final String quizId,
                            final OnSuccessListener<Quiz> onSuccessListener,
                            final OnFailureListener onFailureListener) {

        //TODO: blank checks

        db.collection("quiz").document(quizId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Quiz quiz = SNAPSHOTPARSER_QUIZ.parseSnapshot(documentSnapshot);
                db.collection("quiz_question").whereEqualTo("quizId", quiz.getId()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                final ArrayList<String> qIds = new ArrayList<>();
                                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                                {
                                    qIds.add(docSnap.getString("questionId"));
                                }
                                db.collection("question").whereIn(FieldPath.documentId(), qIds).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                final ArrayList<Question> questions = new ArrayList<>();
                                                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                                                {
                                                    Question question = SNAPSHOTPARSER_QUESTION.parseSnapshot(docSnap);
                                                    questions.add(question);
                                                }
                                                db.collection("choice").whereIn("questionId", qIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        ArrayList<Choice> choices = new ArrayList<>();
                                                        for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                                                        {
                                                            Choice choice = SNAPSHOTPARSER_CHOICE.parseSnapshot(snap);
                                                            choices.add(choice);
                                                        }
                                                        for(Question q:questions)
                                                        {
                                                            ArrayList<Choice> qChoices = new ArrayList<>();
                                                            for(Choice c:choices)
                                                            {
                                                                if(q.getId().equals(c.getQuestionId()))
                                                                {
                                                                    qChoices.add(c);
                                                                }
                                                            }
                                                            q.setChoices(qChoices);
                                                        }
                                                        quiz.setQuestions(questions);
                                                        onSuccessListener.onSuccess(quiz);
                                                    }
                                                }).addOnFailureListener(onFailureListener);
                                            }
                                        }).addOnFailureListener(onFailureListener);
                            }
                        }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a list of quizzes (without their questions) based on the provided user id of the creator
     * @param createdBy the user id of the creator of the quizzes
     * @param onSuccessListener the callback if successful. Returns the list of quizzes that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getQuizzes(final String createdBy,
                        final OnSuccessListener<ArrayList<Quiz>> onSuccessListener,
                        final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("quiz").whereEqualTo("createdBy", createdBy).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<Quiz> quizzes = new ArrayList<>();
                for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                {
                    quizzes.add(SNAPSHOTPARSER_QUIZ.parseSnapshot(snap));
                }
                onSuccessListener.onSuccess(quizzes);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void createQuizSession(final String classId, final String quizId, final Date startTime, final Date endTime,
                                  final OnSuccessListener<QuizSession> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        //TODO: blank checks

        Map<String, Object> quizMap = new HashMap<>();
        quizMap.put("classId", classId);
        quizMap.put("quizId", quizId);
        quizMap.put("startTime", startTime);
        quizMap.put("endTime", endTime);

        db.collection("quiz_session").add(quizMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final QuizSession quizSession = new QuizSession(documentReference.getId(), classId, quizId, startTime, endTime);
                administerQuiz(classId, documentReference.getId(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onSuccessListener.onSuccess(quizSession);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailureListener.onFailure(e);
                    }
                });
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void getQuizSession(final String quizSessionId,
                                  final OnSuccessListener<QuizSession> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("quiz_session").document(quizSessionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                QuizSession quizSession = SNAPSHOTPARSER_QUIZ_SESSION.parseSnapshot(documentSnapshot);
                onSuccessListener.onSuccess(quizSession);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void getStudentQuizSessions(final String studentId,
                               final OnSuccessListener<ArrayList<QuizSession>> onSuccessListener,
                               final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("quiz_session_student").whereEqualTo("studentId", studentId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<String> qIds = new ArrayList<>();
                final ArrayList<String> grades = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    qIds.add(docSnap.getString("quizSessionId"));
                    grades.add(docSnap.getDouble("grade")+"");
                }
                db.collection("quiz_session").whereIn(FieldPath.documentId(), qIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<QuizSession> quizSessions = new ArrayList<>();
                        for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                        {
                            String id = snap.getId();
                            QuizSession quizSession = SNAPSHOTPARSER_QUIZ_SESSION.parseSnapshot(snap);
                            quizSession.setGrade(Double.parseDouble(grades.get(qIds.indexOf(id))));
                            quizSessions.add(quizSession);
                        }
                        onSuccessListener.onSuccess(quizSessions);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void recordStudentChoices(final String studentId, final String quizSessionId, final double grade, final ArrayList<StudentChoice> studentChoices,
                                     final OnSuccessListener<Void> onSuccessListener,
                                     final OnFailureListener onFailureListener) {
        //TODO: blank checks

        WriteBatch batch = db.batch();

        DocumentReference quizSessionStudent = db.collection("quiz_session_student").document();
        Map<String, Object> quizSessionStudentMap = new HashMap<>();
        quizSessionStudentMap.put("studentId", studentId);
        quizSessionStudentMap.put("quizSessionId", quizSessionId);
        quizSessionStudentMap.put("grade", grade);
        batch.set(quizSessionStudent, quizSessionStudentMap);

        for(StudentChoice c: studentChoices)
        {
            DocumentReference studentChoice = db.collection("student_choice").document();
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", c.getStudentId());
            map.put("quizSessionId", c.getQuizSessionId());
            map.put("questionId", c.getQuestionId());
            map.put("choiceNum", c.getChoiceNum());
            batch.set(studentChoice, map);
        }

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onSuccessListener.onSuccess(aVoid);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void getStudentChoices(final String studentId, final String quizSessionId,
                                  final OnSuccessListener<ArrayList<StudentChoice>> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("student_choice").whereEqualTo("studentId", studentId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<StudentChoice> lst = new ArrayList<>(0);
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    if(docSnap.get("quizSessionId").equals(quizSessionId))
                    {
                        lst.add(SNAPSHOTPARSER_STUDENT_CHOICE.parseSnapshot(docSnap));
                    }
                }
                onSuccessListener.onSuccess(lst);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void getStudentChoice(final String studentId, final String quizSessionId, final String questionId,
                                  final OnSuccessListener<StudentChoice> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("student_choice").whereEqualTo("studentId", studentId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                StudentChoice studentChoice = null;
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    if(docSnap.get("quizSessionId").equals(quizSessionId) && docSnap.get("questionId").equals(questionId))
                    {
                        studentChoice = SNAPSHOTPARSER_STUDENT_CHOICE.parseSnapshot(docSnap);
                    }
                }
                onSuccessListener.onSuccess(studentChoice);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void administerQuiz(final String classId, final String quizSessionId,
                               final OnSuccessListener<Void> onSuccessListener,
                               final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("class").document(classId).update("lastQuizSessionId", quizSessionId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onSuccessListener.onSuccess(aVoid);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
