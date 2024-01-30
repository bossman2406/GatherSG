package com.gathersg.user.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.profile.editProfile;
import com.gathersg.user.profile.personal_info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class userProfile extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    FragmentContainerView fragmentContainerView;
    personal_info personalInfo;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView username,uidText,edit,change;
    CircleImageView imageView;
    editProfile editProfile;

    Dialog dialog;
    public userProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        personalInfo = new personal_info();
        username = view.findViewById(R.id.usernameProfile);
        uidText = view.findViewById(R.id.uidProfile);
        imageView = view.findViewById(R.id.profile_image);
        edit = view.findViewById(R.id.editProfile);
        change = view.findViewById(R.id.changePassword);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.forgetpassword);
        
        editProfile = new editProfile();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), com.gathersg.user.profile.editProfile.class);
                startActivity(intent);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            
            private EditText email,text;
            
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                email = dialog.findViewById(R.id.emailLogin);
                text = dialog.findViewById(R.id.textView);
                
                
                AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), text.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // User reauthenticated successfully
                                    // Proceed with changing the password
                                    changePassword( text.getText().toString());
                                    email.setText("Enter Email");
                                    text.setText("Enter New Passwod");
                                    
                                } else {
                                    // Handle reauthentication failure
                                    Toast.makeText(getContext(), "Reauthentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        //add db to get username , uid and image;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();
        String temp = accountHelper.accountType;
        db.collection(temp).document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        String name = snapshot.getString(accountHelper.KEY_USERNAME);
                        username.setText(name);
                        uidText.setText("UID: " + uid);
                        Blob image = snapshot.getBlob(accountHelper.KEY_IMAGE);
                        if(image != null) {
                            byte[] imageData;
                            imageData = image.toBytes();
                            Glide.with(getActivity())
                                    .load(imageData)
                                    .into(imageView);
                        }
                    }
                }
            }
        });

        uidText.setText("UID: " + uid);
        fragmentContainerView = view.findViewById(R.id.userfragmentContainer);
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentTransaction;
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.userfragmentContainer, personalInfo)
                    .addToBackStack(null)
                    .commit();
        }
        return view;
    }
    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password changed successfully
                            // Provide user feedback, e.g., show a Toast
                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle password change failure
                            Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}