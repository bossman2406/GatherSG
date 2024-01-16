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


public class organiserLogin extends Fragment {

    public String uid;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseAuth mAuth;
    accountHelper helper;

    private EditText email,password;
    private Button login;


    public organiserLogin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organiser_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        helper =new accountHelper();
        email = view.findViewById(R.id.emailLoginOrganisers);
        password = view.findViewById(R.id.passwordLoginOrganisers);
        login =view.findViewById(R.id.organiserLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               organiserLogin(String.valueOf(email.getText()),String.valueOf(password.getText()));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    public void organiserLogin(String email, String password){
        if (TextUtils.isEmpty(email)){
            Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(password)){
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("YourTag", "Before signInWithEmailAndPassword");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(requireContext(), "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                            helper.setAccountType(helper.KEY_ORGANISERS);


                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}