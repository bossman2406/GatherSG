package com.gathersg.user;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class eventStatusService {
    FirebaseFirestore db;
    FirebaseAuth auth;
    accountHelper account;
    String uid;
    String accountType;
    eventHelper event;
    dateCompare dateCompare;
    private Handler handler;
    private Runnable runnable;

    protected void checkData() {
        // Implement your data checking logic here
        // This method will be called periodically in the background
        // For example, you can check for updates, synchronize data, etc.

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        uid = currentUser.getUid();
        accountType = accountHelper.accountType;

        CollectionReference events = db.collection(eventHelper.KEY_EVENTS);
        events.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String date = document.getString(eventHelper.KEY_EVENTDATE);
                        String name = document.getString(eventHelper.KEY_EVENTNAME);

                        if (date != null) {
                            Date inputDate = com.gathersg.user.dateCompare.parseDateString(date);
                            if (inputDate != null) {
                                Date currentDate = new Date();
                                // Compare the two dates
                                int comparisonResult = currentDate.compareTo(inputDate);
                                DocumentReference eventStatus = db.collection(eventHelper.KEY_EVENTS).document(name);
                                Map<String, Object> myEvents = new HashMap<>();
                                if (comparisonResult < 0) {
                                    myEvents.put(eventHelper.KEY_EVENTDATE, eventHelper.KEY_EVENTUPCOMING);
                                    Log.d("DATE", "Input date is in the future.");
                                } else {
                                    myEvents.put(eventHelper.KEY_EVENTDATE, eventHelper.KEY_EVENTCONCLUDED);
                                    Log.d("DATE", "Input date is in the past.");
                                }
                                eventStatus.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("DATE", "Input successful");

                                    }
                                });
                            } else {
                                Log.e("DATE", "Failed to parse input date: " + date);
                            }
                        } else {
                            Log.e("DATE", "Date from Firestore is null");
                        }


                    }
                }

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}