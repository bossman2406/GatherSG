package com.gathersg.user;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class eventOrganiserAddPhoto extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int CAMERA_REQUEST_CODE = 123;
    private static final int MY_CAMERA_PERMISSION_REQUEST = 123;
    ImageView eventImage;
    Button selectImage;
    FloatingActionButton addDetails;
    TextView def;
    Dialog dialog;
    private byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organiser_add_photo);
        eventImage = findViewById(R.id.eventOrganiserAddPhotoImage);
        selectImage = findViewById(R.id.eventOrganierAddPhotoButton);
        addDetails = findViewById(R.id.eventOrganiserAddDetails);
        def = findViewById(R.id.defaultText);
        def.setVisibility(View.VISIBLE);
        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), eventOrganiserAdd.class);
                intent.putExtra("imageData", imageData);
                startActivity(intent);
                finish();
            }
        });
        dialog = new Dialog(eventOrganiserAddPhoto.this);
        dialog.setContentView(R.layout.popup_layout);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button gallery = dialog.findViewById(R.id.gallery);
                Button takePhoto = dialog.findViewById(R.id.takePhoto);
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE_PICK);
                    }
                });
                takePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(eventOrganiserAddPhoto.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, request it
                            ActivityCompat.requestPermissions(eventOrganiserAddPhoto.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_REQUEST);
                        } else {
                            // Permission is already granted, proceed with the camera operation
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            }
                        }
                    }
                });
                dialog.show();
                def.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_PICK && data != null) {
            Uri selectedImage = data.getData();

            if (selectedImage != null) {
                try {
                    imageData = convertImageUriToByteArray(selectedImage);

                    Glide.with(this)
                            .load(selectedImage)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                            )
                            .into(eventImage);
                    dialog.dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ImagePickError", "Error converting image URI to byte array: " + e.getMessage());
                }
            } else {
                Log.e("ImagePickError", "Selected image URI is null");
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // The photo is captured and available in the 'data' Intent
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            if (photo != null) {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    imageData = stream.toByteArray();
                    Glide.with(this)
                            .load(photo)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                            )
                            .into(eventImage);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ImagePickError", "Error converting image URI to byte array: " + e.getMessage());
                }

                // Now, you can use the 'photo' Bitmap as needed
            }
        }

    }

    private byte[] convertImageUriToByteArray(Uri imageUri) throws Exception {
        ContentResolver contentResolver = getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(imageUri);

        if (inputStream != null) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                inputStream.close();
            }

            return output.toByteArray();
        } else {
            throw new Exception("Failed to open InputStream for selected image URI");
        }
    }
}