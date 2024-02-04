package com.gathersg.user.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class personal_info extends Fragment {

    TextView bio,email,number,via;
    FirebaseFirestore db;
    FirebaseAuth auth;



    public personal_info() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_personal_info, container, false);
       bio = view.findViewById(R.id.bioAccount);
       number = view.findViewById(R.id.numberAccount);
       email= view.findViewById(R.id.emailAccount);
       via = view.findViewById(R.id.viaAccount);
       FirebaseFirestore.getInstance();
       FirebaseAuth.getInstance();
       showUserData(view);

       return view;
    }
    public void showUserData(View view) {

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String temp = accountHelper.accountType;
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();
        DocumentReference userData = db.collection(temp).document(uid);
        userData.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Access data directly from the documentSnapshot
                    String temp1 = documentSnapshot.getString(accountHelper.KEY_EMAIL);
                    String temp2 = documentSnapshot.getString(accountHelper.KEY_NUMBER);
                    String temp3 = documentSnapshot.getString(accountHelper.KEY_BIO);
                    Long temp4 = documentSnapshot.getLong(accountHelper.KEY_VIA);
                    if (temp1 != null) {
                        email.setText(temp1);
                    }

                    if (temp2 != null) {
                        number.setText(temp2);
                    }

                    if (temp3 != null) {
                        bio.setText(temp3);
                    }
                    if (temp4 !=null){
                        via.setText(String.valueOf(temp4));
                    }

                }
            }
        });

    }
}