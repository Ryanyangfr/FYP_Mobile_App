package com.smu.engagingu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.Utilities.HttpConnectionUtility;

import java.util.HashMap;

public class Narrative extends AppCompatActivity {
    public static final String EXTRA_MESSAGE2 = "com.smu.engagingu.MESSAGE";
    private String placeName;
    private String narrative;
    private String gameModeCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrative);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(HomeFragment.EXTRA_MESSAGE);
        narrative = findNarrative(placeName);
        gameModeCheck = intent.getStringExtra(HomeFragment.GAMEMODE_CHECK);
        TextView narrativeView = findViewById(R.id.narrativeView);
        narrativeView.setText(narrative);
        TextView placeNameView = findViewById(R.id.placeNameView);
        placeNameView.setText(placeName.toUpperCase());
        Button buttonStartQuiz = findViewById(R.id.go_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGame();
            }
        });
    }
    private void goToGame(){
        Intent intent = null;
        if(gameModeCheck==null){
            intent = new Intent(Narrative.this, HomePage .class);
            startActivity(intent);
        }else {
            switch (gameModeCheck) {
                case "1":
                    intent = new Intent(Narrative.this, QuizActivity.class);
                    break;
                case "2":
                    intent = new Intent(Narrative.this, CameraPage.class);
                    break;
                case "3":
                    intent = new Intent(Narrative.this, Drawing.class);
                    break;
                case "4":
                    intent = new Intent(Narrative.this, Anagram.class);
                    break;
                case "5":
                    intent = new Intent(Narrative.this, DragDrop.class);
                    break;
                case "6":
                    intent = new Intent(Narrative.this, WordsSearch.class);
                    break;
                default:
                    System.out.println("no match");
            }
        }
        intent.putExtra(EXTRA_MESSAGE2, placeName);
        startActivity(intent);
    }
    private String findNarrative(String placeName){
        for(int i =0; i < InstanceDAO.hotspotList.size();i++){
            Hotspot currentHotspot = InstanceDAO.hotspotList.get(i);
            if(currentHotspot.getLocationName().equals(placeName)){
                return currentHotspot.getNarrative();
            }
        }
        return "";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String message = Integer.toString(1);//Score
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("team_id",InstanceDAO.teamID);
            System.out.println("tid: "+InstanceDAO.teamID);
            userHash.put("trail_instance_id",InstanceDAO.trailInstanceID);
            userHash.put("score",message);
            userHash.put("hotspot",placeName);
            System.out.println("message: "+message);
            String response = HttpConnectionUtility.post("http://13.229.115.32:3000/team/updateScore",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
}

