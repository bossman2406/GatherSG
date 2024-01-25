package com.gathersg.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCode extends AppCompatActivity {

    Button button;
    ImageView imageView;

  FirebaseFirestore db;
  FirebaseAuth auth;


    public QrCode() {
        // Required empty public constructor
    }


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
       setContentView(R.layout.fragment_qr_code);
        button = findViewById(R.id.QRCodeGenerate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeGenerate();
            }
        });
        imageView = findViewById(R.id.eventQRCode);

    }

    protected void QRCodeGenerate() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            String temp = accountHelper.accountType;
            FirebaseUser currentUser = auth.getCurrentUser();
            String uid = currentUser.getUid();
            BitMatrix bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        // Add your custom behavior here
        // For example, navigate to another activity

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

        // If you want to keep the default behavior (finish the activity), call super.onBackPressed()
        super.onBackPressed();
    }
}