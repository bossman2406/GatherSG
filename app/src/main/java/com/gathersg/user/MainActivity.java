package com.gathersg.user;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private static final String TAG = "user";
    private static final String KEY_TITLE = "username";
    private static final String KEY_NUMBER = "number";
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    public String uid;
    FirebaseAuth auth;
    FirebaseUser user;
    accountHelper helper;
    private Button button, saveDetails, weeklyButton;
    private TextView textView, usernameNav;
    private BottomNavigationView bottomNav;
    private userProfile userProfile;
    private calendar calendar;
    private allEvents allEvents;
    private weeklyCalendarOrg weeklyCalendarOrg;
    private weeklyCalendarVol weeklyCalendarVol;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private EditText username, number;
    private FirebaseFirestore db;
    dataLinking dataLinking;
    eventStatusService eventStatusService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataLinking = new dataLinking();
        eventStatusService = new eventStatusService();

      //  dataLinking.linkData();
        eventStatusService.checkData();
        eventStatusService.checkSignUp();
        dataLinking.linkData();

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String temp = accountHelper.accountType;


        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();



        DocumentReference docRef = db.collection(temp).document(uid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userProfile = new userProfile();
        calendar = new calendar();
        weeklyCalendarVol = new weeklyCalendarVol();
        weeklyCalendarOrg = new weeklyCalendarOrg();
        allEvents = new allEvents();


        bottomNav = findViewById(R.id.bottomNav);
        if (accountHelper.KEY_VOLUNTEER.equals(accountHelper.accountType)) {
            bottomNav.getMenu().findItem(R.id.createEvent).setVisible(false);
        }


        onActivityStart();

        fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, allEvents)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();


        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                invalidateOptionsMenu();
                if (id == R.id.homepage) {
                    eventStatusService.checkData();
                    eventStatusService.checkSignUp();
                    dataLinking.linkData();
                   // dataLinking.linkData();
                    fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, allEvents)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
//
                    return true;
                } else if (id == R.id.calenderEvents) {
                    CalendarUtils.selectedDate = LocalDate.now();
                    eventStatusService.checkData();
                    eventStatusService.checkSignUp();
                    dataLinking.linkData();
                    //dataLinking.linkData();
                    Log.d("DATE", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
                    if (accountHelper.accountType == accountHelper.KEY_VOLUNTEER) {
                        fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, weeklyCalendarVol)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .commit();
                    } else if (accountHelper.accountType == accountHelper.KEY_ORGANISERS) {
                        fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, weeklyCalendarOrg)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .commit();
                    }

                    return true;
                } else if (id == R.id.myEvents) {

                    eventStatusService.checkData();
                    eventStatusService.checkSignUp();
                    dataLinking.linkData();
                   // dataLinking.linkData();
                    if (accountHelper.accountType == accountHelper.KEY_VOLUNTEER){
                        Intent intent = new Intent(getApplicationContext(), QrCode.class);
                        startActivity(intent);
                        finish();
                    } else if(accountHelper.accountType == accountHelper.KEY_ORGANISERS){
                        Intent intent = new Intent(getApplicationContext(), attendance.class);
                        startActivity(intent);
                        finish();
                    }


                    return true;
                } else if (id == R.id.chatbot) {

                    eventStatusService.checkSignUp();
                    eventStatusService.checkData();
                    dataLinking.linkData();

                    return true;
                } else if (id == R.id.createEvent) {

                    eventStatusService.checkSignUp();
                    eventStatusService.checkData();
                    dataLinking.linkData();

                    Intent intent = new Intent(getApplicationContext(), eventOrganiserAddPhoto.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawerMain);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navView);
        View view = navigationView.getHeaderView(0);
        usernameNav = view.findViewById(R.id.usernameNav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.accountNav) {
                    fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, userProfile)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }
                if (id == R.id.logoutNav) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // DocumentSnapshot contains the data
                        String data = document.getString(accountHelper.KEY_USERNAME);
                        if (usernameNav != null) {
                            usernameNav.setText(data);
                        }
                        Log.d("FirestoreData", "DocumentSnapshot data: " + data);
                    } else {
                        Log.d("FirestoreData", "No such document");
                    }
                } else {
                    Log.d("FirestoreData", "get failed with ", task.getException());
                }
            }
        });
//        weeklyButton = findViewById(R.id.weeklyCalendarButton);
//        weeklyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, weeklyCalendar)
//                        .setReorderingAllowed(true)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
        }
    }

    public void onActivityStart() {
        String data;
        Intent intent = getIntent();

        if (intent != null) {
            data = intent.getStringExtra("intent"); // Replace "key" with the key you used in putExtra
            if (data == "viewPublishedEvents") {
                bottomNav.setSelectedItemId(R.id.myEvents);
            }
        } else {

            bottomNav.setSelectedItemId(R.id.homepage);
        }

    }


}