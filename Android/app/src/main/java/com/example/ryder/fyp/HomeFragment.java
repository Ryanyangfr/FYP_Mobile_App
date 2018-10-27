package com.example.ryder.fyp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    MapView mMapView;
    GoogleMap mGoogleMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync((OnMapReadyCallback) this); //this is important

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng Sob = new LatLng(1.2953, 103.8506);
        LatLng Soa = new LatLng(1.2979, 103.8489);
        LatLng SoeSoss = new LatLng(1.2974, 103.8495);
        LatLng Sis = new LatLng(1.2949, 103.8495);
        LatLng Sol = new LatLng(1.2962, 103.8501);
        LatLng LksLib = new LatLng(1.2968, 103.8522);
        LatLng Admin = new LatLng(1.2968, 103.8522);
        mGoogleMap.addMarker(new MarkerOptions().position(Sob).title("School of Business"));
        mGoogleMap.addMarker(new MarkerOptions().position(Soa).title("School of Accountancy"));
        mGoogleMap.addMarker(new MarkerOptions().position(SoeSoss).title("School of Economics/ School of Social Sciences"));
        mGoogleMap.addMarker(new MarkerOptions().position(Sis).title("School of Information Systems"));
        mGoogleMap.addMarker(new MarkerOptions().position(Sol).title("School of Law"));
        mGoogleMap.addMarker(new MarkerOptions().position(LksLib).title("Li Ka Shing Library"));
        mGoogleMap.addMarker(new MarkerOptions().position(Admin).title("SMU Admin Building"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Sis, 15));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
