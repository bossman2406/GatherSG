package com.gathersg.user.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gathersg.user.mainpage.MainActivity;
import com.gathersg.user.R;
import com.gathersg.user.services.eventStatusService;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class attendance extends AppCompatActivity {
    Button button;
    Spinner spinner;
    com.gathersg.user.services.eventStatusService eventStatusService;
    ArrayAdapter<String> adapter;
    List<String> nameList;
    String selectedEvent;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    FirebaseAuth auth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendace);

        eventStatusService= new eventStatusService();



        spinner = findViewById(R.id.autoCompleteEvent);
        button = findViewById(R.id.eventScanButton);

        // Initialize nameList
        nameList = new ArrayList<>();

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEvent = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the event when nothing is selected
            }
        });

        // Set up the button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();
            }
        });

        // Fetch data from Firestore
        fetchEventData();
    }

    protected void scanQrCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Scan QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String uidAttendance = intentResult.getContents();
            if (uidAttendance != null) {
                // Add attendance to Firestore
                Toast.makeText(this,uidAttendance,Toast.LENGTH_LONG).show();
                addAttendanceToFirestore(selectedEvent, uidAttendance);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchEventData() {
        eventStatusService.addData((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error fetching documents", e);
                return;
            }

            // Clear lists to avoid duplicates
            nameList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                // Extract data from the document
                String eventName = document.getString(eventHelper.KEY_EVENTNAME);

                // Log the data for debugging
                Log.d("My_TAG", eventName);
                // Add data to lists
                nameList.add(eventName);
            }
            // Update the adapter with the new data
            adapter.notifyDataSetChanged();
        });
    }

    private void  addAttendanceToFirestore(String selectedEvent, String uidAttendance) {
        DocumentReference attendanceRef = db.collection(eventHelper.KEY_EVENTS).document(selectedEvent).collection(eventHelper.KEY_EVENTATTENDANCE).document(uidAttendance);
        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot temp = task.getResult();
                    if(temp.exists()){
                        Toast.makeText(getApplicationContext(),"Volunteer has already Signed up",Toast.LENGTH_SHORT).show();
                    }else{
                        DocumentReference eventSignUp = db.collection(eventHelper.KEY_EVENTS).document(selectedEvent).collection(eventHelper.KEY_EVENTSIGNUP).document(uidAttendance);
                        eventSignUp.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot temp2 = task.getResult();
                                    if(temp2.exists()){
                                        DocumentReference volunteer = db.collection(accountHelper.KEY_VOLUNTEER).document(uidAttendance);

                                        volunteer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot temp1 = task.getResult();
                                                    if (temp1.exists()) {
                                                        String name = temp1.getString(accountHelper.KEY_USERNAME);
                                                        String email = temp1.getString(accountHelper.KEY_EMAIL);
                                                        String number = temp1.getString(accountHelper.KEY_NUMBER);
                                                        Long via = temp1.getLong(accountHelper.KEY_VIA);

                                                        Map<String, Object> volunteerData = new HashMap<>();
                                                        volunteerData.put(accountHelper.KEY_USERNAME, name);
                                                        volunteerData.put(accountHelper.KEY_EMAIL, email);
                                                        volunteerData.put(accountHelper.KEY_NUMBER, number);
                                                        volunteerData.put(accountHelper.KEY_VIA, via);
                                                        attendanceRef.set(volunteerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                //Log and toast
                                                                Toast.makeText(getApplicationContext(), name + " attendance recorded", Toast.LENGTH_SHORT).show();
                                                                Log.d("Attendance", name + " attendance recorded");

                                                                DocumentReference viaRef = db.collection(accountHelper.KEY_VOLUNTEER).document(uidAttendance);

                                                                long newCount = (via != null) ? via + 1 : 1;

                                                                Map<String, Object> temp = new HashMap<>();
                                                                temp.put(accountHelper.KEY_VIA, newCount);

                                                                viaRef.update(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        DocumentReference docRef = volunteer.collection(accountHelper.KEY_MYEVENTS).document(selectedEvent);

                                                                    }
                                                                });


                                                            }
                                                        });

                                                    }

                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Volunteer has not signed up for event",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });




                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //add toast
            }
        });

    }
    @Override
    public void onBackPressed() {
        // Add your custom behavior here
        // For example, navigate to another activity

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

        // If you want to keep the default behavior (finish the activity), call super.onBackPressed()
        super.onBackPressed();
    }
}
