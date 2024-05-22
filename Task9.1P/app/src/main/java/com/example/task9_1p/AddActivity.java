package com.example.task9_1p;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends Activity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.imgv_return)
    ImageView imgv_return;

    @BindView(R.id.tvCommit)
    TextView tvCommit;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.edt_1)
    EditText edt_1;
    @BindView(R.id.edt_2)
    EditText edt_2;
    @BindView(R.id.edt_3)
    EditText edt_3;
    @BindView(R.id.edt_4)
    EditText edt_4;
    @BindView(R.id.edt_5)
    EditText edt_5;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    String type = "Lost";
    private double latitude;
    private double longitude;
    private String placeName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        tvTitle.setText("CREATE A NEW ADVERT");

        imgv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edt_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent i = new Intent(AddActivity.this, SearchPlaceActivity.class);
                    startActivityForResult(i, 1);
                    return true;
                }
                return false;
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    type = "Lost";
                } else {
                    type = "Found";
                }
            }
        });


        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp1 = edt_1.getText().toString();
                String temp2 = edt_2.getText().toString();
                String temp3 = edt_3.getText().toString();
                String temp4 = edt_4.getText().toString();
                String temp5 = edt_5.getText().toString();
                String lat = latitude + "";
                String lon = longitude + "";
                if (isEmpty(temp1) || isEmpty(temp2) || isEmpty(temp3) || isEmpty(temp4) || isEmpty(temp5) || isEmpty(lat) || isEmpty(lon)) {
                    Toast.makeText(AddActivity.this, " Incomplete information ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(temp2.length()!=10){
                    Toast.makeText(AddActivity.this, "please enter correct phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!temp4.matches("\\d{2}/\\d{2}/\\d{4}")){
                    Toast.makeText(AddActivity.this, "date format incorrect, please follow dd/mm/yyyy", Toast.LENGTH_SHORT).show();
                    return;
                }


                MySqliteOpenHelper.insert(AddActivity.this,
                        temp1, temp2, temp3, temp4, temp5,
                        type, lat, lon);
                Toast.makeText(AddActivity.this, " Set successfully ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if location is available
                if (ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(AddActivity.this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(AddActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Location found, do something with it
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    // Use Geocoder to get place name
                                    Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            placeName = addresses.get(0).getAddressLine(0);
                                            edt_5.setText(placeName);
                                            // Now you have the place name, use it as needed
                                            // For example, you can display it in a TextView or use it in further operations
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    // Location is null, handle the case where location is not available
                                    Toast.makeText(AddActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(AddActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure here
                                Toast.makeText(AddActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                placeName = data.getStringExtra("result");
                latitude = Double.parseDouble(data.getStringExtra("lat"));
                longitude = Double.parseDouble(data.getStringExtra("lon"));
                // 处理返回的数据
                if (placeName.isEmpty()) {
                    edt_5.setText("Lat:" + latitude + "-Lon:" + longitude);
                } else {
                    edt_5.setText(placeName);
                }
            }
        }
    }


    public static String getTodayData_3() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = df.format(new Date());
        return str;
    }

    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equalsIgnoreCase(input)) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
}