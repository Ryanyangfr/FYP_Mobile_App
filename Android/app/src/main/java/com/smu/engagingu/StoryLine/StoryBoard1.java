package com.smu.engagingu.StoryLine;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class StoryBoard1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_board1);
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
                System.out.println("LAT: " + lat);
                System.out.println("LNG: " + lng);
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
        } catch (JSONException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
        Button nextButton = findViewById(R.id.storyButton1);
        nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goNext();
                }
            });
        }
        private void goNext () {
            Intent intent = new Intent(StoryBoard1.this, StoryBoard2.class);
            startActivity(intent);
        }
        private class getAllHotspot extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... params) {
                String response = HttpConnectionUtility.get("http://54.255.245.23:3000/hotspot/getAllHotspots?trail_instance_id=" + InstanceDAO.trailInstanceID);
                if (response == null) {
                    return null;
                }
                return response;
            }
        }
        private class getStartingHotspot extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... params) {
                String response = HttpConnectionUtility.get("http://54.255.245.23:3000/team/startingHotspot?trail_instance_id=" + InstanceDAO.trailInstanceID);
                if (response == null) {
                    return null;
                }
                return response;
            }
        }
}
