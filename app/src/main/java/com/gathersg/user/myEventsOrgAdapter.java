package com.gathersg.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Blob;

import java.util.ArrayList;

public class myEventsOrgAdapter extends  RecyclerView.Adapter<myEventsOrgAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> nameList, descList, locNameList, dateList, orgList,statusList;
    private ArrayList<Double> latList, lonList;
    private ArrayList<Blob> imageList;
    private ArrayList<Long> signUpList;

    public myEventsOrgAdapter(Context context, ArrayList<String> nameList, ArrayList<String> descList,
                              ArrayList<String> dateList, ArrayList<String> orgList,
                              ArrayList<String> locNameList, ArrayList<Double> latList, ArrayList<Double> lonList,
                              ArrayList<Blob> imageList, ArrayList<String> statusList,ArrayList<Long> signUpList) {
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
        this.signUpList =signUpList;
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
        Log.d("temp tag",signUpList.get(position).toString());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,desc,date,organiser,loc,number;
        ImageView image;
        Button closeSignUpButton,cancelEventButton;
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

        protected void closeSignUp(){

        }
        protected void cancelEvent(){

        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}
