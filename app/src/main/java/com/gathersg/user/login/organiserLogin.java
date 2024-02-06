package com.gathersg.user.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gathersg.user.mainpage.MainActivity;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
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

    private EditText email, password;
    private Button login;
    private TextView forgetPass;
    Dialog dialog;


    public organiserLogin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organiser_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        helper = new accountHelper();
        email = view.findViewById(R.id.emailLoginOrganisers);
        password = view.findViewById(R.id.passwordLoginOrganisers);
        login = view.findViewById(R.id.organiserLogin);
        forgetPass = view.findViewById(R.id.forgetPassword);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.forgetpassword);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organiserLogin(String.valueOf(email.getText()), String.valueOf(password.getText()));
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button reset;
                EditText email;
                reset = dialog.findViewById(R.id.resetBtn);
                email = dialog.findViewById(R.id.emailLogin);

                FirebaseAuth auth = FirebaseAuth.getInstance();

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Assuming you have an EditText for email input named 'emailEditText'
                        String userEmail = email.getText().toString().trim();

                        // Validate email format
                        if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            // Email is empty or not in a valid format
                            Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        auth.sendPasswordResetEmail(userEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Password reset email sent successfully
                                            // Provide user feedback, e.g., show a Toast
                                            Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle errors, e.g., show an error message
                                            Toast.makeText(getContext(), "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Intent intent = new Intent(getContext(), Login.class);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
        if (savedInstanceState == null){
            return view;
        }
        else return null;

        // Inflate the layout for this fragment

    }

    public void organiserLogin(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("YourTag", "Before signInWithEmailAndPassword");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(requireContext(), "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                            helper.setAccountType(accountHelper.KEY_ORGANISERS);


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