package com.example.quiz.Service;

import com.example.quiz.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        db.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                User user = new User(documentReference.getId(), firstName, lastName, email);
                onSuccessListener.onSuccess(user);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
