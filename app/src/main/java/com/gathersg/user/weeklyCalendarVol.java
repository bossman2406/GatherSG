package com.gathersg.user;

import static com.gathersg.user.CalendarUtils.daysInWeekArray;
import static com.gathersg.user.CalendarUtils.monthYearFromDate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class weeklyCalendarVol extends Fragment implements CalendarAdapter.OnItemListener {
    FirebaseAuth auth;
    myEventsVolAdapter myEventsVolAdapter;
    eventHelper event;
    accountHelper account;
    private FirebaseFirestore db;
    private RecyclerView recyclerView, calendarRecyclerView;
    private List<eventHelper> eventHelpers;
    // Declare lists outside the onCreateView method
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList, statusList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Blob> imageList;
    private TextView monthYearText;
    private ListView eventListView;
    private Button nextWeekAction, previousWeekAction;
    eventStatusService eventStatusService;
    dataLinking dataLinking;

    public weeklyCalendarVol() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataLinking = new dataLinking();
        dataLinking.linkData();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_calendar, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        nextWeekAction = view.findViewById(R.id.nextWeekAction);
        previousWeekAction = view.findViewById(R.id.previousWeekAction);
        nextWeekAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeekAction(v);
            }
        });
        previousWeekAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWeekAction(v);
            }
        });
        setWeekView();
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
        String temp = accountHelper.accountType;

        CollectionReference eventsCollection = db.collection(temp).document(uid).collection(accountHelper.KEY_MYEVENTS);
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
                    String status = document.getString(eventHelper.KEY_EVENTSTATUS);

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
                    helper.uploadmyEvents(eventName, eventDesc, date, organiser, locName, lat, lon, image, status);
                    eventHelpers.add(helper);
                }
                myEventsVolAdapter = new myEventsVolAdapter(getContext(), nameList, descList, dateList, orgList, locNameList, latList, lonList, imageList, statusList);
                recyclerView.setAdapter(myEventsVolAdapter);

                // Update the RecyclerView with the new data
                if (myEventsVolAdapter != null) {
                    myEventsVolAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });
        return view;
    }


    private void initWidgets() {

    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, (CalendarAdapter.OnItemListener) getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }
}