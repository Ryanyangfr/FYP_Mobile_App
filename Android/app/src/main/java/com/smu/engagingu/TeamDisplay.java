package com.smu.engagingu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.StoryLine.Welcome;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TeamDisplay extends AppCompatActivity {
    private String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_display);
        try {
            response = new MyHttpRequestTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        HashMap<String,Integer> isLeaderMap = handleResponse(response);
        String temp = InstanceDAO.userName+InstanceDAO.teamID;
        Integer isLeader = isLeaderMap.get(temp);
        TextView leaderTv = findViewById(R.id.isLeaderTv);
        if(isLeader==0){
            InstanceDAO.isLeader = false;
            leaderTv.setText("MEMBER");
        }else if (isLeader == 1){
            InstanceDAO.isLeader = true;
            leaderTv.setText("LEADER");
        }
        TextView groupNumberView = findViewById(R.id.GroupNumberView);
        String toDisplay = "TEAM "+InstanceDAO.teamID;
        groupNumberView.setText(toDisplay);

        final Button confirmationButton = findViewById(R.id.groupNumberButton);
        final ProgressBar pb = findViewById(R.id.teamDisplayProgress);
        confirmationButton.setVisibility(View.GONE);
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                while (!InstanceDAO.startTrail) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    confirmationButton.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirmationButton.invalidate();
                        confirmationButton.requestLayout();
                        confirmationButton.setText("GO");
                        confirmationButton.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                    }
                });

            }
        };
        if(!InstanceDAO.startTrail) {
            Thread myThread = new Thread(myRunnable);
            myThread.start();
        }

        confirmationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("Hello"+InstanceDAO.startTrail);
                if (InstanceDAO.startTrail) {
                    try {
                        goToStoryPage();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }
    private void goToStoryPage(){
        Intent intent = new Intent(this, Welcome.class);
        startActivity(intent);
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/user/retrieveAllUser");
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private HashMap<String,Integer> handleResponse (String response){
        HashMap<String,Integer> toReturn = new HashMap<>();
        JSONArray jsonMainNode = null;
        try {
            jsonMainNode = new JSONArray(response);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String userName = jsonChildNode.getString("username");
                int team = jsonChildNode.getInt("team");
                int isLeader = jsonChildNode.getInt("isLeader");
                toReturn.put(userName+team,isLeader);
            }
        }  catch (JSONException e) {
        e.printStackTrace();
    }
    return toReturn;
    }
}
