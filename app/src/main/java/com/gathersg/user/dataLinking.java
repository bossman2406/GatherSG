package com.gathersg.user;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class dataLinking  {

    private static final String TAG = "dataLinkingService";
    private Handler handler;
    private Runnable runnable;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private accountHelper account;
    private String uid;
    private String accountType;
    private eventHelper event;

    private void linkData() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        uid = currentUser.getUid();
        accountType = account.accountType;

        CollectionReference userRef = db.collection(accountType);
        userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // for each user
                        String documentId = document.getId();
                        CollectionReference myEventsRef = userRef.document(documentId).collection(account.KEY_MYEVENTS);
                        myEventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot doc : task.getResult()) {
                                        // for each myEvent name
                                        String docId = doc.getId();
                                        DocumentReference eventsRef = db.collection(event.KEY_EVENTS).document(docId);
                                        eventsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    String eventName = document.getString(event.KEY_EVENTNAME);
                                                    String eventDesc = document.getString(event.KEY_EVENTDESC);
                                                    String date = document.getString(event.KEY_EVENTDATE);
                                                    String organiser = document.getString(event.KEY_EVENTORG);
                                                    String locName = document.getString(event.KEY_EVENTLOCNAME);
                                                    Double lat = document.getDouble(event.KEY_LAT);
                                                    Double lon = document.getDouble(event.KEY_LON);
                                                    Blob image = document.getBlob(event.KEY_EVENTIMAGE);
                                                    String status = document.getString(event.KEY_EVENTSTATUS);
                                                    Long signup = document.getLong(event.KEY_EVENTSIGNUP);

                                                    DocumentReference myEventsNameRef = myEventsRef.document(docId);
                                                    Map<String, Object> myEvents = new HashMap<>();
                                                    myEvents.put(event.KEY_EVENTNAME, eventName);
                                                    myEvents.put(event.KEY_EVENTDESC, eventDesc);
                                                    myEvents.put(event.KEY_EVENTLOCNAME, locName);
                                                    myEvents.put(event.KEY_LAT, lat);
                                                    myEvents.put(event.KEY_LON, lon);
                                                    myEvents.put(event.KEY_EVENTDATE, date);
                                                    myEvents.put(event.KEY_EVENTORG, organiser);
                                                    myEvents.put(event.KEY_EVENTSTATUS, status);
                                                    myEvents.put(event.KEY_EVENTIMAGE, image);
                                                    myEvents.put(event.KEY_EVENTSIGNUP, signup);
                                                    myEventsNameRef.update(myEvents).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d(TAG, "Data linking success");
                                                        }
                                                    });
                                                } else {
                                                    Log.e(TAG, "Error getting events document", task.getException());
                                                }
                                            }
                                        });

                                    }
                                } else {
                                    Log.e(TAG, "Error getting myEvents documents", task.getException());
                                }
                            }
                        });

                    }
                } else {
                    Log.e(TAG, "Error getting user documents", task.getException());
                }
            }
        });
    }
}
