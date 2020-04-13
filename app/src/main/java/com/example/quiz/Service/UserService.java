package com.example.quiz.Service;

import androidx.annotation.NonNull;

import com.example.quiz.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public void createUser(final String firstName, final String lastName, final String email,
                                  final OnSuccessListener<User> onSuccessListener,
                                  final OnFailureListener onFailureListener)
    {
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

    public void getUser(final String userId,
                           final OnSuccessListener<User> onSuccessListener,
                           final OnFailureListener onFailureListener)
    {
        String id = userId;
        if(userId.equals(""))
            id = null;

        db.collection("user").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = SNAPSHOTPARSER_USER.parseSnapshot(documentSnapshot);
                onSuccessListener.onSuccess(user);
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void getUsers(final ArrayList<String> userIds,
                    final OnSuccessListener<ArrayList<User>> onSuccessListener,
                    final OnFailureListener onFailureListener)
    {
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
        }

        db.collection("user").whereIn(FieldPath.documentId(), uIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<User> users = new ArrayList<>();
                for(QueryDocumentSnapshot docSnap:queryDocumentSnapshots)
                {
                    users.add(SNAPSHOTPARSER_USER.parseSnapshot(docSnap));
                }
                onSuccessListener.onSuccess(users);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
