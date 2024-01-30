package com.gathersg.user.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gathersg.user.R;
import com.gathersg.user.login.Login;
import com.gathersg.user.register.organiserRegister;
import com.gathersg.user.register.volunteerRegister;

public class Register extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Button volunteers, organiser;
    private TextView textView;

    private com.gathersg.user.register.volunteerRegister volunteerRegister;
    private com.gathersg.user.register.organiserRegister organiserRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        volunteers = findViewById(R.id.volunteerButton);
        organiser = findViewById(R.id.organiserButton);
        textView = findViewById(R.id.logintext);

        volunteerRegister = new volunteerRegister();
        organiserRegister = new organiserRegister();

        volunteers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(volunteerRegister);
            }
        });

        organiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(organiserRegister);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            showViewsAfterBackStack();
        } else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }

    private void navigateToFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.registerFragmentContainer, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
        hideViewsForFragment();
    }

    private void hideViewsForFragment() {
        volunteers.setVisibility(View.INVISIBLE);
        organiser.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }

    private void showViewsAfterBackStack() {
        volunteers.setVisibility(View.VISIBLE);
        organiser.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }
}
