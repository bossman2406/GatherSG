package com.gathersg.user;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class myEventsVol extends Fragment {
    private FirebaseFirestore db;
    FirebaseAuth auth;
    private RecyclerView recyclerView;

    myEventsVolAdapter myEventsVolAdapter;
    private List<eventHelper> eventHelpers;
    eventHelper event;
    accountHelper account;

    // Declare lists outside the onCreateView method
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList,statusList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Blob> imageList;




    public myEventsVol() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events_vol, container, false);
        recyclerView = view.findViewById(R.id.myEventRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventHelpers = new ArrayList<>();

        // Initialize lists here
        nameList = new ArrayList<>();
        descList = new ArrayList<>();
        locNameList = new ArrayList<>();
        dateList = new ArrayList<>();
        orgList = new ArrayList<>();
        latList = new ArrayList<>();
        lonList = new ArrayList<>();
        imageList = new ArrayList<>();
        statusList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();
        String temp = account.accountType;

        CollectionReference eventsCollection = db.collection(temp).document(uid).collection(account.KEY_MYEVENTS);
        eventsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore", "Error fetching documents", e);
                    return;
                }

                // Clear lists to avoid duplicates
                nameList.clear();
                descList.clear();
                locNameList.clear();
                dateList.clear();
                orgList.clear();
                latList.clear();
                lonList.clear();
                imageList.clear();
                eventHelpers.clear();

                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    // Extract data from the document
                    String eventName = document.getString(event.KEY_EVENTNAME);
                    String eventDesc = document.getString(event.KEY_EVENTDESC);
                    String date = document.getString(event.KEY_EVENTDATE);
                    String organiser = document.getString(event.KEY_EVENTORG);
                    String locName = document.getString(event.KEY_EVENTLOCNAME);
                    Double lat = document.getDouble(event.KEY_LAT);
                    Double lon = document.getDouble(event.KEY_LON);
                    Blob image = document.getBlob(event.KEY_EVENTIMAGE);
                    String status = document.getString(event.KEY_EVENTSTATUS);

                    // Log the data for debugging
                    Log.d("My_TAG", eventName + eventDesc + date + organiser + locName + lat + lon + image);

                    // Add data to lists
                    nameList.add(eventName);
                    descList.add(eventDesc);
                    locNameList.add(locName);
                    dateList.add(date);
                    orgList.add(organiser);
                    latList.add(lat);
                    lonList.add(lon);
                    imageList.add(image);
                    statusList.add(status);

                    // Create an eventHelper object and add it to the list
                    eventHelper helper = new eventHelper();
                    helper.uploadmyEvents(eventName, eventDesc, date, organiser, locName, lat, lon, image,status);
                    eventHelpers.add(helper);
                }
                myEventsVolAdapter = new myEventsVolAdapter(getContext(), nameList, descList, dateList, orgList,locNameList, latList, lonList, imageList,statusList);
                recyclerView.setAdapter(myEventsVolAdapter);

                // Update the RecyclerView with the new data
                if (myEventsVolAdapter != null) {
                    myEventsVolAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });

        return view;
    }
}