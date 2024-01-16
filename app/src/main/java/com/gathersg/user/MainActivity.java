package com.gathersg.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private Button button, saveDetails, weeklyButton;
    private TextView textView,usernameNav;
    private BottomNavigationView bottomNav;
    FirebaseAuth auth;
    FirebaseUser user;
    private userProfile userProfile;
    private calendar calendar;
    private allEvents allEvents;
    private myEventsVol myEventsVol;
    private myEventsOrg myEventsOrg;
    private weeklyCalendar weeklyCalendar;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;


    private EditText username, number;

    private static final String TAG = "user";
    private static final String KEY_TITLE = "username";
    private static final String KEY_NUMBER = "number";
    private FirebaseFirestore db ;
    public String uid;
    accountHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        auth =FirebaseAuth.getInstance();
        String temp = helper.accountType;
        Log.d("Your_Tag_help",temp);


        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();

        Log.d("Your_Tag",temp + uid);
        DocumentReference docRef = db.collection(temp).document(uid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userProfile = new userProfile();
        calendar = new calendar();
        weeklyCalendar = new weeklyCalendar();
        allEvents = new allEvents();
        myEventsVol = new myEventsVol();
        myEventsOrg = new myEventsOrg();





        bottomNav = findViewById(R.id.bottomNav);
        if (helper.KEY_VOLUNTEER.equals(helper.accountType)){
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
                if(id == R.id.homepage) {
                    fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, allEvents)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
//
                    return true;
                }else if(id==R.id.calenderEvents){
                    CalendarUtils.selectedDate = LocalDate.now();
                    fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, calendar)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();

                    return true;
                } else if (id == R.id.myEvents) {
                    if(helper.accountType==helper.KEY_VOLUNTEER){
                        fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, myEventsVol)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .commit();
                    } else if (helper.accountType==helper.KEY_ORGANISERS) {
                        fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, myEventsOrg)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .commit();
                    }


                    return true;
                } else if (id == R.id.chatbot) {
                    return true;
                } else if (id == R.id.createEvent) {
                    Intent intent = new Intent(getApplicationContext(),eventOrganiserAddPhoto.class);
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
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.nav_open ,R.string.nav_close);


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
                if(id==R.id.accountNav){
                    fragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, userProfile)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }
                if (id == R.id.logoutNav){
                    Intent intent = new Intent(getApplicationContext(),Login.class);
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
                        String data = document.getString(helper.KEY_USERNAME);
                        if(usernameNav!=null) {
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
    public void onItemClick(int position, LocalDate date)
    {
        if(date !=null)
        {
            CalendarUtils.selectedDate =date;
        }
    }

    public void onActivityStart(){
        String data;
        Intent intent = getIntent();

        if (intent != null) {
            data = intent.getStringExtra("intent"); // Replace "key" with the key you used in putExtra
        } else{
            data = "default";
        }

        switch (data){
            case "viewPublishedEvents":
                bottomNav.setSelectedItemId(R.id.myEvents);

                break;


            case "default":
                bottomNav.setSelectedItemId(R.id.homepage);

                break;

        }

    }

}