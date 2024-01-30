package com.gathersg.user.scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Blob;

import java.util.ArrayList;

public class eventItem  extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> nameList;

    public eventItem(Context context, ArrayList<String> nameList) {
        super(context, android.R.layout.simple_spinner_item ,nameList);
        this.context = context;
        this.nameList = nameList;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = rowView.findViewById(android.R.id.text1);
        textView.setText(nameList.get(position));

        return rowView;
    }
}
