package com.gathersg.user;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    private Button volunteers, organiser, register;
    private TextView loginText, registerText;
    private volunteerLogin volunteerLogin;
    private organiserLogin organiserLogin;
    private FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        volunteers = findViewById(R.id.volunteerButton);
        organiser = findViewById(R.id.organiserButton);
        loginText = findViewById(R.id.logintext);
        registerText = findViewById(R.id.registerText);
        register = findViewById(R.id.registerButton);
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
                switchToFragment(volunteerLogin);
            }
        });

        organiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(organiserLogin);
            }
        });
    }

    private void switchToFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView3, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

        volunteers.setVisibility(View.INVISIBLE);
        organiser.setVisibility(View.INVISIBLE);
        loginText.setVisibility(View.INVISIBLE);
        registerText.setVisibility(View.INVISIBLE);
        register.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            resetViews();
        } else {
            super.onBackPressed();
        }
    }

    private void resetViews() {
        volunteers.setVisibility(View.VISIBLE);
        organiser.setVisibility(View.VISIBLE);
        loginText.setVisibility(View.VISIBLE);
        registerText.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
    }
}
