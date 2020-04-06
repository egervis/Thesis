package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.Choice;
import com.example.quiz.Model.Question;
import com.example.quiz.Model.Quiz;
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

    public void getQuestions(final String createdBy,
                            final OnSuccessListener<ArrayList<Question>> onSuccessListener,
                            final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("question").whereEqualTo("createdBy", createdBy).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<Question> questions = new ArrayList<>();
                ArrayList<String> qIds = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    Question question = SNAPSHOTPARSER_QUESTION.parseSnapshot(docSnap);
                    questions.add(question);
                    qIds.add(question.getId());
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
                        onSuccessListener.onSuccess(questions);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

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
                quiz.setQuestions(questions);
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
}
