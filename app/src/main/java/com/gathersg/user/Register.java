package com.gathersg.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Register extends AppCompatActivity {

    private Button volunteers,organiser;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private TextView textView;

    private volunteerRegister volunteerRegister;
    private organiserRegister organiserRegister;


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        volunteers = findViewById(R.id.volunteerButton);
        organiser =findViewById(R.id.organiserButton);
        textView =findViewById(R.id.logintext);

        volunteerRegister = new volunteerRegister();
        organiserRegister = new organiserRegister();

        volunteers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager.beginTransaction().replace(R.id.registerFragmentContainer, volunteerRegister)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                volunteers.setVisibility(View.INVISIBLE);
                organiser.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        });
        organiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.registerFragmentContainer, organiserRegister)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                volunteers.setVisibility(View.INVISIBLE);
                organiser.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }
        });


    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            volunteers.setVisibility(View.VISIBLE);
            organiser.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else  if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }


}
