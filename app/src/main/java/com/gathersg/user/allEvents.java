package com.gathersg.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class allEvents extends Fragment {

    eventHelper helper;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    dataLinking dataLinking;
    eventStatusService eventStatusService;
    private upcomingEventAdapter upcomingEventAdapter;
    private List<eventHelper> eventHelpers;
    // Declare lists outside the onCreateView method
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Blob> imageList;

    public allEvents() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);
        recyclerView = view.findViewById(R.id.allEventsRecyclerView);
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

        db = FirebaseFirestore.getInstance();
        CollectionReference eventsCollection = db.collection("events");
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
                    String eventName = document.getString(eventHelper.KEY_EVENTNAME);
                    String eventDesc = document.getString(eventHelper.KEY_EVENTDESC);
                    String date = document.getString(eventHelper.KEY_EVENTDATE);
                    String organiser = document.getString(eventHelper.KEY_EVENTORG);
                    String locName = document.getString(eventHelper.KEY_EVENTLOCNAME);
                    Double lat = document.getDouble(eventHelper.KEY_LAT);
                    Double lon = document.getDouble(eventHelper.KEY_LON);
                    Blob image = document.getBlob(eventHelper.KEY_EVENTIMAGE);

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

                    // Create an eventHelper object and add it to the list
                    eventHelper helper = new eventHelper();
                    helper.uploadAllEvents(eventName, eventDesc, date, organiser, locName, lat, lon, image);
                    eventHelpers.add(helper);
                }
                upcomingEventAdapter = new upcomingEventAdapter(getContext(), nameList, descList, dateList, orgList, locNameList, latList, lonList, imageList);
                recyclerView.setAdapter(upcomingEventAdapter);

                // Update the RecyclerView with the new data
                if (upcomingEventAdapter != null) {
                    upcomingEventAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });

        return view;
    }
}

