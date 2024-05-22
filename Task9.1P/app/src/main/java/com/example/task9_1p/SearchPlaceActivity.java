package com.example.task9_1p;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.task9_1p.adapter.PlaceAutocompleteAdapter;
import com.example.task9_1p.interfaces.PlaceSearchListener;
import com.example.task9_1p.util.PermissionUtils;
import com.example.task9_1p.util.PlaceSearchTask;
import com.example.task9_1p.util.TouchableWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchPlaceActivity extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener, TouchableWrapper.UpdateMapAfterUserInterection,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnCameraChangeListener,
        PlaceSearchListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.googleplacesearch)
    AutoCompleteTextView mAutocompleteView;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String mLatitude, mLongitude;
    private GoogleMap mMap;
    private String placeName = "";
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            hideKeyboard();
            mLatitude = String.valueOf(place.getLatLng().latitude);
            mLongitude = String.valueOf(place.getLatLng().longitude);
            LatLng newLatLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            // LatLng centerLatLng=new LatLng(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLngTemp, 15f));
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    @OnClick({R.id.imgv_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgv_return:
                onBackPressed();
                break;
        }
    }

    private ProgressDialog progressDialog;
    private void showLoading(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoading(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        tvTitle.setText("SEARCH PLACE");
        getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fabAdd.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.search_items,
                mGoogleApiClient, null, null);

        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutocompleteView.getRight() - mAutocompleteView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mAutocompleteView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeName = mAutocompleteView.getText().toString().trim();
                if (placeName != null && !placeName.isEmpty()) {
                    showLoading();
                    new PlaceSearchTask(SearchPlaceActivity.this).execute(placeName);
                }else{
                    Toast.makeText(SearchPlaceActivity.this, "please write a place", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("place","query:"+query);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnCameraChangeListener(this);
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onUpdateMapAfterUserInterection() {

    }

    private void back() {
        Intent i = new Intent(SearchPlaceActivity.this, AddActivity.class);
        i.putExtra("result", placeName);
        i.putExtra("lat", mLatitude);
        i.putExtra("lon", mLongitude);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                placeName = mAutocompleteView.getText().toString().trim();
                if (placeName != null && !placeName.isEmpty()) {
                    mLatitude = mMap.getCameraPosition().target.latitude + "";
                    mLongitude = mMap.getCameraPosition().target.longitude + "";
                    back();
                } else {
                    Toast.makeText(SearchPlaceActivity.this, "please write a place", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onPlaceFound(LatLng latLng) {
        if (placeName != null && !placeName.isEmpty()) {
            mLatitude = latLng.latitude + "";
            mLongitude = latLng.longitude + "";
            hideLoading();
            back();
        }
      }

    @Override
    public void onPlaceNotFound() {
        Toast.makeText(this, "place not found", Toast.LENGTH_SHORT).show();
        hideLoading();
    }
}
