package com.gathersg.user;

import static com.gathersg.user.CalendarUtils.daysInWeekArray;
import static com.gathersg.user.CalendarUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class weeklyCalendarOrg extends Fragment implements  CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    private RecyclerView recyclerView;

    myEventsOrgAdapter myEventsOrgAdapter;
    private List<eventHelper> eventHelpers;
    eventHelper event;
    accountHelper account;

    // Declare lists outside the onCreateView method
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList,statusList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Long> signUpList;
    private ArrayList<Blob> imageList;



    public weeklyCalendarOrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view  = inflater.inflate(R.layout.fragment_weekly_calendar, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
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
        signUpList = new ArrayList<>();

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
                signUpList.clear();

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


                    DocumentReference signUpDocument = db.collection(event.KEY_EVENTS).document(eventName);
                    signUpDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot userSignupDocument = task.getResult();
                            Long temp = userSignupDocument.getLong(event.KEY_EVENTSIGNUP);
                            DocumentReference tempRef = db.collection(account.KEY_ORGANISERS).document(uid).collection(account.KEY_MYEVENTS).document(eventName);
                            Map<String,Object> eventSignUp = new HashMap<>();
                            eventSignUp.put(event.KEY_EVENTSIGNUP,temp);
                            tempRef.update(eventSignUp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("SIGN UP","ADDED TO ORGANISER MY EVENTS");
                                }
                            });
                        }
                    });


                    Long pax = document.getLong(event.KEY_EVENTSIGNUP);

                    // Log the data for debugging
                    Log.d("My_TAG", eventName + eventDesc + date + organiser + locName + lat + lon + image +status +pax);

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
                    signUpList.add(pax);

                    // Create an eventHelper object and add it to the list
                    eventHelper helper = new eventHelper();
                    helper.uploadmyOrgEvents(eventName, eventDesc, date, organiser, locName, lat, lon, image,status,pax);
                    eventHelpers.add(helper);
                }
                myEventsOrgAdapter = new myEventsOrgAdapter(getContext(), nameList, descList, dateList, orgList,locNameList, latList, lonList, imageList,statusList,signUpList);
                recyclerView.setAdapter(myEventsOrgAdapter);

                // Update the RecyclerView with the new data
                if (myEventsOrgAdapter != null) {
                    myEventsOrgAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });

        return view;
    }
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, (CalendarAdapter.OnItemListener) getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }
}