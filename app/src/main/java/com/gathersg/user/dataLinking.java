package com.gathersg.user;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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

    protected void linkData() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        uid = currentUser.getUid();
        accountType = accountHelper.accountType;

        CollectionReference userRef = db.collection(accountType);
        Log.d("Your_Tag",accountType);
        Log.d("Your Tag","linking data");

                        CollectionReference myEventsRef = userRef.document(uid).collection(accountHelper.KEY_MYEVENTS);
                        myEventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot doc : task.getResult()) {
                                        // for each myEvent name
                                        String name = doc.getString(eventHelper.KEY_EVENTNAME);
                                        DocumentReference eventsRef = db.collection(eventHelper.KEY_EVENTS).document(name);
                                        eventsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    String eventName = document.getString(eventHelper.KEY_EVENTNAME);
                                                    String eventDesc = document.getString(eventHelper.KEY_EVENTDESC);
                                                    String date = document.getString(eventHelper.KEY_EVENTDATE);
                                                    String organiser = document.getString(eventHelper.KEY_EVENTORG);
                                                    String locName = document.getString(eventHelper.KEY_EVENTLOCNAME);
                                                    Double lat = document.getDouble(eventHelper.KEY_LAT);
                                                    Double lon = document.getDouble(eventHelper.KEY_LON);
                                                    Blob image = document.getBlob(eventHelper.KEY_EVENTIMAGE);
                                                    String status = document.getString(eventHelper.KEY_EVENTSTATUS);
                                                    Long signup = document.getLong(eventHelper.KEY_EVENTSIGNUP);

                                                    DocumentReference myEventsNameRef = myEventsRef.document(name);
                                                    Map<String, Object> myEvents = new HashMap<>();
                                                    myEvents.put(eventHelper.KEY_EVENTNAME, eventName);
                                                    myEvents.put(eventHelper.KEY_EVENTDESC, eventDesc);
                                                    myEvents.put(eventHelper.KEY_EVENTLOCNAME, locName);
                                                    myEvents.put(eventHelper.KEY_LAT, lat);
                                                    myEvents.put(eventHelper.KEY_LON, lon);
                                                    myEvents.put(eventHelper.KEY_EVENTDATE, date);
                                                    myEvents.put(eventHelper.KEY_EVENTORG, organiser);
                                                    myEvents.put(eventHelper.KEY_EVENTSTATUS, status);
                                                    myEvents.put(eventHelper.KEY_EVENTIMAGE, image);
                                                    myEvents.put(eventHelper.KEY_EVENTSIGNUP, signup);
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
                }

