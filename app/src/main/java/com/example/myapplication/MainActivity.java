package com.example.myapplication;



import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.map.NaverMapSdk;

import java.io.IOException;

import java.util.List;
import java.util.Locale;


import com.openai.api.ChatCompletion;
import com.openai.api.Gpt3;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView txtLatitude;
    private TextView txtLongitude;
    private LocationManager locationManager;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Naver Maps SDK 초기화
        NaverMapSdk.getInstance(getApplicationContext()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("rorz7rmft4")
        );

        setContentView(R.layout.map);

        Gpt3.configure("YOUR_API_KEY");


        // 위도, 경도
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            // Permission is already granted, proceed with obtaining location
            obtainLocation();

            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
    }



    private void obtainLocation() {
        Location location;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
                
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Perform necessary tasks using the location information
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String latitudeText = getString(R.string.latitude_text, latitude);
            String longitudeText = getString(R.string.longitude_text, longitude);

            txtLatitude.setText(latitudeText);
            txtLongitude.setText(longitudeText);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
                if (addresses != null && addresses.size() > 0) {
                    String addressLine = addresses.get(0).getAddressLine(0);
                    Log.e("위치", addresses.get(0).toString());
                    Log.e("주소", addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    // 위치정보 권한 체크 로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, proceed with obtaining location
                obtainLocation();
            } else {
                Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show();
                // Location permissions denied, handle according
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String latitudeText = getString(R.string.latitude_text, latitude);
        String longitudeText = getString(R.string.longitude_text, longitude);

        txtLatitude.setText(latitudeText);
        txtLongitude.setText(longitudeText);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses != null && addresses.size() > 0) {
                String addressLine = addresses.get(0).getAddressLine(0);
                Log.e("위치", addresses.get(0).toString());
                Log.e("주소", addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy() called");
    }
}
