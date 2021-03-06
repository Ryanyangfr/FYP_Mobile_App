package com.smu.engagingu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.DAO.Session;
import com.smu.engagingu.Game.QuestionType;
import com.smu.engagingu.Objects.Hotspot;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
/*
 * HomeFragment is the main fragment that displays the homepage of the application. It displays the
 * map with the location hotspots as defined from the server back-end
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {
    public static final String EXTRA_MESSAGE = "com.smu.engagingu.MESSAGE1";
    public static final String NARRATIVE_MESSAGE = "com.smu.engagingu.MESSAGE2";
    public static final String GAMEMODE_CHECK = "com.smu.engagingu.MESSAGE3";
    MapView mMapView;
    GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private LatLng locationToReturn;
    private final long MIN_TIME = 1000; //1 second
    private final long MIN_DIST = 5; //metres?
    private String snippetText;
    private HashMap<String, String> questionTypeMap;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationToReturn = new LatLng(0.0,0.0);
        saveSession();
        questionTypeMap = InstanceDAO.questionTypeMap;
        if(InstanceDAO.completedList.size()!=0 && (InstanceDAO.completedList.size()==questionTypeMap.size()) ){
            openDialog();
        }
        if (!InstanceDAO.hasPulled) {
            populateData();
            QuestionType questionType = new QuestionType();
            questionTypeMap = questionType.getQuestionTypeMap();
            InstanceDAO.questionTypeMap = questionTypeMap;
        }
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        final Activity activity = getActivity();
        getLocation(activity);
        new MyHttpRequestTask().execute("");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getLocation(activity);
                    }
                }, 0, 5000);
            }
        });

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync((OnMapReadyCallback) this);
        return v;
    }
    /*
     * Initialise the google map view. Hotspot longitude and latitudes are obtained from the
     * server backend and displayed as hotspot location pins on the map. If the hotspot has not been
     * completed, the pin would be red. else, it would be displayed in green.
     *
     * A geofencing distance is set to ensures users only complete hotspots when they are close
     * enough to the hotspot.
     */
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
        if(InstanceDAO.completedList.size()>0) {
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
        }else if (InstanceDAO.startingHotspot!=null){
            Double lat = InstanceDAO.startingHotspot.getLatitude();
            Double lng = InstanceDAO.startingHotspot.getLongitude();
            String placeName = InstanceDAO.startingHotspot.getLocationName();
            String narrative = InstanceDAO.startingHotspot.getNarrative();
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName).snippet(narrative));
            Session.setFirstTime(Objects.requireNonNull(getActivity()).getApplicationContext(), false);
        }else{
            Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
            CharSequence text = "Oops, There is something wrong with the connection. Please logout and restart the app.";
            int duration = Toast.LENGTH_SHORT;

            final Toast toast2 = Toast.makeText(context, text, duration);
            toast2.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast2.cancel();
                }
            }, 7000);
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.2969,103.8507), 16));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                while(locationToReturn==null){
                }
                double distance = computeDistanceBetween(locationToReturn,new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude));
                snippetText = arg0.getSnippet();
                if(!(snippetText.equals("Completed"))) {
                    if(distance<50.0) {
                        arg0.setSnippet("Click me to start mission");
                        arg0.showInfoWindow();
                    }else{
                        String placeName = arg0.getTitle();
                        Toast.makeText(getActivity(), placeName+" seems to be too far away!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        // to activate InfoWindow when the location pin is clicked
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                    Intent intent = new Intent(getActivity(), Narrative.class);
                    intent.putExtra(GAMEMODE_CHECK,questionTypeMap.get(title));
                    intent.putExtra(EXTRA_MESSAGE, title);
                    intent.putExtra(NARRATIVE_MESSAGE, snippetText);
                    startActivity(intent);
                }
        });
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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

        try {
            Objects.requireNonNull(locationManager).requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
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
    //Get user's current location
    private void getLocation(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng oldLocation = locationToReturn;
                            locationToReturn = new LatLng(location.getLatitude(),location.getLongitude());
                            double distance = computeDistanceBetween(locationToReturn,oldLocation);
                            if(distance>10) {
                                if(InstanceDAO.isLeader) {
                                    new MyHttpRequestTask().execute("");
                                }
                            }
                        }
                    }
                });
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {

            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("teamID",InstanceDAO.teamID);
            userHash.put("long",Double.toString(locationToReturn.longitude));
            userHash.put("lat",Double.toString(locationToReturn.latitude));
            String response = HttpConnectionUtility.post("https://amazingtrail.ml/api/team/teamLocation",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    /*
     * method to populate the application's global variables from the
     * session shared preferences
     */
    private void populateData(){
        try {
            String response = new getAllHotspot().execute("").get();
            JSONArray jsonMainNode = new JSONArray(response);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONArray latlng = jsonChildNode.getJSONArray("coordinates");
                String latString = latlng.getString(0);
                String lngString = latlng.getString(1);
                double lat = Double.parseDouble(latString);
                double lng = Double.parseDouble(lngString);
                String placeName = jsonChildNode.getString("name");
                String narrative = jsonChildNode.getString("narrative");
                Hotspot currentHotspot = new Hotspot(placeName,lat,lng,narrative);
                InstanceDAO.hotspotList.add(currentHotspot);
            }
            String response2 = new getStartingHotspot().execute("").get();
            JSONArray jsonMainNode2 = new JSONArray(response2);
            for (int i = 0; i < jsonMainNode2.length(); i++) {
                JSONObject jsonChildNode2 = jsonMainNode2.getJSONObject(i);
                String teamID = jsonChildNode2.getString("team");
                if (teamID.equals(InstanceDAO.teamID)) {
                    String startingHotspot2 = jsonChildNode2.getString("startingHotspot");
                    JSONArray latlng2 = jsonChildNode2.getJSONArray("coordinates");
                    String latString2 = latlng2.getString(0);
                    Double lat2 = Double.parseDouble(latString2);
                    String lngString2 = latlng2.getString(1);
                    Double lng2 = Double.parseDouble(lngString2);
                    String narrativeString2 = jsonChildNode2.getString("narrative");
                    InstanceDAO.startingHotspot = new Hotspot(startingHotspot2,lat2,lng2,narrativeString2);
                }
            }
            String response3 = new getCompletedList().execute("").get();
            JSONArray jsonMainMode3 = new JSONArray(response3);
            for(int i =0; i < jsonMainMode3.length();i++){
                JSONObject jsonChildNode3 = jsonMainMode3.getJSONObject(i);
                if (jsonChildNode3.getInt("iscompleted")==1){
                    InstanceDAO.completedList.add(jsonChildNode3.getString("hotspot"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally{
            InstanceDAO.hasPulled=true;
        }
    }
    /*
     * get all completed hotspots
     */
    private class getCompletedList extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/completedHotspots?trail_instance_id="+InstanceDAO.trailInstanceID+"&team="+InstanceDAO.teamID);
            if (response == null) {
                return null;
            }
            return response;
        }
    }
    /*
     * get all hotspots
     */
    private class getAllHotspot extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/location/getAllHotspots?trail_instance_id=" + InstanceDAO.trailInstanceID);
            if (response == null) {
                return null;
            }
            return response;
        }
    }
    /*
     * get starting hotspot
     */
    private class getStartingHotspot extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/team/startingHotspot?trail_instance_id=" + InstanceDAO.trailInstanceID);
            if (response == null) {
                return null;
            }
            return response;
        }
    }
    /*
     * Method called whenever user reaches homepage to save the current session
     */
    private void saveSession(){
        Session.setTeamID(Objects.requireNonNull(getActivity()).getApplicationContext(),InstanceDAO.teamID);
        Session.setTrailInstanceID(getActivity().getApplicationContext(),InstanceDAO.trailInstanceID);
        Session.setUserName(getActivity().getApplicationContext(),InstanceDAO.userName);
        Session.setEventAdapter(getActivity().getApplicationContext(),InstanceDAO.adapter);
        Session.setIsLeader(getActivity().getApplicationContext(),InstanceDAO.isLeader);
    }
    /*
     * Display dialog to congratulate user upon completion of whole trail
     */
    public void openDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        // Set Custom Title
        TextView title = new TextView(getActivity());
        title.setText("Congratulations!");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#151C55"));
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        TextView msg = new TextView(getActivity());
        // Message Properties
        msg.setText("Congratulations on completing all your missions! \n Please head back to the admin building \n for a debrief.");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });

        new Dialog(getContext());
        alertDialog.show();

    }
}

