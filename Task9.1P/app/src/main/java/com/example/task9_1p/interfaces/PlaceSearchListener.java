package com.example.task9_1p.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface PlaceSearchListener {
    void onPlaceFound(LatLng latLng);
    void onPlaceNotFound();
}
