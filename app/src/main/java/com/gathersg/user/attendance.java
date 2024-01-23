package com.gathersg.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class attendance extends AppCompatActivity {
    Button button;
    Spinner spinner;
    eventStatusService eventStatusService;
    ArrayAdapter<String> adapter;
    List<String> nameList;
    String selectedEvent;

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

    private void addAttendanceToFirestore(String selectedEvent, String uidAttendance) {
        eventStatusService.addAttendance(selectedEvent, uidAttendance, unused -> {
            Log.d("MYTAF", "User attended");
        });
    }
}
