package com.gathersg.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;


public class userProfile extends Fragment {

    FragmentContainerView fragmentContainerView;
    personal_info personalInfo;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView username,uid;
    CircleImageView imageView;

    public userProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        personalInfo = new personal_info();
        username = view.findViewById(R.id.usernameProfile);
        uid = view.findViewById(R.id.uidProfile);
        //add db to get username , uid and image;
        username.setText();
        uid.setText();
        fragmentContainerView = view.findViewById(R.id.userfragmentContainer);
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentTransaction;
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.userfragmentContainer, personalInfo)
                    .addToBackStack(null)
                    .commit();
        }
        return view;
    }

    public void onBackPressed() {
        requireActivity().onBackPressed();
    }


}