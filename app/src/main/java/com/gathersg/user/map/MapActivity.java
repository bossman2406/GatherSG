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
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private ImageView compassImageView;
    private double myLat;
    private double myLon;

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

        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME").snippet("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.place)));
        Marker locationMarker = mMap.addMarker(new MarkerOptions().position(location).title(name)); // Use camelCase for variable names
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

        Log.d(TAG, "onMapReady");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magnetometer) {
            float azimuth = event.values[0];
            runOnUiThread(() -> updateCompass(azimuth)); // Run UI-related operations on the UI thread
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
}
