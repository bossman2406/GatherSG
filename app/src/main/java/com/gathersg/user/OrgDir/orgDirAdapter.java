package com.gathersg.user.OrgDir;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gathersg.user.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class orgDirAdapter extends RecyclerView.Adapter<orgDirAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> nameList,numList,uenList;

    private final ArrayList<Blob> imageList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Initialize FirebaseAuth instance
    // Initialize FirebaseFirestore instance

    public orgDirAdapter(Context context, ArrayList<String> nameList,ArrayList<String> numList,ArrayList<String> uenList,
                              ArrayList<Blob> imageList) {
        this.context = context;
        this.nameList = nameList;
        this.numList =numList;
        this.uenList = uenList;
        this.imageList = imageList;

        logArrayList("nameList", nameList);
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
    public orgDirAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orgdiritem, parent, false);
        return new orgDirAdapter.ViewHolder(view);

        /// need change layout
    }

    @Override
    public void onBindViewHolder(@NonNull orgDirAdapter.ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.number.setText("Number: " + numList.get(position));
        holder.uen.setText("UEN: " + uenList.get(position));
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
        TextView name, uen, number;
        CircleImageView image;

        public ViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.orgName);

            uen = v.findViewById(R.id.orgUen);
            number = v.findViewById(R.id.orgNum);
            image = v.findViewById(R.id.orgImage);


            auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth instance
            db = FirebaseFirestore.getInstance();  // Initialize FirebaseFirestore instance
        }



        // need change layout

    }

}