package com.gathersg.user.myevents;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.gathersg.user.helpers.eventHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myEventsVolAdapter extends RecyclerView.Adapter<myEventsVolAdapter.ViewHolder> {
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
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore  db = FirebaseFirestore.getInstance();
      // Initialize FirebaseAuth instance
     // Initialize FirebaseFirestore instance

    public myEventsVolAdapter(Context context, ArrayList<String> nameList, ArrayList<String> descList,
                              ArrayList<String> dateList, ArrayList<String> orgList,
                              ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                              ArrayList<Blob> imageList, ArrayList<String> statusList) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.myeventvol_cell, parent, false);
        return new ViewHolder(view);

        /// need change layout
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.date.setText(dateList.get(position));
        holder.status.setText(statusList.get(position));
        Blob temp = imageList.get(position);
        // need change layout

        if (temp != null) {
            byte[] imageData;
            imageData = temp.toBytes();
            Glide.with(context)
                    .load(imageData)
                    .into(holder.image);
        }
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, status;
        ImageView image;

        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.myEventCardName);

            date = v.findViewById(R.id.myEventCardDate);
            status = v.findViewById(R.id.myEventCardStatus);
            image = v.findViewById(R.id.myEventCardImage);
            auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth instance
            db = FirebaseFirestore.getInstance();  // Initialize FirebaseFirestore instance
        }

            public void bindData(int position) {
                db.collection(eventHelper.KEY_EVENTS).document(nameList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String eventStatus = document.getString(eventHelper.KEY_EVENTSTATUS);
                                String signUpStatus = document.getString(eventHelper.KEY_SIGNUPSTATUS);
                                Map<String, Object> temp = new HashMap<>();
                                temp.put(eventHelper.KEY_SIGNUPSTATUS, signUpStatus);
                                temp.put(eventHelper.KEY_EVENTSTATUS, eventStatus);
                                db.collection(accountHelper.KEY_VOLUNTEER)
                                        .document(auth.getUid())
                                        .collection(accountHelper.KEY_MYEVENTS)
                                        .document(nameList.get(position))
                                        .update(temp);
                            }
                        }
                    }
                });
            }

            // need change layout

    }

}

