package com.gathersg.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class userProfile extends Fragment {

    FragmentContainerView fragmentContainerView;
    personal_info personalInfo;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction ;

    public userProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        personalInfo =new personal_info();
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