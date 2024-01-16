package com.gathersg.user;


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
import com.google.firebase.firestore.Blob;

import java.util.ArrayList;

public class myEventsVolAdapter extends RecyclerView.Adapter<myEventsVolAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList,statusList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Blob> imageList;

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
            // need change layout
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}

