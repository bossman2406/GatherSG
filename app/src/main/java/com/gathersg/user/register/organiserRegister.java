package com.gathersg.user.register;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gathersg.user.mainpage.MainActivity;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class organiserRegister extends Fragment {

    private static final String TAG = "user";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String uid;
    FirebaseAuth mAuth;
    EditText password, uen, name, email;
    Button register;
    accountHelper helper;
    CheckBox checkBox;
    Boolean check;

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
        checkBox = view.findViewById(R.id.checkBoxTerms);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = isChecked;
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.terms_and_conditions);
                dialog.show();
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
        if (check.equals(false)){
            Toast.makeText(getContext(), "Accept Terms and Conditions", Toast.LENGTH_SHORT).show();
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

                            DocumentReference docRef = db.collection(accountHelper.KEY_ORGANISERS).document(uid);
                            CollectionReference itemsCollection = docRef.collection("myEvents");

                            Map<String, Object> note = new HashMap<>();
                            note.put(accountHelper.KEY_USERNAME, name);
                            note.put(accountHelper.KEY_EMAIL, email);
                            note.put(accountHelper.KEY_UEN, uen);
                            note.put(accountHelper.KEY_PASSWORD, password);
                            note.put(accountHelper.KEY_DOB,null);
                            note.put(accountHelper.KEY_NUMBER,null);
                            note.put(accountHelper.KEY_BIO,null);
                            Drawable drawable = getResources().getDrawable(R.drawable.default_icon);

// Convert the Drawable to a Bitmap
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

// Convert the Bitmap to a byte array
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageData = baos.toByteArray();

// Convert the byte array to a Blob
                            com.google.firebase.firestore.Blob imageBlob = com.google.firebase.firestore.Blob.fromBytes(imageData);
                            note.put(accountHelper.KEY_IMAGE,imageBlob);


                            docRef.set(note)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Done", Toast.LENGTH_LONG).show();

                                            accountHelper.accountType = accountHelper.KEY_ORGANISERS;
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
