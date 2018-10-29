package com.example.ryder.fyp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    MapView mMapView;
    GoogleMap mGoogleMap;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 1000; //1 second
    private final long MIN_DIST = 5; //metres?

    private LatLng latLng;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
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
        try{
            mGoogleMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        LatLng Sob = new LatLng(1.2953, 103.8506);
        LatLng Soa = new LatLng(1.2956, 103.8498);
        LatLng SoeSoss = new LatLng(1.2979, 103.8489);
        LatLng Sis = new LatLng(1.2974, 103.8495);
        LatLng Sol = new LatLng(1.2949, 103.8495);
        LatLng LksLib = new LatLng(1.2962, 103.8501);
        LatLng Admin = new LatLng(1.2968, 103.8522);
        mGoogleMap.addMarker(new MarkerOptions().position(Sob).title("School of Business"));
        mGoogleMap.addMarker(new MarkerOptions().position(Soa).title("School of Accountancy"));
        mGoogleMap.addMarker(new MarkerOptions().position(SoeSoss).title("School of Economics/ School of Social Sciences"));
        mGoogleMap.addMarker(new MarkerOptions().position(Sis).title("School of Information Systems"));
        mGoogleMap.addMarker(new MarkerOptions().position(Sol).title("School of Law"));
        mGoogleMap.addMarker(new MarkerOptions().position(LksLib).title("Li Ka Shing Library"));
        mGoogleMap.addMarker(new MarkerOptions().position(Admin).title("SMU Admin Building"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Sis, 15));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Intent intent = new Intent(getActivity(), Narrative.class);
                startActivity(intent);
                return true;
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIST,locationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }

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
