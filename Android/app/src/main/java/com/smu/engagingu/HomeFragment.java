package com.smu.engagingu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

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
        try {
            String response = new HomeFragment.MyHttpRequestTask().execute("http://54.255.245.23:3000/hotspot/getAllHotspots").get();
            JSONArray jsonMainNode = new JSONArray(response);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONArray latlng = jsonChildNode.getJSONArray("coordinates");
                String latString = latlng.getString(0);
                String lngString = latlng.getString(1);
                double lat = Double.parseDouble(latString);
                double lng = Double.parseDouble(lngString);
                System.out.println("LAT: "+lat);
                System.out.println("LNG: "+lng);
                String placeName = jsonChildNode.getString("name");
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(placeName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.2974,103.8495), 15));
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
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/hotspot/getAllHotspots?trail_instance_id=1");
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
