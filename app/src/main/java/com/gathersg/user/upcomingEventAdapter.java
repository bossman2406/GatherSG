package com.gathersg.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class upcomingEventAdapter extends RecyclerView.Adapter<upcomingEventAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> nameList;
    private final ArrayList<String> descList;
    private final ArrayList<String> locNameList;
    private final ArrayList<String> dateList;
    private final ArrayList<String> orgList;
    private final ArrayList<Double> latList;
    private final ArrayList<Double> lonList;
    private final ArrayList<Blob> imageList;

    public upcomingEventAdapter(Context context, ArrayList<String> nameList, ArrayList<String>
            descList, ArrayList<String> dateList,
                                ArrayList<String> orgList, ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                                ArrayList<Blob> imageList) {
        this.context = context;
        this.nameList = nameList;
        this.descList = descList;
        this.locNameList = locNameList;
        this.orgList = orgList;
        this.dateList = dateList;
        this.latList = latList;
        this.lonList = lonList;
        this.imageList = imageList;
        logArrayList("nameList", nameList);
        logArrayList("descList", descList);
        logArrayList("locNameList", locNameList);
        logArrayList("orgList", orgList);
        logArrayList("dateList", dateList);
        logImageArrayList("imageList", imageList);


    }

    private void logArrayList(String name, ArrayList<String> list) {
        Log.d("ArrayList", name + ": " + list.toString());
    }

    private void logImageArrayList(String name, ArrayList<Blob> list) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(name).append(": [");

        for (Blob blob : list) {
            if (blob != null) {
                logMessage.append("Image, ");
            } else {
                logMessage.append("null, ");
            }
        }

        logMessage.append("]");
        Log.d("ArrayList", logMessage.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.desc.setText(descList.get(position));
        holder.date.setText(dateList.get(position));
        holder.organiser.setText(orgList.get(position));
        holder.loc.setText(locNameList.get(position));
        Blob temp = imageList.get(position);

        if (temp != null) {
            byte[] imageData;
            imageData = temp.toBytes();
            Glide.with(context)
                    .load(imageData)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, date, organiser, loc;
        ImageView image;
        Button volunteerSignUp;
        accountHelper helper;
        eventHelper event;
        FirebaseAuth auth;
        FirebaseUser user;
        FirebaseFirestore db;

        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.eventCardName);
            desc = v.findViewById(R.id.eventCardDesc);
            date = v.findViewById(R.id.eventCardDate);
            organiser = v.findViewById(R.id.eventCardOrg);
            loc = v.findViewById(R.id.eventCardLoc);
            image = v.findViewById(R.id.eventCardImage);
            volunteerSignUp = v.findViewById(R.id.volunteerSignUpButton);
            int position = getAdapterPosition();
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String uid = currentUser.getUid();
            final boolean[] temp = {false};
            final boolean[] status = {false};
            db.collection(accountHelper.KEY_VOLUNTEER).document(uid).collection(accountHelper.KEY_MYEVENTS).document(nameList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot tempRef = task.getResult();
                        if (tempRef.exists()){
                            temp[0] = true;
                        } else temp[0] = false;
                    }
                }
            });
            db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()){
                       DocumentSnapshot signUp = task.getResult();
                       if(signUp.exists()){
                           String signUpStatus = signUp.getString(eventHelper.KEY_SIGNUPSTATUS);
                           if( signUpStatus.equals(eventHelper.KEY_CLOSE)){
                               status[0] = true;
;                           }else {
                               status[0] = false;
                           }

                       }
                   }
                }
            });



            if (accountHelper.KEY_ORGANISERS.equals(accountHelper.accountType)||temp.equals(true)||status.equals(true)) {
                volunteerSignUp.setVisibility(View.GONE);
            } else {
                volunteerSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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

                                        DocumentReference docRef = db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position));
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot eventDocument = task.getResult();
                                                    CollectionReference signupsCollection = docRef.collection(eventHelper.KEY_EVENTSIGNUP);

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
                                                                            CollectionReference signupsCollection = docRef.collection(eventHelper.KEY_EVENTSIGNUP);

                                                                            // Create a document for the current user's signup
                                                                            DocumentReference signupDocument = signupsCollection.document(uid);

                                                                            // Get volunteer details


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
                                                                                            DocumentReference documentRef = db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position));

                                                                                            db.runTransaction(new Transaction.Function<Void>() {
                                                                                                @Override
                                                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                                    DocumentSnapshot document = transaction.get(documentRef);

                                                                                                    // Check if the document exists
                                                                                                    if (document.exists()) {
                                                                                                        Log.d("taggg", "snapshot exist");
                                                                                                        // Get the current count value
                                                                                                        Long currentCount = document.getLong(eventHelper.KEY_EVENTSIGNUP);
                                                                                                        String eventName = document.getString(eventHelper.KEY_EVENTNAME);
                                                                                                        String eventDesc = document.getString(eventHelper.KEY_EVENTDESC);
                                                                                                        String date = document.getString(eventHelper.KEY_EVENTDATE);
                                                                                                        String organiser = document.getString(eventHelper.KEY_EVENTORG);
                                                                                                        String locName = document.getString(eventHelper.KEY_EVENTLOCNAME);
                                                                                                        Double lat = document.getDouble(eventHelper.KEY_LAT);
                                                                                                        Double lon = document.getDouble(eventHelper.KEY_LON);
                                                                                                        Blob image = document.getBlob(eventHelper.KEY_EVENTIMAGE);
                                                                                                        String status = document.getString(eventHelper.KEY_EVENTSTATUS);

                                                                                                        // Increment the count by 1 (or any other value)
                                                                                                        long newCount = (currentCount != null) ? currentCount + 1 : 1;

                                                                                                        // Update the count field in the document
                                                                                                        transaction.update(documentRef, eventHelper.KEY_EVENTSIGNUP, newCount);

//                                                                                                        // Update the count in the organiserRef collection
//                                                                                                        CollectionReference organiserRef = db.collection(helper.KEY_ORGANISERS).document(uid).collection(helper.KEY_MYEVENTS);
//                                                                                                        DocumentReference organiserDocRef = organiserRef.document(eventName); // Use the correct document ID
//                                                                                                        transaction.update(organiserDocRef, event.KEY_EVENTSIGNUP, newCount);


                                                                                                        DocumentReference volunteerRef = db.collection(accountHelper.KEY_VOLUNTEER).document(uid);
                                                                                                        CollectionReference volunteerEventsCollection = volunteerRef.collection(accountHelper.KEY_MYEVENTS);


                                                                                                        volunteerEventsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                if (task.isSuccessful()) {

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
                                                                                                                    volunteerEventsDocument.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void unused) {
                                                                                                                            volunteerSignUp.setVisibility(View.GONE);
                                                                                                                            Toast.makeText(context.getApplicationContext(), "Sign Up Successful.", Toast.LENGTH_SHORT).show();
                                                                                                                            Log.d("Firestore", "Volunteer signup added successfully");

                                                                                                                        }
                                                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                                                        @Override
                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                            Log.d("Fire", "FailedS Sign Up");
                                                                                                                        }
                                                                                                                    });
