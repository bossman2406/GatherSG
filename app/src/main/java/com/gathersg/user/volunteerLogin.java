package com.gathersg.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class volunteerLogin extends Fragment {
    public String uid;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseAuth mAuth;
    public String accountType;

    accountHelper helper;

    private EditText email,password;
    private Button login;


    public volunteerLogin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        helper =new accountHelper();
        email = view.findViewById(R.id.emailLoginVolunteer);
        password = view.findViewById(R.id.passwordLoginVolunteer);
        login =view.findViewById(R.id.volunteerLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("YourTag", "Login button clicked");
                volunteerLogin();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    public void volunteerLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("YourTag", "Before signInWithEmailAndPassword");

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("YourTag", "Login Successful");
                            Toast.makeText(requireContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                            helper.setAccountType(helper.KEY_VOLUNTEER);
                            String temp = helper.getAccountType();
                            Log.d("Your_TAG",temp);

                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            // If sign-in fails, display a message to the user.
                            Log.e("YourTag", "Login failed", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}