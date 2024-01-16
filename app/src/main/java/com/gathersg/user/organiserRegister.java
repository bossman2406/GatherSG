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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class organiserRegister extends Fragment {

    FirebaseAuth mAuth;
    EditText password, uen, name, email;
    Button register;
    public String uid;
    accountHelper helper;

    private static final String TAG = "user";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public organiserRegister() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organiser_register, container, false);
        name = view.findViewById(R.id.nameRegisterOrganiser);
        mAuth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.emailRegisterOrganisers);
        uen = view.findViewById(R.id.uenRegisterOrganiser);
        password = view.findViewById(R.id.passwordRegisterOrganisers);
        register = view.findViewById(R.id.organiserRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volunteerRegister(
                        String.valueOf(name.getText()),
                        String.valueOf(uen.getText()),
                        String.valueOf(email.getText()),
                        String.valueOf(password.getText())
                );
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    protected void volunteerRegister(String name, String uen, String email, String password) {
        if (TextUtils.isEmpty(uen)) {
            Toast.makeText(getContext(), "Enter UEN", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Enter Organisation Name", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                uid = currentUser.getUid();
                                // Now, 'uid' contains the unique identifier for the current user
                            }

                            DocumentReference docRef = db.collection(helper.KEY_ORGANISERS).document(uid);
                            CollectionReference itemsCollection = docRef.collection("myEvents");

                            Map<String, Object> note = new HashMap<>();
                            note.put(helper.KEY_USERNAME, name);
                            note.put(helper.KEY_EMAIL, email);
                            note.put(helper.KEY_UEN, uen);
                            note.put(helper.KEY_PASSWORD, password);

                            docRef.set(note)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Done", Toast.LENGTH_LONG).show();

                                            helper.accountType = helper.KEY_ORGANISERS;
                                            Intent intent = new Intent(requireContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
