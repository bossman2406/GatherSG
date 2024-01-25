package com.gathersg.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splashscreen extends AppCompatActivity {
    Animation splashscreen;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        splashscreen = AnimationUtils.loadAnimation(this,R.anim.animation);
        imageView = findViewById(R.id.splashimage);
        imageView.setAnimation(splashscreen);


    }
}