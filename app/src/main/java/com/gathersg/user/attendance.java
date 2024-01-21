package com.gathersg.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class attendance extends Fragment {
    WebView webView;
    Button button;


    public attendance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendace, container, false);

        webView = view.findViewById(R.id.webView);
        button = view.findViewById(R.id.eventScanButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();

            }
        });
        // Enable JavaScript (if needed)
        webView.getSettings().setJavaScriptEnabled(true);

        // Load a URL or HTML content
        webView.loadUrl("https://www.giving.sg/home");
        return view;
    }

    protected void scanQrCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(requireActivity());
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Scan QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode
                , data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                //Next task here
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


}