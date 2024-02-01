package com.gathersg.user.myPhotos;

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

import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
import com.gathersg.user.mainpage.MainActivity;
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
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class myPhotoAdd extends AppCompatActivity {

    FirebaseAuth auth;
    eventHelper helper;
    accountHelper account;
    private EditText eventName, eventDesc, eventLocName, eventMaxPax,eventVIA;
    private String eventDate;
    private MaterialCardView dateCard, locCard;
    private double eventLatitude, eventLongitude;
    private int eventMaxParticipants;
    private byte[] imageData;
    private Button eventPublishButton;
    private TextView viewPublishedEvents, eventPublishDate;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photo_add);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("imageData")) {
            imageData = intent.getByteArrayExtra("imageData");
        }
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        eventName = findViewById(R.id.eventPublishName);
        eventDesc = findViewById(R.id.eventPublishDesc);
        eventPublishButton = findViewById(R.id.eventPublishButton);

        eventPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventPublishButtonClick();
            }
        });
    }

    private void onEventPublishButtonClick() {
        String eventNameValue = eventName.getText().toString();
        String eventDescValue = eventDesc.getText().toString();

        if (eventNameValue.isEmpty() || eventDescValue.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fill in all data fields", Toast.LENGTH_SHORT).show();
        } else {

            db.collection(accountHelper.accountType).document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            Blob temp = documentSnapshot.getBlob(accountHelper.KEY_IMAGE);
                            Map<String, Object> eventData = new HashMap<>();
                            eventData.put(eventHelper.KEY_EVENTNAME, eventNameValue);
                            eventData.put(eventHelper.KEY_EVENTDESC, eventDescValue);

                            // Add the imageData if available
                            if (imageData != null) {
                                Blob imageBlob = Blob.fromBytes(imageData);
                                eventData.put(eventHelper.KEY_EVENTIMAGE, imageBlob);
                            }
                            if (temp != null) {
                                eventData.put(accountHelper.KEY_IMAGE, temp);
                            }

                            // Specify a document reference with a unique ID
                            DocumentReference eventDocument = db.collection(accountHelper.KEY_MYPHOTOS).document();

                            // Set the data in the document
                            eventDocument.set(eventData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Use the same eventData for the second set operation
                                            DocumentReference myPhoto = db.collection(accountHelper.accountType)
                                                    .document(auth.getUid())
                                                    .collection(accountHelper.KEY_MYPHOTOS)
                                                    .document();

                                            myPhoto.set(eventData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure
                                            Log.e("Firestore", "Error adding document", e);
                                        }
                                    });
                        }
                    }
                }

            });
            // Use Firestore server timestamp

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}