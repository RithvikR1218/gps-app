package com.example.gps_final_project;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Switch;
import android.speech.tts.TextToSpeech;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.gps_final_project.databinding.ActivityMainBinding;
import android.widget.Toast;
import android.Manifest;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static final int DEFAULT_INTERVAL_TIMER = 5;
    public static final int FAST_INTERVAL_TIMER = 3;
    public static final int PERMISSIONS_FINE_LOCATIONS = 99;

    public TextView tv_lat;
    public TextToSpeech tv_lat_speak;

    public TextView tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address;

    Switch sw_locationsupdates,sw_gps;

    LocationRequest locationRequest;

    //Google API for location
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);

        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_INTERVAL_TIMER);
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_TIMER);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensor");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");
                }
            }
        });

        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else  {
                    Toast.makeText(this, "This app needs permissions to work",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATIONS);
            }
        }
    }

    private void updateUIValues(Location location) {
        if (location != null) {
            if (tv_lon != null) {
                tv_lon.setText(String.valueOf(location.getLongitude()));
            }
            if (tv_lat != null) {
                Log.d("MainActivity", "Latitude value: " + location.getLatitude());
                tv_lat.setText(String.valueOf(location.getLatitude()));
                tv_lat_speak = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {

                        // if No error is found then only it will run
                        if(i!=TextToSpeech.ERROR){
                            // To Choose language of speech
                            tv_lat_speak.setLanguage(Locale.UK);
                            String lat = tv_lat.getText().toString();
                            String lon = tv_lon.getText().toString();

                            String speakText = "Latitude is " + lat + " and Longitude is " + lon;

                            tv_lat_speak.speak(speakText,TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                });
            }
            if (tv_accuracy != null && location.hasAccuracy()) {
                tv_accuracy.setText(String.valueOf(location.getAccuracy()));
            }
            if (tv_altitude != null && location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            }
            if (tv_speed != null && location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            }
        } else {
            // Handle the case where the location is null
            if (tv_lat != null) {
                tv_lat.setText("Latitude is not available");
            }
            if (tv_lon != null) {
                tv_lon.setText("Longitude is not available");
            }
            if (tv_accuracy != null) {
                tv_accuracy.setText("Accuracy is not available");
            }
            if (tv_altitude != null) {
                tv_altitude.setText("Altitude is not available");
            }
            if (tv_speed != null) {
                tv_speed.setText("Speed is not available");
            }
        }
    }
}