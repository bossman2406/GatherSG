package com.gathersg.user.myPhotos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class myUserPhotos extends Fragment {
    FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ArrayList<String> nameList, descList;
    private ArrayList<Blob> imageList,profileList;

    private ListView eventListView;
    myPhotosAdapter myPhotosAdapter;
    FloatingActionButton floatingActionButton;


    public myUserPhotos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_my_photos, container, false);
        floatingActionButton = view.findViewById(R.id.myPhotoButton);
    recyclerView = view.findViewById(R.id.myPhotoView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    floatingActionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(),myPhotoAddPhoto.class);
            startActivity(intent);
        }
    });
        nameList = new ArrayList<>();
        descList = new ArrayList<>();
        imageList = new ArrayList<>();
        profileList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();


        CollectionReference photoCollection = db.collection(accountHelper.accountType).document(uid).collection(accountHelper.KEY_MYPHOTOS);
        photoCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore", "Error fetching documents", e);
                    return;
                }

                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    // Extract data from the document
                    String eventName = document.getString(eventHelper.KEY_EVENTNAME);
                    String eventDesc = document.getString(eventHelper.KEY_EVENTDESC);
                    Blob image = document.getBlob(eventHelper.KEY_EVENTIMAGE);
                   Blob profile = document.getBlob(accountHelper.KEY_IMAGE);
                    Log.d("Firestore", "EventName: " + eventName + ", EventDesc: " + eventDesc);


                    // Log the data for debugging

//                    if(date.equals(parseDateString(sel))){
//
//                    }

                    // Add data to lists
                    nameList.add(eventName);
                    descList.add(eventDesc);
                    imageList.add(image);
                    profileList.add(profile);

                    // Create an eventHelper object and add it to the list
                }
                myPhotosAdapter = new myPhotosAdapter(getContext(), nameList, descList, imageList, profileList);
                recyclerView.setAdapter(myPhotosAdapter);

                // Update the RecyclerView with the new data
                if (myPhotosAdapter != null) {
                    myPhotosAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });
    return view;
    }

}