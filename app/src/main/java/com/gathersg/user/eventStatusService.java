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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class eventStatusService extends Service {
    private Handler handler;
    private Runnable runnable;
    FirebaseFirestore db;
    FirebaseAuth auth;
    accountHelper account;
    String uid;
    String accountType;
    eventHelper event;
    dateCompare dateCompare;

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
         uid = currentUser.getUid();
        accountType = account.accountType;



        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Perform your background task here
                checkData();

                // Schedule the next execution after a certain interval (e.g., 10 seconds)
                handler.postDelayed(this, 10000);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the service when it is explicitly requested
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not needed for this example
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the service when it is no longer needed
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    private void checkData() {
        // Implement your data checking logic here
        // This method will be called periodically in the background
        // For example, you can check for updates, synchronize data, etc.
        CollectionReference events = db.collection(event.KEY_EVENTS);
        events.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        String date = document.getString(event.KEY_EVENTDATE);
                        String name = document.getString(event.KEY_EVENTNAME);

                        if(date !=null) {
                            Date inputDate = dateCompare.parseDateString(date);
                            if(inputDate != null) {
                                Date currentDate = new Date();
                                // Compare the two dates
                                int comparisonResult = currentDate.compareTo(inputDate);
                                DocumentReference eventStatus = db.collection(event.KEY_EVENTS).document(name);
                                Map<String, Object> myEvents = new HashMap<>();
                                if (comparisonResult < 0) {
                                    myEvents.put(event.KEY_EVENTDATE, event.KEY_EVENTUPCOMING);
                                    Log.d("DATE", "Input date is in the future.");
                                } else {
                                    myEvents.put(event.KEY_EVENTDATE, event.KEY_EVENTCONCLUDED);
                                    Log.d("DATE", "Input date is in the past.");
                                }
                                eventStatus.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("DATE", "Input successful");

                                    }
                                });
                            }else {
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




        Toast.makeText(this, "Checking Data...", Toast.LENGTH_SHORT).show();
    }
}