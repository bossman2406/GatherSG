package com.gathersg.user.OrgDir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gathersg.user.OrgDir.orgDirAdapter;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.map.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class OrganisationDirectory extends Fragment {
    RecyclerView recyclerView;
    private ArrayList<String> nameList, numberList, uenList;
    private ArrayList<Blob> imageList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    com.gathersg.user.OrgDir.orgDirAdapter orgDirAdapter;

    public OrganisationDirectory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_organisation_directory, container, false);
       recyclerView = view.findViewById(R.id.orgDirRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists here
        nameList = new ArrayList<>();
        numberList = new ArrayList<>();
        imageList = new ArrayList<>();
        uenList = new ArrayList<>();
        setList();
       return view;
    }
    private void setList()

    {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        CollectionReference eventsCollection = db.collection(accountHelper.KEY_ORGANISERS);
        eventsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore", "Error fetching documents", e);
                    return;
                }

                // Clear lists to avoid duplicates
                nameList.clear();
                numberList.clear();
                imageList.clear();
                uenList.clear();

                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    // Extract data from the document
                    String name = document.getString(accountHelper.KEY_USERNAME);
                    String number = document.getString(accountHelper.KEY_NUMBER);
                    String uen = document.getString(accountHelper.KEY_UEN);
                    Blob image = document.getBlob(accountHelper.KEY_IMAGE);


                        nameList.add(name);
                        numberList.add(number);
                        uenList.add(uen);
                        imageList.add(image);


                    // Create an eventHelper object and add it to the list

                }
                orgDirAdapter = new orgDirAdapter(getContext(),nameList,numberList,uenList,imageList);

                recyclerView.setAdapter(orgDirAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + numberList.get(position)));

                                // Check if there's an app to handle the Intent before starting
                                if (callIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                    startActivity(callIntent);
                                } else {
                                    // Handle the case where no app can handle the dial action
                                    Toast.makeText(getContext(), "No app to handle phone calls", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }));


                // Update the RecyclerView with the new data
                if (orgDirAdapter != null) {
                    orgDirAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }// Notify adapter of data change
            }
        });
    }
}