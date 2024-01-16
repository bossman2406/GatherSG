package com.gathersg.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private Button volunteers,organiser,register;
    private TextView loginText,registerText;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    private volunteerLogin volunteerLogin;
    private organiserLogin organiserLogin;
    private FragmentContainerView fragmentContainerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//
//
////        if(currentUser!=null){
////            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////            startActivity(intent);
////            finish();
////        }

        setContentView(R.layout.activity_login);
        volunteers = findViewById(R.id.volunteerButton);
        organiser =findViewById(R.id.organiserButton);
        loginText =findViewById(R.id.logintext);
        registerText =findViewById(R.id.registerText);
        register= findViewById(R.id.registerButton);
        fragmentContainerView = findViewById(R.id.fragmentContainerView3);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();

            }
        });

        volunteerLogin = new volunteerLogin();
        organiserLogin = new organiserLogin();

        volunteers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView3, volunteerLogin)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();


                volunteers.setVisibility(View.INVISIBLE);
                organiser.setVisibility(View.INVISIBLE);
                loginText.setVisibility(View.INVISIBLE);
                registerText.setVisibility(View.INVISIBLE);
                register.setVisibility(View.INVISIBLE);
            }
        });
        organiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView3, organiserLogin)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                volunteers.setVisibility(View.INVISIBLE);
                organiser.setVisibility(View.INVISIBLE);
                loginText.setVisibility(View.INVISIBLE);
                registerText.setVisibility(View.INVISIBLE);
                register.setVisibility(View.INVISIBLE);

            }
        });
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            volunteers.setVisibility(View.VISIBLE);
            organiser.setVisibility(View.VISIBLE);
            loginText.setVisibility(View.VISIBLE);
            registerText.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

}