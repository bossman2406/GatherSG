package com.gathersg.user.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gathersg.user.calendar.dateCompare;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class eventStatusService {
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    FirebaseAuth auth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    accountHelper account;
    String uid;
    String accountType;
    eventHelper event;
    dateCompare dateCompare;
    private Handler handler;
    private Runnable runnable;






    public void checkData() {
        // Implement your data checking logic here
        // This method will be called periodically in the background
        // For example, you can check for updates, synchronize data, etc.

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
                            Date inputDate = com.gathersg.user.calendar.dateCompare.parseDateString(date);
                            if (inputDate != null) {
                                Date currentDate = new Date();
                                // Compare the two dates
                                int comparisonResult = currentDate.compareTo(inputDate);
                                DocumentReference eventStatus = db.collection(eventHelper.KEY_EVENTS).document(name);

                                DocumentReference myEventStatus = db.collection(accountType).document(uid).collection(accountHelper.KEY_MYEVENTS).document(name);

                                Map<String, Object> myEvents = new HashMap<>();
                                if (comparisonResult > 1) {
                                    myEvents.put(eventHelper.KEY_EVENTSTATUS, eventHelper.KEY_EVENTUPCOMING);
                                    Log.d("DATE", "Input date is in the future.");
                                } else {
                                    myEvents.put(eventHelper.KEY_EVENTSTATUS, eventHelper.KEY_EVENTCONCLUDED);
                                    Log.d("DATE", "Input date is in the past.");
                                }
                                myEventStatus.update(eventHelper.KEY_EVENTSTATUS, myEvents.get(eventHelper.KEY_EVENTSTATUS));

                                eventStatus.update(eventHelper.KEY_EVENTSTATUS, myEvents.get(eventHelper.KEY_EVENTSTATUS));
                                checkSignUp();

                            }
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
    public void checkSignUp() {
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
                        Long signUp = document.getLong(eventHelper.KEY_EVENTSIGNUP);
                        Long max = document.getLong(eventHelper.KEY_EVENTMAXPAX);
                        String name = document.getString(eventHelper.KEY_EVENTNAME);

                                // Compare the two dates
                                int comparisonResult = max.compareTo(signUp);
                                DocumentReference eventStatus = db.collection(eventHelper.KEY_EVENTS).document(name);
                                DocumentReference myEventStatus = db.collection(accountType).document(uid).collection(accountHelper.KEY_MYEVENTS).document(name);

                                Map<String, Object> myEvents = new HashMap<>();
                        if (comparisonResult < 1) {
                            myEvents.put(eventHelper.KEY_SIGNUPSTATUS, eventHelper.KEY_CLOSE);
                            Log.d("signup", "sign up  closed.");
                        } else {
                            myEvents.put(eventHelper.KEY_SIGNUPSTATUS, eventHelper.KEY_OPEN);
                            Log.d("sign up", "sign up open.");
                        }
                                eventStatus.update(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("signUp", "Input successful");

                                    }
                                });
                        myEventStatus.update(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("signUp", "Input successful");

                            }
                        });
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

    public static void signUpForEvent(String eventName, String uid, Context context, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(accountHelper.KEY_VOLUNTEER).document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {
                        String username = userDocument.getString(accountHelper.KEY_USERNAME);
                        String email = userDocument.getString(accountHelper.KEY_EMAIL);
                        Long number = userDocument.getLong(accountHelper.KEY_NUMBER);

                        DocumentReference eventRef = db.collection(eventHelper.KEY_EVENTS).document(eventName);
                        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot eventDocument = task.getResult();
                                    CollectionReference signupsCollection = eventRef.collection(eventHelper.KEY_EVENTSIGNUP);

                                    DocumentReference userSignupDocument = signupsCollection.document(uid);

                                    userSignupDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot userSignupDocument = task.getResult();
                                                if (userSignupDocument.exists()) {
                                                    // The user has already signed up
                                                    Toast.makeText(context.getApplicationContext(), "You have already signed up for this event.", Toast.LENGTH_SHORT).show();
                                                    Log.d("Firestore", "Volunteer has already signed up");
                                                } else {
                                                    // DocumentSnapshot contains the event data

                                                    // Create a subcollection for volunteer signups
                                                    CollectionReference signupsCollection = eventRef.collection(eventHelper.KEY_EVENTSIGNUP);

                                                    // Create a document for the current user's signup
                                                    DocumentReference signupDocument = signupsCollection.document(uid);

                                                    // Create a Map to hold volunteer signup details
                                                    Map<String, Object> signupData = new HashMap<>();
                                                    signupData.put(accountHelper.KEY_USERNAME, username);
                                                    signupData.put(accountHelper.KEY_EMAIL, email);
                                                    signupData.put(accountHelper.KEY_NUMBER, number);

                                                    // Set the volunteer signup data in the document
                                                    signupDocument.set(signupData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Volunteer signup added successfully
                                                                    updateEventDetails(eventRef, uid, eventName, context, view);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e("Firestore", "Error adding volunteer signup", e);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("Firestore", "Error getting event document", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.e("Firestore", "Error getting user document", task.getException());
                    }
                }
            }
        });
    }

    public static void updateEventDetails(DocumentReference eventRef, String uid, String eventName, Context context, View view) {
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Long currentCount = document.getLong(eventHelper.KEY_EVENTSIGNUP);
                        String eventDesc = document.getString(eventHelper.KEY_EVENTDESC);
                        String date = document.getString(eventHelper.KEY_EVENTDATE);
                        String organiser = document.getString(eventHelper.KEY_EVENTORG);
                        String locName = document.getString(eventHelper.KEY_EVENTLOCNAME);
                        Double lat = document.getDouble(eventHelper.KEY_LAT);
                        Double lon = document.getDouble(eventHelper.KEY_LON);
                        Blob image = document.getBlob(eventHelper.KEY_EVENTIMAGE);
                        String status = document.getString(eventHelper.KEY_EVENTSTATUS);

                        long newCount = (currentCount != null) ? currentCount + 1 : 1;
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> eventData = new HashMap<>();
                        eventData.put(eventHelper.KEY_EVENTSIGNUP, newCount);
                        DocumentReference orgMyEvents = db.collection(accountHelper.KEY_ORGANISERS).document(uid).collection(accountHelper.KEY_MYEVENTS).document(eventName);
                        orgMyEvents.update(eventData);

                        // Update the count field in the document
                        eventRef.update(eventData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Update successful
                                        updateVolunteerEvents(uid, eventName, eventDesc, date, organiser, locName, lat, lon, image, status, context, view);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Firestore", "Error updating event details", e);
                                    }
                                });
                    } else {
                        Log.e("Firestore", "Event document does not exist");
                    }
                } else {
                    Log.e("Firestore", "Error getting event document", task.getException());
                }
            }
        });
    }

    public static void updateVolunteerEvents(String uid, String eventName, String eventDesc, String date, String organiser, String locName, Double lat, Double lon, Blob image, String status, Context context, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference volunteerRef = db.collection(accountHelper.KEY_VOLUNTEER).document(uid);
        CollectionReference volunteerEventsCollection = volunteerRef.collection(accountHelper.KEY_MYEVENTS);

        DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(eventName);

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

        volunteerEventsDocument.set(myEvents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Volunteer signup added successfully
                        Toast.makeText(context.getApplicationContext(), "Sign Up Successful.", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "Volunteer signup added successfully");
                        // You can also update the UI or perform any additional actions after a successful sign-up
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error updating volunteer events", e);
                    }
                });
    }
    public void addData(EventListener<QuerySnapshot> listener) {
        CollectionReference myEvent = db.collection(accountHelper.KEY_ORGANISERS)
                .document(auth.getUid())
                .collection(accountHelper.KEY_MYEVENTS);

        myEvent.addSnapshotListener(listener);
    }

    public void addAttendance(String selectedEvent, String uidAttendance, OnSuccessListener<Void> successListener) {
        CollectionReference eventAttendance = db.collection(eventHelper.KEY_EVENTS)
                .document(auth.getUid())
                .collection(accountHelper.KEY_MYEVENTS)
                .document(selectedEvent)
                .collection(eventHelper.KEY_ATTENDANCE);

        DocumentReference attendance = eventAttendance.document(uidAttendance);

        Map<String, Object> attendees = new HashMap<>();
        attendees.put(accountHelper.KEY_USERNAME, uidAttendance);

        attendance.set(attendees)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(e -> Log.e("MYTAF", "Error adding attendance", e));
    }

}