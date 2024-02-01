package com.gathersg.user.myPhotos;


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

public class myPhotosAdapter extends RecyclerView.Adapter<myPhotosAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<String> nameList;
    private final ArrayList<String> descList;
    private final ArrayList<Blob> imageList , profileList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore  db = FirebaseFirestore.getInstance();
      // Initialize FirebaseAuth instance
     // Initialize FirebaseFirestore instance

    public myPhotosAdapter(Context context, ArrayList<String> nameList, ArrayList<String> descList,
                           ArrayList<Blob> imageList, ArrayList<Blob> profile) {
        this.context = context;
        this.nameList = nameList;
        this.descList = descList;
        this.imageList = imageList;
        this.profileList = profile;
        Log.d("Firestore", "NameList: " + nameList.toString());
        Log.d("Firestore", "DescList: " + descList.toString());
        Log.d("Firestore", "ImageList: " + imageList.toString());
        Log.d("Firestore", "ProfileList: " + profileList.toString());



    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.myphotos_item, parent, false);
        return new ViewHolder(view);

        /// need change layout
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.desc.setText(descList.get(position));
        Blob profile = profileList.get(position);
        Blob image = imageList.get(position);
        // need change layout

        if (profile != null){
            byte[] profileData;
            profileData = profile.toBytes();
            Glide.with(context)
                    .load(profileData)
                    .into(holder.profile);
        }

        if (image != null) {
            byte[] imageData;
            imageData = image.toBytes();
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
        TextView name, desc;
        ImageView image,profile;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.photoName);
            desc = v.findViewById(R.id.photoDesc);
            image = v.findViewById(R.id.photoPhoto);
            profile = v.findViewById(R.id.photoProfile);


        }

//            public void bindData(int position) {
//                db.collection(//change ).document(nameList.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                String eventStatus = document.getString(eventHelper.KEY_EVENTSTATUS);
//                                String signUpStatus = document.getString(eventHelper.KEY_SIGNUPSTATUS);
//                                Map<String, Object> temp = new HashMap<>();
//                                temp.put(eventHelper.KEY_SIGNUPSTATUS, signUpStatus);
//                                temp.put(eventHelper.KEY_EVENTSTATUS, eventStatus);
//                                db.collection(accountHelper.KEY_VOLUNTEER)
//                                        .document(auth.getUid())
//                                        .collection(accountHelper.KEY_MYEVENTS)
//                                        .document(nameList.get(position))
//                                        .update(temp);
//                            }
//                        }
//                    }
//                });
            }

            // need change layout

    }


