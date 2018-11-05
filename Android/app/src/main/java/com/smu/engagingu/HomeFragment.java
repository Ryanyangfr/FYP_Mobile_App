package com.smu.engagingu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.Quiz.Question;
import com.smu.engagingu.Quiz.QuestionDatabase;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnMapReadyCallback{
    public static final String EXTRA_MESSAGE = "com.smu.engagingu.MESSAGE1";
    public static final String NARRATIVE_MESSAGE = "com.smu.engagingu.MESSAGE2";
    public static final String SELFIE_CHECK="com.smu.engagingu.MESSAGE3";
    MapView mMapView;
    GoogleMap mGoogleMap;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Boolean completed;

    private final long MIN_TIME = 1000; //1 second
    private final long MIN_DIST = 5; //metres?
    private String snippetText;


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
        Intent intent = getActivity().getIntent();
        String completedPlace = intent.getStringExtra(QuizActivity.EXTRA_MESSAGE);
        if(completedPlace!=null) {
            InstanceDAO.completedList.add(completedPlace);
        }
        try{
            mGoogleMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        if((!InstanceDAO.firstTime) && InstanceDAO.completedList.size()>0) {
            System.out.println("hotspotList: "+InstanceDAO.hotspotList);
            for(int i =0; i <InstanceDAO.hotspotList.size();i++){
                Hotspot currentHotspot = InstanceDAO.hotspotList.get(i);
                Double lat = currentHotspot.getLatitude();
                Double lng = currentHotspot.getLongitude();
                String placeName = currentHotspot.getLocationName();
                String narrative = currentHotspot.getNarrative();
                if(!(InstanceDAO.completedList.contains(placeName))) {
                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName).snippet(narrative));
                }else{
                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(placeName).snippet("Completed").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }
        }else{
            Double lat = InstanceDAO.startingHotspot.getLatitude();
            Double lng = InstanceDAO.startingHotspot.getLongitude();
            String placeName = InstanceDAO.startingHotspot.getLocationName();
            String narrative = InstanceDAO.startingHotspot.getNarrative();
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName).snippet(narrative));
            InstanceDAO.firstTime = false;
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.2969,103.8507), 16));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                snippetText = arg0.getSnippet();
                if(!(snippetText.equals("Completed"))) {
                    arg0.setSnippet("Click me to start mission");
                    arg0.showInfoWindow();
                }
                return true;
            }
        });
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                    QuestionDatabase qnsDB = new QuestionDatabase();
                    ArrayList<Question> questionsList = qnsDB.getQuestionsMap().get(title);

                    Intent intent = new Intent(getActivity(), Narrative.class);
                    if (questionsList != null) {

                        intent.putExtra(SELFIE_CHECK, "1");
                    } else {

                        intent.putExtra(SELFIE_CHECK, "0");
                    }
                    intent.putExtra(EXTRA_MESSAGE, title);
                    intent.putExtra(NARRATIVE_MESSAGE, snippetText);
                    startActivity(intent);
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
