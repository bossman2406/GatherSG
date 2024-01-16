package com.gathersg.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class eventOrganiserAdd extends AppCompatActivity {

    private EditText eventName, eventDesc, eventLocName, eventMaxPax;
    private String eventDate;
    private MaterialCardView dateCard, locCard;
    private double eventLatitude, eventLongitude;
    private int eventMaxParticipants;
    FirebaseAuth auth;
    private byte[] imageData;
    private Button eventPublishButton;
    private TextView viewPublishedEvents, eventPublishDate;
    private FirebaseFirestore db;
    eventHelper helper;
    accountHelper account;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organiser_add);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("imageData")) {
            imageData = intent.getByteArrayExtra("imageData");
            // Now you have the data in the variable receivedData
        }
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        eventName = findViewById(R.id.eventPublishName);
        eventDesc = findViewById(R.id.eventPublishDesc);
        eventLocName = findViewById(R.id.eventPublishLocName);
        eventMaxPax = findViewById(R.id.eventPublishMaxPax);
        eventPublishButton = findViewById(R.id.eventPublishButton);
        dateCard = findViewById(R.id.selectDateButton);
        locCard = findViewById(R.id.getLocationButton);
        eventPublishDate = findViewById(R.id.eventPublishDate);
        viewPublishedEvents = findViewById(R.id.viewPublishedEvents);
        eventPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventPublishButtonClick();
            }
        });

        dateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDateButtonClick();
            }
        });

        locCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetLocationButtonClick();
            }
        });

        viewPublishedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewPublishedEventsClick();
            }
        });


    }

    private void onEventPublishButtonClick() {
        String eventNameValue = eventName.getText().toString();
        String eventDescValue = eventDesc.getText().toString();
        String eventLocNameValue = eventLocName.getText().toString();
        String temp =eventMaxPax.getText().toString();
        String eventDateValue = eventPublishDate.getText().toString();

        if (eventNameValue.isEmpty() || eventDescValue.isEmpty() || eventLocNameValue.isEmpty()||temp.isEmpty()||eventDateValue.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Fill in all data fields ",Toast.LENGTH_SHORT).show();
        }else{
            int eventMaxPaxValue = Integer.parseInt(eventMaxPax.getText().toString());
            String uid = auth.getCurrentUser().getUid();
            DocumentReference docRef = db.collection(account.KEY_ORGANISERS).document(uid);

            // Retrieve the document
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // DocumentSnapshot contains the data of the document
                            String eventOrg = document.getString(account.KEY_USERNAME);
                            Log.d("Firestore", "Document data: " + document.getData());

                            // Create a Map to hold your data
                            Map<String, Object> eventData = new HashMap<>();
                            eventData.put(helper.KEY_EVENTNAME, eventNameValue);
                            eventData.put(helper.KEY_EVENTDESC, eventDescValue);
                            eventData.put(helper.KEY_EVENTLOCNAME, eventLocNameValue);
                            eventData.put(helper.KEY_EVENTMAXPAX, eventMaxPaxValue);
                            eventData.put(helper.KEY_LAT, 0);
                            eventData.put(helper.KEY_LON, 0);
                            eventData.put(helper.KEY_EVENTDATE, eventDateValue);
                            eventData.put(helper.KEY_EVENTORG, eventOrg);
                            eventData.put(helper.KEY_EVENTSIGNUP, 0);

                            // Add the imageData if available
                            if (imageData != null) {
                                Blob imageBlob = Blob.fromBytes(imageData);
                                eventData.put(helper.KEY_EVENTIMAGE, imageBlob);
                            }

                            // Specify the collection reference where you want to store the data
                            CollectionReference eventsCollection = db.collection(helper.KEY_EVENTS);

                            // Specify a document reference with the provided event name
                            DocumentReference eventDocument = eventsCollection.document(eventNameValue);

                            // Set the data in the document
                            eventDocument.set(eventData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DocumentReference volunteerRef = db.collection(account.KEY_ORGANISERS).document(uid);


                                            volunteerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot userSignupDocument = task.getResult();
                                                        if (userSignupDocument.exists()) {
                                                            CollectionReference volunteerEventsCollection = volunteerRef.collection(account.KEY_MYEVENTS);
                                                            DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(eventNameValue);

                                                            Map<String, Object> myEvents = new HashMap<>();
                                                            myEvents.put(helper.KEY_EVENTNAME, eventNameValue);
                                                            myEvents.put(helper.KEY_EVENTDESC, eventDescValue);
                                                            myEvents.put(helper.KEY_EVENTLOCNAME, eventLocNameValue);
                                                            myEvents.put(helper.KEY_EVENTMAXPAX, eventMaxPaxValue);
                                                            myEvents.put(helper.KEY_LAT, 0);
                                                            myEvents.put(helper.KEY_LON, 0);
                                                            myEvents.put(helper.KEY_EVENTDATE, eventDateValue);
                                                            myEvents.put(helper.KEY_EVENTORG, eventOrg);
                                                            myEvents.put(helper.KEY_EVENTSIGNUP, 0);

                                                            // Add the imageData if available
                                                            if (imageData != null) {
                                                                Blob imageBlob = Blob.fromBytes(imageData);
                                                                myEvents.put(helper.KEY_EVENTIMAGE, imageBlob);
                                                            }
                                                            volunteerEventsDocument.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getApplicationContext(), "Event Added Successfully.", Toast.LENGTH_SHORT).show();
                                                                    Log.d("Firestore", "Event added to myEvents");

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Fire", "FailedS Sign Up");
                                                                }
                                                            });
                                                        }


                                                    } else {
                                                        CollectionReference volunteerEventsCollection = volunteerRef.collection(account.KEY_MYEVENTS);
                                                        DocumentReference volunteerEventsDocument = volunteerEventsCollection.document(eventNameValue);
                                                        Map<String, Object> myEvents = new HashMap<>();
                                                        myEvents.put(helper.KEY_EVENTNAME, eventNameValue);
                                                        myEvents.put(helper.KEY_EVENTDESC, eventDescValue);
                                                        myEvents.put(helper.KEY_EVENTLOCNAME, eventLocNameValue);
                                                        myEvents.put(helper.KEY_EVENTMAXPAX, eventMaxPaxValue);
                                                        myEvents.put(helper.KEY_LAT, 0);
                                                        myEvents.put(helper.KEY_LON, 0);
                                                        myEvents.put(helper.KEY_EVENTDATE, eventDateValue);
                                                        myEvents.put(helper.KEY_EVENTORG, eventOrg);
                                                        myEvents.put(helper.KEY_EVENTSIGNUP, 0);
                                                        myEvents.put(helper.KEY_EVENTSTATUS, helper.KEY_EVENTUPCOMING);

                                                        // Add the imageData if available
                                                        if (imageData != null) {
                                                            Blob imageBlob = Blob.fromBytes(imageData);
                                                            myEvents.put(helper.KEY_EVENTIMAGE, imageBlob);
                                                        }
                                                        volunteerEventsDocument.set(myEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getApplicationContext(), "Event Added Successfully.", Toast.LENGTH_SHORT).show();
                                                                Log.d("Firestore", "Event added to myEvents");

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

                                        }
                                    });
                        } else {
                            Log.d("Firestore", "No such organizer document");
                            // Handle the case where the organizer document does not exist
                        }
                    } else {
                        Log.e("Firestore", "Error getting organizer document", task.getException());
                    }
                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }


    private void onSelectDateButtonClick () {
        // Implement the logic when the select date button is clicked
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (selectedDate.after(currentDate)) {
                            // Selected date is in the future
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String formattedDate = dateFormat.format(selectedDate.getTime());
                            Toast.makeText(getApplicationContext(),"Selected Date: " + formattedDate,Toast.LENGTH_LONG).show();
                            eventPublishDate.setText(formattedDate);

                        } else {
                            // Selected date is not in the future
                            Toast.makeText(getApplicationContext(),"Please select a future date.",Toast.LENGTH_LONG).show();

                        }
                    }
                },
                year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }


    private void onGetLocationButtonClick () {
        // Implement the logic when the get location button is clicked
    }

    private void onViewPublishedEventsClick () {
        // Implement the logic when the view published events button is clicked
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("intent","viewPublishedEvents");
        startActivity(intent);
        finish();

    }
}