//                                                                                                                    } else {
//                                                                                                                        DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(eventName);
//
//                                                                                                                        Map<String, Object> myEvents = new HashMap<>();
//                                                                                                                        myEvents.put(event.KEY_EVENTNAME, eventName);
//                                                                                                                        myEvents.put(event.KEY_EVENTDESC, eventDesc);
//                                                                                                                        myEvents.put(event.KEY_EVENTLOCNAME, locName);
//                                                                                                                        myEvents.put(event.KEY_LAT, lat);
//                                                                                                                        myEvents.put(event.KEY_LON, lon);
//                                                                                                                        myEvents.put(event.KEY_EVENTDATE, date);
//                                                                                                                        myEvents.put(event.KEY_EVENTORG, organiser);
//                                                                                                                        myEvents.put(event.KEY_EVENTSTATUS, status);
//                                                                                                                        myEvents.put(event.KEY_EVENTIMAGE, image);
//                                                                                                                        Log.d("FIRESSSSS", "BEFORE SET EVENT");
//                                                                                                                        volunteerEventsDocument.update(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                                                            @Override
//                                                                                                                            public void onSuccess(Void unused) {
//                                                                                                                                volunteerSignUp.setVisibility(View.GONE);
//                                                                                                                                Toast.makeText(context.getApplicationContext(), "Sign Up Successful.", Toast.LENGTH_SHORT).show();
//                                                                                                                                Log.d("Firestore", "Volunteer signup added successfully");
//
//                                                                                                                            }
//                                                                                                                        }).addOnFailureListener(new OnFailureListener() {
//                                                                                                                            @Override
//                                                                                                                            public void onFailure(@NonNull Exception e) {
//                                                                                                                                Log.d("Fire", "FailedS Sign Up");
//                                                                                                                            }
//                                                                                                                        });


                                                                                                                }
                                                                                                            }

                                                                                                        });

//                                                                                                                Map<String, Object> myEvents = new HashMap<>();
//                                                                                                                myEvents.put(temp, temp);
//                                                                                                                volunteerEventsDocument.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                                                    @Override
//                                                                                                                    public void onSuccess(Void unused) {
//                                                                                                                        volunteerSignUp.setVisibility(View.GONE);
//                                                                                                                        Toast.makeText(context.getApplicationContext(), "Sign Up Successful.", Toast.LENGTH_SHORT).show();
//                                                                                                                        Log.d("Firestore", "Volunteer signup added successfully");
//
//                                                                                                                    }
//                                                                                                                }).addOnFailureListener(new OnFailureListener() {
//                                                                                                                    @Override
//                                                                                                                    public void onFailure(@NonNull Exception e) {
//                                                                                                                        Log.d("Fire", "FailedS Sign Up");
//                                                                                                                    }
//                                                                                                                });

                                                                                                    }
                                                                                                    return null;
                                                                                                }

                                                                                            });
                                                                                        }


                                                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            Log.d("Firestore", "Transaction successfully completed!");
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Log.e("Firestore", "Transaction failed: " + e);
                                                                                        }
                                                                                    });

                                                                            // Add dialog or something so like make button gone or toast

                                                                        }
                                                                    }
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Handle errors
                                                                    Log.e("Firestore", "Error adding volunteer signup", e);
                                                                }

                                                            });
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("Firestore", "Error getting event document", task.getException());
                                    }
                                }
                            }
                        });
                    }

                });

            }


        }
    }
}
