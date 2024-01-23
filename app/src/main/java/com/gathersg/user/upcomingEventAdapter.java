package com.gathersg.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class upcomingEventAdapter extends RecyclerView.Adapter<upcomingEventAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> nameList;
    private final ArrayList<String> descList;
    private final ArrayList<String> locNameList;
    private final ArrayList<String> dateList;
    private final ArrayList<String> orgList;
    private final ArrayList<Double> latList;
    private final ArrayList<Double> lonList;
    private final ArrayList<Blob> imageList;

    public upcomingEventAdapter(Context context, ArrayList<String> nameList, ArrayList<String>
            descList, ArrayList<String> dateList,
                                ArrayList<String> orgList, ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                                ArrayList<Blob> imageList) {
        this.context = context;
        this.nameList = nameList;
        this.descList = descList;
        this.locNameList = locNameList;
        this.orgList = orgList;
        this.dateList = dateList;
        this.latList = latList;
        this.lonList = lonList;
        this.imageList = imageList;
        logArrayList("nameList", nameList);
        logArrayList("descList", descList);
        logArrayList("locNameList", locNameList);
        logArrayList("orgList", orgList);
        logArrayList("dateList", dateList);
        logImageArrayList("imageList", imageList);


    }

    private void logArrayList(String name, ArrayList<String> list) {
        Log.d("ArrayList", name + ": " + list.toString());
    }

    private void logImageArrayList(String name, ArrayList<Blob> list) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(name).append(": [");

        for (Blob blob : list) {
            if (blob != null) {
                logMessage.append("Image, ");
            } else {
                logMessage.append("null, ");
            }
        }

        logMessage.append("]");
        Log.d("ArrayList", logMessage.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.desc.setText(descList.get(position));
        holder.date.setText(dateList.get(position));
        holder.organiser.setText(orgList.get(position));
        holder.loc.setText(locNameList.get(position));
        Blob temp = imageList.get(position);

        if (temp != null) {
            byte[] imageData;
            imageData = temp.toBytes();
            Glide.with(context)
                    .load(imageData)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, date, organiser, loc;
        ImageView image;
        Button volunteerSignUp;
        accountHelper helper;
        eventHelper event;
        FirebaseAuth auth;
        FirebaseUser user;
        FirebaseFirestore db;

        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.eventCardName);
            desc = v.findViewById(R.id.eventCardDesc);
            date = v.findViewById(R.id.eventCardDate);
            organiser = v.findViewById(R.id.eventCardOrg);
            loc = v.findViewById(R.id.eventCardLoc);
            image = v.findViewById(R.id.eventCardImage);
            volunteerSignUp = v.findViewById(R.id.volunteerSignUpButton);
            int position = getAdapterPosition();
                auth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String uid = currentUser.getUid();

            volunteerSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        eventStatusService.signUpForEvent(nameList.get(position), uid, context, v);
                    }
                }
            });
                    }


            }
        }






