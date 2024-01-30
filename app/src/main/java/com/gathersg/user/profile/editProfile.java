package com.gathersg.user.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gathersg.user.R;
import com.gathersg.user.helpers.accountHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfile extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int CAMERA_REQUEST_CODE = 123;
    private static final int MY_CAMERA_PERMISSION_REQUEST = 123;

    EditText editUsername, editPassword, editBio, editEmail, editNumber;
    TextView editDOB;
    Button saveButton, imageButton;
    String usernameUser, passwordUser, bioUser, emailUser, numberUser,dobUser;

    Blob imageUser;
    DocumentReference reference;
    CircleImageView profilePic;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Dialog dialog;
    private byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        String temp = accountHelper.accountType;
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser.getUid();

        reference = db.collection(temp).document(uid);

        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        editBio = findViewById(R.id.edit_bio);
        editEmail = findViewById(R.id.edit_email);
        editNumber = findViewById(R.id.edit_number);
        editDOB = findViewById(R.id.edit_DOB);
        imageButton = findViewById(R.id.galleryBtn);
        profilePic = findViewById(R.id.previewImage);
        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDateButtonClick();
            }
        });
        saveButton = findViewById(R.id.save_button);
        dialog = new Dialog(editProfile.this);
        dialog.setContentView(R.layout.popup_layout);
        imageButton.setOnClickListener(new View.OnClickListener() {
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
                        if (ContextCompat.checkSelfPermission(editProfile.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, request it
                            ActivityCompat.requestPermissions(editProfile.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_REQUEST);
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

            }
        });


        showData();

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                usernameUser = editUsername.getText().toString();
                passwordUser = editPassword.getText().toString();
                bioUser = editBio.getText().toString();
                emailUser = editEmail.getText().toString();
                numberUser = editNumber.getText().toString();
                dobUser = editDOB.getText().toString();

                Map<String, Object> userData = new HashMap<>();
                userData.put(accountHelper.KEY_USERNAME, usernameUser);
                userData.put(accountHelper.KEY_PASSWORD, passwordUser);
                userData.put(accountHelper.KEY_NUMBER, numberUser);
                userData.put(accountHelper.KEY_EMAIL, emailUser);
                userData.put(accountHelper.KEY_BIO, bioUser);
                userData.put(accountHelper.KEY_DOB, dobUser);
                if (imageData != null) {
                    Blob imageBlob = Blob.fromBytes(imageData);
                    userData.put(accountHelper.KEY_IMAGE, imageBlob);
                }
                reference.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(editProfile.this, "Saved", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }
        );
}


    public boolean isUsernameChanged(){
        if (!usernameUser.equals(editUsername.getText().toString())){
            usernameUser = editUsername.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    public boolean isPasswordChanged(){
        if (!passwordUser.equals(editPassword.getText().toString())){
            passwordUser = editPassword.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    public boolean isBioChanged(){
        if (!bioUser.equals(editBio.getText().toString())){
            bioUser = editBio.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    public boolean isEmailChanged(){
        if (!emailUser.equals(editEmail.getText().toString())){
            emailUser = editEmail.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    public boolean isNumberChanged(){
        if (!numberUser.equals(editNumber.getText().toString())){
            numberUser = editNumber.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    public boolean isDOBChanged() {
        if (!dobUser.equals(editDOB.getText().toString())) {
            dobUser = editDOB.getText().toString();
            return true;
        } else {
            return false;
        }
    }

        private void showData() {
            String temp = accountHelper.accountType;
            FirebaseUser currentUser = auth.getCurrentUser();
            String uid = currentUser.getUid();

            DocumentReference userData = db.collection(temp).document(uid);
            userData.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    usernameUser = documentSnapshot.getString(accountHelper.KEY_USERNAME);
                                    passwordUser = documentSnapshot.getString(accountHelper.KEY_PASSWORD);
                                    dobUser = documentSnapshot.getString(accountHelper.KEY_DOB);
                                    numberUser  = documentSnapshot.getString(accountHelper.KEY_NUMBER);
                                    emailUser = documentSnapshot.getString(accountHelper.KEY_EMAIL);
                                    bioUser = documentSnapshot.getString(accountHelper.KEY_BIO);
                                    imageUser = documentSnapshot.getBlob(accountHelper.KEY_IMAGE);

                                    editDOB.setText(dobUser);
                                    editUsername.setText(usernameUser);
                                    editPassword.setText(passwordUser);

                                    editBio.setText(bioUser);
                                    editNumber.setText(numberUser);
                                    editEmail.setText(emailUser);
                                    if(imageUser != null) {
                                        byte[] imageData;
                                        imageData = imageUser.toBytes();
                                        Glide.with(getApplicationContext())
                                                .load(imageData)
                                                .into(profilePic);
                                    }

                                }
                            }
                        }
                    });
                }
            });
    }
    private void onSelectDateButtonClick() {
        // Implement the logic when the select date button is clicked
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (isDateValid(selectedDate)) {
                            // Date is valid (at least 13 years before the current date)
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String formattedDate = dateFormat.format(selectedDate.getTime());
                            Toast.makeText(getApplicationContext(), "Selected Date: " + formattedDate, Toast.LENGTH_LONG).show();
                            editDOB.setText(formattedDate);
                        } else {
                            // Date is not valid
                            Toast.makeText(getApplicationContext(), "Please select a date at least 13 years ago.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }
    private boolean isDateValid(Calendar selectedDate) {
        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        // Set the minimum age to 13 years
        currentDate.add(Calendar.YEAR, -13);

        // Check if the selected date is at least 13 years before the current date
        return selectedDate.before(currentDate);
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
                            .into(profilePic);
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
                            .into(profilePic);
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
