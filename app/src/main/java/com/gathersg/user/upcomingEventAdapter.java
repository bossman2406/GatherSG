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
import java.util.List;
import java.util.Map;

public class upcomingEventAdapter extends RecyclerView.Adapter<upcomingEventAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> nameList,descList,locNameList,dateList,orgList;
    private ArrayList<Double> latList,lonList;
    private ArrayList<Blob> imageList;

    public upcomingEventAdapter(Context context, ArrayList<String> nameList, ArrayList<String>
                               descList, ArrayList<String> dateList,
                                ArrayList<String> orgList,ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                                ArrayList<Blob> imageList){
        this.context = context;
        this.nameList = nameList;
        this.descList =descList;
        this.locNameList = locNameList;
        this.orgList = orgList;
        this.dateList =dateList;
        this.latList =latList;
        this.lonList = lonList;
        this.imageList =imageList;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.event_cell,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,desc,date,organiser,loc;
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
            desc= v.findViewById(R.id.eventCardDesc);
            date =v.findViewById(R.id.eventCardDate);
            organiser =v.findViewById(R.id.eventCardOrg);
            loc = v.findViewById(R.id.eventCardLoc);
            image = v.findViewById(R.id.eventCardImage);
            volunteerSignUp = v.findViewById(R.id.volunteerSignUpButton);


            if(helper.KEY_ORGANISERS.equals(helper.accountType)){
                volunteerSignUp.setVisibility(View.GONE);
            }else{
                volunteerSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        auth = FirebaseAuth.getInstance();
                        db = FirebaseFirestore.getInstance();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        String uid = currentUser.getUid();

                        DocumentReference userRef = db.collection(helper.KEY_VOLUNTEER).document(uid);
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userDocument = task.getResult();
                                    if (userDocument.exists()) {
                                        String username = userDocument.getString(helper.KEY_USERNAME);
                                        String email = userDocument.getString(helper.KEY_EMAIL);
                                        Long number = userDocument.getLong(helper.KEY_NUMBER);

                                        DocumentReference docRef = db.collection(event.KEY_EVENTS).document(nameList.get(position));
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot eventDocument = task.getResult();
                                                    CollectionReference signupsCollection = docRef.collection(event.KEY_EVENTSIGNUP);

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
                                                                            CollectionReference signupsCollection = docRef.collection(event.KEY_EVENTSIGNUP);

                                                                            // Create a document for the current user's signup
                                                                            DocumentReference signupDocument = signupsCollection.document(uid);

                                                                            // Get volunteer details


                                                                            // Create a Map to hold volunteer signup details
                                                                            Map<String, Object> signupData = new HashMap<>();
                                                                            signupData.put(helper.KEY_USERNAME, username);
                                                                            signupData.put(helper.KEY_EMAIL, email);
                                                                            signupData.put(helper.KEY_NUMBER, number);

                                                                            // Set the volunteer signup data in the document
                                                                            signupDocument.set(signupData)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            // Volunteer signup added successfully
                                                                                            DocumentReference documentRef = db.collection(event.KEY_EVENTS).document(nameList.get(position));

                                                                                            db.runTransaction(new Transaction.Function<Void>() {
                                                                                                @Override
                                                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                                    DocumentSnapshot document = transaction.get(documentRef);

                                                                                                    // Check if the document exists
                                                                                                    if (document.exists()) {
                                                                                                        Log.d("taggg", "snapshot exist");
                                                                                                        // Get the current count value
                                                                                                        Long currentCount = document.getLong(event.KEY_EVENTSIGNUP);
                                                                                                        String eventName = document.getString(event.KEY_EVENTNAME);
                                                                                                        String eventDesc = document.getString(event.KEY_EVENTDESC);
                                                                                                        String date = document.getString(event.KEY_EVENTDATE);
                                                                                                        String organiser = document.getString(event.KEY_EVENTORG);
                                                                                                        String locName = document.getString(event.KEY_EVENTLOCNAME);
                                                                                                        Double lat = document.getDouble(event.KEY_LAT);
                                                                                                        Double lon = document.getDouble(event.KEY_LON);
                                                                                                        Blob image = document.getBlob(event.KEY_EVENTIMAGE);
                                                                                                        String status = document.getString(event.KEY_EVENTSTATUS);

                                                                                                        // Increment the count by 1 (or any other value)
                                                                                                        long newCount = (currentCount != null) ? currentCount + 1 : 1;

                                                                                                        // Update the count field in the document
                                                                                                        transaction.update(documentRef, event.KEY_EVENTSIGNUP, newCount);

//                                                                                                        // Update the count in the organiserRef collection
//                                                                                                        CollectionReference organiserRef = db.collection(helper.KEY_ORGANISERS).document(uid).collection(helper.KEY_MYEVENTS);
//                                                                                                        DocumentReference organiserDocRef = organiserRef.document(eventName); // Use the correct document ID
//                                                                                                        transaction.update(organiserDocRef, event.KEY_EVENTSIGNUP, newCount);


                                                                                                        DocumentReference volunteerRef = db.collection(helper.KEY_VOLUNTEER).document(uid);


                                                                                                        volunteerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    DocumentSnapshot userSignupDocument = task.getResult();
                                                                                                                    if (userSignupDocument.exists()) {
                                                                                                                        CollectionReference volunteerEventsCollection = volunteerRef.collection(helper.KEY_MYEVENTS);
                                                                                                                        DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(eventName);

                                                                                                                        Map<String, Object> myEvents = new HashMap<>();
                                                                                                                        myEvents.put(event.KEY_EVENTNAME, eventName);
                                                                                                                        myEvents.put(event.KEY_EVENTDESC, eventDesc);
                                                                                                                        myEvents.put(event.KEY_EVENTLOCNAME, locName);
                                                                                                                        myEvents.put(event.KEY_LAT, lat);
                                                                                                                        myEvents.put(event.KEY_LON, lon);
                                                                                                                        myEvents.put(event.KEY_EVENTDATE, date);
                                                                                                                        myEvents.put(event.KEY_EVENTORG, organiser);
                                                                                                                        myEvents.put(event.KEY_EVENTSTATUS,status);
                                                                                                                        myEvents.put(event.KEY_EVENTIMAGE,image);
                                                                                                                                volunteerEventsDocument.update(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                                                                                            }


                                                                                                                    } else {
                                                                                                                        CollectionReference volunteerEventsCollection = volunteerRef.collection(helper.KEY_MYEVENTS);
                                                                                                                        String temp = nameList.get(position);
                                                                                                                        DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(temp);
                                                                                                                        Map<String, Object> myEvents = new HashMap<>();
                                                                                                                        myEvents.put(temp, temp);
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

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}
