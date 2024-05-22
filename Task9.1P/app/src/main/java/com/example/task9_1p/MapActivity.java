package com.example.task9_1p;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.imgv_return)
    ImageView imgv_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        tvTitle.setText("MAP Markers");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);

        imgv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap mMap) {
            List<Bean> list_all = MySqliteOpenHelper.query(MapActivity.this);
            LatLng[] points = new LatLng[list_all.size()];
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < list_all.size(); i++) {
                Bean bean = list_all.get(i);
                points[i] = new LatLng(Double.parseDouble(bean.latitude), Double.parseDouble(bean.longitude));
                mMap.addMarker(new MarkerOptions().position(points[i]).title("Location:"+bean.value4+" (Type:"+bean.type+")"+" Name:"+bean.value0));
                builder.include(points[i]);
            }
            LatLngBounds bounds = builder.build();
            int padding = 300;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
        }
    };

}
