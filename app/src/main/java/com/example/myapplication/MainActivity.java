package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.openai.api.ChatCompletion;
import com.openai.api.Gpt3;


public class MainActivity extends AppCompatActivity implements LocationListener {

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {
    private TextView txtLatitude;
    private TextView txtLongitude;

    private LocationManager locationManager;
    private Geocoder geocoder;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat;
    private double latitude;
    private double longitude;
    private gps_db dbHelper;

    Button gptbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터베이스 도우미 객체 생성
        dbHelper = new gps_db(MainActivity.this);

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

        insertLocationPeriodically();

        gptbtn = findViewById(R.id.gptbtn);
        gptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 화면으로 전환
                Intent intent = new Intent(MainActivity.this, gpt_api.class);
                startActivity(intent);
            }
        });

    }

    private void obtainLocation() {
        Location location;
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //현재 시간
        String currentTime1 = mFormat.format(new Date()); // 현재 시간을 currentTime 변수에 할당

        dbHelper.insertLocation(latitude, longitude, currentTime1);

        // 위치 권한이 허용되지 않았을 경우 메서드 종료
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // GPS 공급자를 사용하여 마지막으로 알려진 위치를 가져옴
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // 위치 정보를 사용하여 필요한 작업 수행
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // 데이터베이스에 위치 정보 삽입
            gps_db dbHelper = new gps_db(MainActivity.this);
            dbHelper.insertLocation(latitude, longitude, currentTime);

            // 현재 날짜 가져오기
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // 데이터베이스 초기화
            if (!dbHelper.checkDateExists(currentDate)) {
                dbHelper.resetDatabase();
                dbHelper.insertDate(currentDate);
            }

            try {
                // 위도와 경도에 해당하는 주소 목록을 최대 10개까지 가져옴
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
                if (addresses != null && addresses.size() > 0) {
                    // 첫 번째 주소의 주소 문자열과 전체 주소 정보를 로그로 출력
                    String addressLine = addresses.get(0).getAddressLine(0);
                    Log.e("위치", addresses.get(0).toString());
                    Log.e("주소", addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 위치정보 권한 체크 로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // 위치 권한 요청의 결과를 처리
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용된 경우, 위치 정보를 가져오는 작업 계속 진행
                obtainLocation();
            } else {
                // 위치 권한이 거부된 경우, 해당 처리를 수행
                Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show();

                // 위치 권한 거부에 대한 처리를 추가적으로 구현할 수 있음
                // Location permissions denied, handle according
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //새로운 위치의 위도와 경도를 가져옴.

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
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        Log.d("MainActivity", "onDestroy() called");
    }

    private void insertLocationPeriodically() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 30분마다 현재 위치 정보를 데이터베이스에 삽입
                obtainLocation();

                // 다음 호출을 30분 뒤에 실행
                handler.postDelayed(this, 30 * 60 * 1000);
            }
        }, 30 * 60 * 1000); // 앱 시작 후 첫 호출까지 30분 대기
    }
}