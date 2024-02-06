package com.gathersg.user.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.VideoView;

import com.gathersg.user.R;
import com.gathersg.user.login.Login;

public class splashscreen extends AppCompatActivity {
    VideoView videoView;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        videoView = findViewById(R.id.splashimage);


        String path = "android.resource://com.gathersg.user/" + R.raw.splashscreenraw;

        Uri uri = Uri.parse(path);

        videoView.setVideoURI(uri);

        mediaPlayer = MediaPlayer.create(this, R.raw.splashscreenmusic);
        mediaPlayer.setLooping(false);

       videoView.start();
          mediaPlayer.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splashscreen.this, Login.class));
                finish();
            }

        },5000);


    }
}