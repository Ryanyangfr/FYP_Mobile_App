package com.smu.engagingu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Objects.Hotspot;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
/*
 * This class is used to display the narrative corresponding to each individual hotspot
 * the information is obtained from the questiontype hashmap, according to the question type,
 * the program redirects the user to the respective gamemode
 */
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
        String word = null;
        Intent intent = null;
        Boolean isConnected = false;
        if(gameModeCheck==null){
            intent = new Intent(Narrative.this, HomePage .class);
            startActivity(intent);
        }else {
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            try {
                word = new MyHttpRequestTask().execute("").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (word.equals("fail") || word.equals("")) {
                Toast toast = Toast.makeText(Narrative.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                //switch case to check for which game mode to redirect the user to
                if(isConnected) {
                    switch (gameModeCheck) {
                        case "1":
                            intent = new Intent(Narrative.this, QuizActivity.class);
                            break;
                        case "2":
                            if(!InstanceDAO.isLeader){
                                InstanceDAO.completedList.add(placeName);
                                intent = new Intent(Narrative.this, MemberSubmissionPage.class);
                            }else {
                                intent = new Intent(Narrative.this, CameraPage.class);
                            }
                            break;
                        case "3":
                            if(!InstanceDAO.isLeader) {
                                InstanceDAO.completedList.add(placeName);
                                intent = new Intent(Narrative.this, MemberSubmissionPage.class);
                            }else{
                                intent = new Intent(Narrative.this,Drawing.class);
                            }
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
                    }
                }
                Objects.requireNonNull(intent).putExtra(EXTRA_MESSAGE2, placeName);
                startActivity(intent);
            }if(!isConnected){
                Toast toast = Toast.makeText(Narrative.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
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
    // testing for connection
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/draganddrop/getDragAndDrop?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null || response.equals("fail")){
                return "fail";
            }
            return response;
        }
    }
}

