package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.Question;
import com.example.quiz.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    /** Gets a database instance*/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Parses a user retrieved from the database into a user object*/
    public final SnapshotParser<User> SNAPSHOTPARSER_USER = new SnapshotParser<User>() {
        @NonNull
        @Override
        public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new User(snapshot.getId(),
                    snapshot.getString("firstName"),
                    snapshot.getString("lastName"),
                    snapshot.getString("email"));
        }
    };

    /**
     * Creates a user with the provided information
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param onSuccessListener the callback if successful. Returns the user that was created.
     * @param onFailureListener the callback if there was a failure.
     */
    public void createUser(final String firstName, final String lastName, final String email,
                                  final OnSuccessListener<User> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        String f = firstName;
        String l = lastName;
        String e = email;
        if(f.equals(""))
            f = null;
        if(l.equals(""))
            l = null;
        if(e.equals(""))
            e = null;


        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", f);
        userMap.put("lastName", l);
        userMap.put("email", e);

        db.collection("user").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                User user = new User(documentReference.getId(), firstName, lastName, email);
                onSuccessListener.onSuccess(user);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Creates a user with the provided information
     * @param id the id of the user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param onSuccessListener the callback if successful. Returns the user that was created.
     * @param onFailureListener the callback if there was a failure.
     */
    public void createUser(final String id, final String firstName, final String lastName, final String email,
                           final OnSuccessListener<Void> onSuccessListener,
                           final OnFailureListener onFailureListener) {
        String f = firstName;
        String l = lastName;
        String e = email;
        if(f.equals(""))
            f = null;
        if(l.equals(""))
            l = null;
        if(e.equals(""))
            e = null;


        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", f);
        userMap.put("lastName", l);
        userMap.put("email", e);

        db.collection("user").document(id).set(userMap).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a user role based on the provided user id and class id
     * @param userId the id of the user
     * @param classId the id of the class
     * @param onSuccessListener the callback if successful. Returns the user role that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getUserRole(final String userId, final String classId,
                            final OnSuccessListener<String> onSuccessListener,
                            final OnFailureListener onFailureListener) {
        db.collection("user_class").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    if(documentSnapshot.getString("classId").equals(classId))
                    {
                        onSuccessListener.onSuccess(documentSnapshot.getString("role"));
                    }
                }
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a user based on the provided user id
     * @param userId the id of the user
     * @param onSuccessListener the callback if successful. Returns the user that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getUser(final String userId,
                           final OnSuccessListener<User> onSuccessListener,
                           final OnFailureListener onFailureListener) {
        String id = userId;
        if(userId.equals(""))
            id = null;

        db.collection("user").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    User user = SNAPSHOTPARSER_USER.parseSnapshot(documentSnapshot);
                    onSuccessListener.onSuccess(user);
                }
                else
                    onSuccessListener.onSuccess(null);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Gets a list of users based on the provided user ids
     * @param userIds the list of ids of the users
     * @param onSuccessListener the callback if successful. Returns the list of users that was retrieved.
     * @param onFailureListener the callback if there was a failure.
     */
    public void getUsers(final ArrayList<String> userIds,
                         final OnSuccessListener<ArrayList<User>> onSuccessListener,
                         final OnFailureListener onFailureListener) {
        ArrayList<String> uIds = userIds;

        if(userIds!=null)
        {
            for(String id:userIds)
            {
                if(id.equals(""))
                {
                    uIds = null;
                }

            }
            if(uIds.size() == 0)
                uIds.add("null");
        }

        ArrayList<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for(String s:userIds)
        {
            Task<QuerySnapshot> query = db.collection("user").whereEqualTo(FieldPath.documentId(), s).get();
            tasks.add(query);
        }
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                ArrayList<User> users = new ArrayList<>();
                for(Object doc:objects)
                {
                    QuerySnapshot docSnap = (QuerySnapshot) doc;
                    if(docSnap.getDocuments().size()!=0) {
                        User user = SNAPSHOTPARSER_USER.parseSnapshot(docSnap.getDocuments().get(0));
                        users.add(user);
                    }
                }
                onSuccessListener.onSuccess(users);
            }
        }).addOnFailureListener(onFailureListener);
    }

//    /**
//     * Gets a list of users based on the provided user ids
//     * @param userIds the list of ids of the users
//     * @param onSuccessListener the callback if successful. Returns the list of users that was retrieved.
//     * @param onFailureListener the callback if there was a failure.
//     */
//    public void getUsers(final ArrayList<String> userIds,
//                    final OnSuccessListener<ArrayList<User>> onSuccessListener,
//                    final OnFailureListener onFailureListener) {
//        ArrayList<String> uIds = userIds;
//
//        if(userIds!=null)
//        {
//            for(String id:userIds)
//            {
//                if(id.equals(""))
//                {
//                    uIds = null;
//                }
//
//            }
//            if(uIds.size() == 0)
//                uIds.add("null");
//        }
//
//        db.collection("user").whereIn(FieldPath.documentId(), uIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                ArrayList<User> users = new ArrayList<>();
//                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
//                {
//                    users.add(SNAPSHOTPARSER_USER.parseSnapshot(docSnap));
//                }
//                onSuccessListener.onSuccess(users);
//            }
//        }).addOnFailureListener(onFailureListener);
//    }
}
