package com.example.quiz.Service;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BluetoothService{
    /** Gets a database instance*/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUserMacAddress(final String userId, final String mac,
                                  final OnSuccessListener<String> onSuccessListener,
                                  final OnFailureListener onFailureListener) {

    }

    public void getUsersMacAddresses(final ArrayList<String> userIds,
                                     final OnSuccessListener<ArrayList<String>> onSuccessListener,
                                     final OnFailureListener onFailureListener) {
        db.collection("user_mac").whereIn("userId", userIds).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> classes = new ArrayList<>();
                for(QueryDocumentSnapshot snap:queryDocumentSnapshots)
                {
                    classes.add(snap.getString("macAddress"));
                }
                onSuccessListener.onSuccess(classes);
            }
        }).addOnFailureListener(onFailureListener);
    }
}
