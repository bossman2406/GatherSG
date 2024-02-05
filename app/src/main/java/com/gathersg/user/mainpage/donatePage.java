package com.gathersg.user.mainpage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gathersg.user.R;


public class donatePage extends Fragment {


    private WebView webView;

    public donatePage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_page, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webView);

        // Enable JavaScript (if needed)
        webView.getSettings().setJavaScriptEnabled(true);

        // Load a URL or HTML content
        webView.loadUrl("https://www.giving.sg/");
        // webView.loadData("<html><body><h1>Hello, WebView!</h1></body></html>", "text/html", "UTF-8");

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

}