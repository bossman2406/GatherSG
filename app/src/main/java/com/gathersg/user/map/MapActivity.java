package com.gathersg.user.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.gathersg.user.R;
import com.gathersg.user.mainpage.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private MapView mapView;
    private LatLng location,ME;
    private double lat;
    private double lon;
    private String name; // Use camelCase for variable names
    private final String TAG = "MapActivity"; // Use camelCase for variable names

    private SensorManager sensorManager;
    private Sensor magnetometer;
    FloatingActionButton button;
    private ImageView compassImageView;
    private double myLat;
    private double myLon;

    private static final int FIRST_DELAY = 3000;  // 3 seconds
    private static final int SECOND_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);


        lat = getIntent().getDoubleExtra("LATITUDE", 0.0);
        lon = getIntent().getDoubleExtra("LONGITUDE", 0.0);
        name = getIntent().getStringExtra("NAME"); // Use camelCase for variable names
        myLat = getIntent().getDoubleExtra("MYLATITUDE",0);
        myLon = getIntent().getDoubleExtra("MYLONGITUDE", 0);


        mapView = findViewById(R.id.mapView);
        button = findViewById(R.id.recentre);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clicked");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 15));
            }
        });
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Initialize compass ImageView
        compassImageView = findViewById(R.id.compassImageView);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(this);
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        location = new LatLng(lat, lon);
        ME = new  LatLng(myLat,myLon);


// Create a MarkerOptions object and set its properties
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(name);

// Set a custom marker size
        float markerSize = 1.0f; // Set your desired size
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker))
                .anchor(0.5f, 0.5f) // Center the marker on the specified position
                .infoWindowAnchor(0.5f, 0.5f); // Center the info window on the specified position

        MarkerOptions me = new MarkerOptions().position(ME).title("My Location");
        me.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)).anchor(0.5f, 0.5f) // Center the marker on the specified position
                .infoWindowAnchor(0.5f, 0.5f); // Center the info window on the specified position


// Add the marker to the map
        mMap.addMarker(markerOptions);
        mMap.addMarker(me);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 15));
        Log.d(TAG, "Zoomed to ME");

        new Handler().postDelayed(() -> {
            // Zoom out and move to the location after a delay
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15), 2000, null);
            Log.d(TAG, "Zoomed to location");
            // Post another delayed action to move back to ME after a delay
            new Handler().postDelayed(() -> {
                // Move back to ME after a delay
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ME, 15), 2000, null);
                Log.d(TAG, "Zoomed to ME");

            }, SECOND_DELAY);
        }, FIRST_DELAY);


        Log.d(TAG, "onMapReady");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magnetometer) {
            float azimuth = event.values[0];
            runOnUiThread(() -> updateCompass(azimuth)); updateCameraBearing(azimuth);// Run UI-related operations on the UI thread
            Log.d(TAG, "onSensorChanged: azimuth = " + azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
        Log.d(TAG, "onAccuracyChanged: " + accuracy);
    }

    private void updateCompass(float azimuth) {
        // Rotate the compass needle image based on the azimuth
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.navigation);
        Matrix matrix = new Matrix();
        matrix.postRotate(-azimuth); // Negative to match the device's rotation
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        compassImageView.setImageBitmap(rotatedBitmap);

        Log.d(TAG, "updateCompass: azimuth = " + azimuth);
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
    private void updateCameraBearing(float azimuth) {
        if (mMap != null) {
            CameraPosition currentCameraPosition = mMap.getCameraPosition();
            CameraPosition newCameraPosition = new CameraPosition.Builder(currentCameraPosition)
                    .bearing(azimuth)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        }
    }

}
