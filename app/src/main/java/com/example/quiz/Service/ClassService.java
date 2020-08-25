package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.Classroom;
import com.example.quiz.Model.User;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassService {
    /** Gets a database instance*/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Parses a class retrieved from the database into a classroom object*/
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

    /**
     * Creates a class with the provided information
     * @param name the name of the class
     * @param sectionNumber the section number of the class
     * @param startDate the start date of the class
     * @param endDate the end date of the class
     * @param teacherId the creator/teacher of the class
     * @param onSuccessListener the callback if successful. Returns the class (classroom) that was created.
     * @param onFailureListener the callback if there was a failure.
     */
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

    /**
     * Adds a user to a class with the provided information
     * @param classId the id of the class
     * @param userId the id of the user
     * @param role the role of the user (teacher,student)
     * @param onSuccessListener the callback if successful. Returns the id of the user class entry that was created.
     * @param onFailureListener the callback if there was a failure.
     */
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

    /**
     * Gets a class WITH (teachers and students are set) all its users based on the provided class id
     * @param classId the id of the class
     * @param onSuccessListener the callback if successful. Returns the class (classroom) that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void  getClass(final String classId,
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

    /**
     * Gets a list of classes WITHOUT (teachers and students are null) its users based on the provided user id and role of user
     * @param userId the user id
     * @param role the role of the user
     * @param onSuccessListener the callback if successful. Returns the list of classes (classrooms) that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void  getClasses(final String userId, final String role,
                            final OnSuccessListener<ArrayList<Classroom>> onSuccessListener,
                            final OnFailureListener onFailureListener) {
        //TODO: role/blank checks

        db.collection("user_class").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Task<QuerySnapshot>> tasks = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    if(docSnap.getString("role").equals(role)) {
                        Task<QuerySnapshot> query = db.collection("class").whereEqualTo(FieldPath.documentId(), docSnap.getString("classId")).get();
                        tasks.add(query);
                    }
                }
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        ArrayList<Classroom> classes = new ArrayList<>();
                        for(Object doc:objects)
                        {
                            QuerySnapshot docSnap = (QuerySnapshot) doc;
                            if(docSnap.getDocuments().size()!=0) {
                                Classroom classroom = SNAPSHOTPARSER_CLASS.parseSnapshot(docSnap.getDocuments().get(0));
                                classes.add(classroom);
                            }
                        }
                        onSuccessListener.onSuccess(classes);
                    }
                }).addOnFailureListener(onFailureListener);
            }
        }).addOnFailureListener(onFailureListener);
    }

//    /**
//     * Gets a list of classes WITHOUT (teachers and students are null) its users based on the provided user id and role of user
//     * @param userId the user id
//     * @param role the role of the user
//     * @param onSuccessListener the callback if successful. Returns the list of classes (classrooms) that was retrieved.
//     * @param onFailureListener the callback if there was a failure.
//     */
//    public void  getClasses(final String userId, final String role,
//                                   final OnSuccessListener<ArrayList<Classroom>> onSuccessListener,
//                                   final OnFailureListener onFailureListener) {
//        //TODO: role/blank checks
//
//        db.collection("user_class").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                ArrayList<String> classIds = new ArrayList<>();
//                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
//                {
//                    if(docSnap.getString("role").equals(role))
//                        classIds.add(docSnap.getString("classId"));
//                }
//                if(classIds.size() == 0)
//                    classIds.add("null");
//                db.collection("class").whereIn(FieldPath.documentId(), classIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        ArrayList<Classroom> classes = new ArrayList<>();
//                        for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
//                        {
//                            classes.add(SNAPSHOTPARSER_CLASS.parseSnapshot(snap));
//                        }
//                        onSuccessListener.onSuccess(classes);
//                    }
//                }).addOnFailureListener(onFailureListener);
//            }
//        }).addOnFailureListener(onFailureListener);
//    }

    public void getClassCode(final String classId,
                             final OnSuccessListener<String> onSuccessListener,
                             final OnFailureListener onFailureListener) {
        db.collection("class_code").whereEqualTo("classId", classId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String s = "";
                if(queryDocumentSnapshots.size()!=0) {
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        //Date date = snap.getDate("timestamp");
                        String code = snap.getString("code");
                        s = code;
                        //s = s + "~" + date.toString();
                        break;
                    }
                }
                onSuccessListener.onSuccess(s);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void setClassCode(final String classId, final Date timestamp, final String code,
                             final OnSuccessListener<Boolean> onSuccessListener,
                             final OnFailureListener onFailureListener) {

        db.collection("class_code").whereEqualTo("classId", classId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String id = "";
                if(queryDocumentSnapshots.size()!=0) {
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        id = snap.getId();
                        break;
                    }
                }
                Map<String, Object> classCodeMap = new HashMap<>();
                classCodeMap.put("classId", classId);
                classCodeMap.put("timestamp", timestamp);
                classCodeMap.put("code", code);
                if(id.equals(""))
                {
                    db.collection("class_code").add(classCodeMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            onSuccessListener.onSuccess(true);
                        }
                    }).addOnFailureListener(onFailureListener);
                }
                else {
                    db.collection("class_code").document(id).update(classCodeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onSuccessListener.onSuccess(true);
                        }
                    }).addOnFailureListener(onFailureListener);
                }
            }
        }).addOnFailureListener(onFailureListener);
    }
}