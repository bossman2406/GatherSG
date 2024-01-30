package com.gathersg.user.services;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class dataLinking {

    private static final String TAG = "dataLinkingService";
    private Handler handler;
    private Runnable runnable;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private accountHelper account;
    private String uid;
    private String accountType;
    private eventHelper event;

    public void linkData() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        uid = currentUser.getUid();
        accountType = accountHelper.accountType;

        CollectionReference userRef = db.collection(accountType);
        Log.d("Your_Tag", accountType);
        Log.d("Your Tag", "linking data");
        CollectionReference sourceCollectionRef = db.collection(eventHelper.KEY_EVENTS);

// Reference to the target collection
        CollectionReference targetCollectionRef = db.collection(accountType).document(uid).collection(accountHelper.KEY_MYEVENTS);

// Fetch all documents from the source collection
        targetCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        // Get a list of document IDs in the source collection
                        for (QueryDocumentSnapshot targetDocument : querySnapshot) {
                            String name = targetDocument.getString(eventHelper.KEY_EVENTNAME);
                            DocumentReference eventRef = db.collection(eventHelper.KEY_EVENTS).document(name);
                            getData(eventRef,name,uid);

                        }



                    } else {
                        // Handle the case where the source collection is empty
                        Log.d("document", "Source collection is empty");
                    }
                } else {
                    // Handle errors in fetching documents from the source collection
                    Log.e(TAG, "Error fetching documents from source collection", task.getException());
                }
            }
        });


    }
    public void getData(DocumentReference eventRef, String name,String uid){
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Long signUp = document.getLong(eventHelper.KEY_EVENTSIGNUP);
                        String eventStatus = document.getString(eventHelper.KEY_EVENTSTATUS);
                        String signUpStatus = document.getString(eventHelper.KEY_SIGNUPSTATUS);
                        DocumentReference targetCollectionRef = db.collection(accountType).document(uid).collection(accountHelper.KEY_MYEVENTS).document(name);
                        addData(targetCollectionRef,signUp,eventStatus,signUpStatus);
                    }

                }
            }
        });

    }
    public void addData(DocumentReference documentReference,Long signUp,String eventStatus,String signUpStatus){
        Map<String, Object> addData = new HashMap<>();
        addData.put(eventHelper.KEY_EVENTSIGNUP,signUp);
        addData.put(eventHelper.KEY_EVENTSTATUS,eventStatus);
        addData.put(eventHelper.KEY_SIGNUPSTATUS,signUpStatus);
        documentReference.update(addData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}

