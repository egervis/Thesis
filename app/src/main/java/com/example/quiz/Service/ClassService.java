package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.User;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClassService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public final SnapshotParser<Classroom> SNAPSHOTPARSER_CLASS = new SnapshotParser<Classroom>() {
        @NonNull
        @Override
        public Classroom parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new Classroom(snapshot.getId(),
                    snapshot.getString("name"),
                    snapshot.getString("sectionNumber"),
                    snapshot.getDate("startDate"),
                    snapshot.getDate("endDate"),
                    snapshot.getString("lastQuizSessionId"));
        }
    };

    public void createClass(final String name, final String sectionNumber, final Date startDate, final Date endDate,
                           final String teacherId,
                           final OnSuccessListener<Classroom> onSuccessListener,
                           final OnFailureListener onFailureListener) {
        //TODO: blank checks

        Map<String, Object> classMap = new HashMap<>();
        classMap.put("name", name);
        classMap.put("sectionNumber", sectionNumber);
        classMap.put("startDate", startDate);
        classMap.put("endDate", endDate);
        classMap.put("lastQuizSessionId", null);

        db.collection("class").add(classMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final Classroom c = new Classroom(documentReference.getId(), name, sectionNumber, startDate, endDate, null);
                Map<String, Object> userClassMap = new HashMap<>();
                userClassMap.put("classId", documentReference.getId());
                userClassMap.put("userId", teacherId);
                userClassMap.put("role", "teacher");
                db.collection("user_class").add(userClassMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        UserService userService = new UserService();
                        userService.getUser(teacherId, new OnSuccessListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                ArrayList<User> teachers = new ArrayList<>();
                                teachers.add(user);
                                c.setTeachers(teachers);
                                onSuccessListener.onSuccess(c);
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
        }).addOnFailureListener(onFailureListener);
    }

    public void addUserToClass(final String classId, final String userId, final String role,
                               final OnSuccessListener<String> onSuccessListener,
                               final OnFailureListener onFailureListener) {
        //TODO: blank checks and role check

        Map<String, Object> userClassMap = new HashMap<>();
        userClassMap.put("classId", classId);
        userClassMap.put("userId", userId);
        userClassMap.put("role", role);
        db.collection("user_class").add(userClassMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                onSuccessListener.onSuccess(documentReference.getId());
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void  getClassWithUsers(final String classId,
                                   final OnSuccessListener<Classroom> onSuccessListener,
                                   final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("user_class").whereEqualTo("classId", classId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> studentIds = new ArrayList<>();
                final ArrayList<String> teacherIds = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    if(docSnap.getString("role").equals("student"))
                    {
                        studentIds.add(docSnap.getString("userId"));
                    }
                    if(docSnap.getString("role").equals("teacher"))
                    {
                        teacherIds.add(docSnap.getString("userId"));
                    }
                }
                final UserService userService = new UserService();
                userService.getUsers(studentIds, new OnSuccessListener<ArrayList<User>>() {
                    @Override
                    public void onSuccess(ArrayList<User> users) {
                        final ArrayList<User> students = users;
                        userService.getUsers(teacherIds, new OnSuccessListener<ArrayList<User>>() {
                            @Override
                            public void onSuccess(ArrayList<User> users) {
                                final ArrayList<User> teachers = users;
                                db.collection("class").document(classId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Classroom c = SNAPSHOTPARSER_CLASS.parseSnapshot(documentSnapshot);
                                        c.setStudents(students);
                                        c.setTeachers(teachers);
                                        onSuccessListener.onSuccess(c);
                                    }
                                }).addOnFailureListener(onFailureListener);
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onFailureListener.onFailure(e);
                            }
                        });
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

    public void  getClassesWithoutUsers(final String userId,
                                   final OnSuccessListener<ArrayList<Classroom>> onSuccessListener,
                                   final OnFailureListener onFailureListener) {
        //TODO: blank checks

        db.collection("user_class").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> classIds = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    classIds.add(docSnap.getString("classId"));
                }
                db.collection("class").whereIn(FieldPath.documentId(), classIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Classroom> classes = new ArrayList<>();
                        for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                        {
                            classes.add(SNAPSHOTPARSER_CLASS.parseSnapshot(snap));
                        }
                        onSuccessListener.onSuccess(classes);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
