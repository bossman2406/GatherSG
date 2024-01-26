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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myEventsOrgAdapter extends RecyclerView.Adapter<myEventsOrgAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> nameList;
    private final ArrayList<String> descList;
    private final ArrayList<String> locNameList;
    private final ArrayList<String> dateList;
    private final ArrayList<String> orgList;
    private final ArrayList<String> statusList;
    private final ArrayList<Double> latList;
    private final ArrayList<Double> lonList;
    private final ArrayList<Blob> imageList;
    private final ArrayList<Long> signUpList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();


    public myEventsOrgAdapter(Context context, ArrayList<String> nameList, ArrayList<String> descList,
                              ArrayList<String> dateList, ArrayList<String> orgList,
                              ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                              ArrayList<Blob> imageList, ArrayList<String> statusList, ArrayList<Long> signUpList) {
        this.context = context;
        this.nameList = nameList;
        this.descList = descList;
        this.locNameList = locNameList;
        this.orgList = orgList;
        this.dateList = dateList;
        this.latList = latList;
        this.lonList = lonList;
        this.imageList = imageList;
        this.statusList = statusList;
        this.signUpList = signUpList;
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
    public myEventsOrgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.myeventorg_cell, parent, false);
        return new myEventsOrgAdapter.ViewHolder(view);

        /// need change layout
    }

    @Override
    public void onBindViewHolder(@NonNull myEventsOrgAdapter.ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.date.setText(dateList.get(position));
        holder.desc.setText(descList.get(position));
        holder.organiser.setText(orgList.get(position));
        holder.loc.setText(locNameList.get(position));
        Log.d("temp tag", signUpList.get(position).toString());
        holder.number.setText((signUpList.get(position)).toString());

        Blob temp = imageList.get(position);
        // need change layout

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
        TextView name, desc, date, organiser, loc, number;
        ImageView image;
        Button closeSignUpButton, cancelEventButton;
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.myEventOrgName);
            desc = v.findViewById(R.id.myEventOrgDesc);
            date = v.findViewById(R.id.myEventOrgDate);
            organiser = v.findViewById(R.id.myEventOrgOrg);
            loc = v.findViewById(R.id.myEventOrgLoc);
            image = v.findViewById(R.id.eventCardImage);
            number = v.findViewById(R.id.myEventOrgSignUp);
            closeSignUpButton = v.findViewById(R.id.closeSignUpButton);
            closeSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeSignUp();
                }
            });
            cancelEventButton = v.findViewById(R.id.cancelEventButton);
            cancelEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelEvent();
                }
            });
            // need change layout
        }

        protected void closeSignUp() {

            Map<String, Object> signUp = new HashMap<>();
            signUp.put(eventHelper.KEY_SIGNUPSTATUS,eventHelper.KEY_CLOSE);
            db.collection(eventHelper.KEY_EVENTS).document(nameList.get(getAdapterPosition())).update(signUp).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context.getApplicationContext(),"Sign Up Closed",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });





        }

        protected void cancelEvent() {


        }

    }
    public void deleteEvent(int position){

        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();

        // Remove the event document from the collection

        db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context.getApplicationContext(), "Event Deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to delete the event
                        Toast.makeText(context.getApplicationContext(), "Event Delete Unsuccessful",Toast.LENGTH_SHORT).show();
                        Log.e("DeleteEvent", "Error deleting event", e);
                    }
                });
        CollectionReference collectionReference = db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position)).collection(eventHelper.KEY_EVENTSIGNUP);

// Delete all documents in the collection
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    // Handle errors
                }
            }
        });
        db.collection(accountHelper.KEY_ORGANISERS).document(uid).collection(accountHelper.KEY_MYEVENTS).document(nameList.get(position)).delete();

    }
}
