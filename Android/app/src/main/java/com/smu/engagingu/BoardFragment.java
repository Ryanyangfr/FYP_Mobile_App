package com.smu.engagingu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smu.engagingu.Adapters.LeaderboardAdapter;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BoardFragment extends Fragment {
    //json string
//    private String jsonString = "";
    private boolean hasGottenInput = false;
    ArrayList<String>leaderboardList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("hello"+jsonString);
        System.out.println(3);
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        initList();
        TextView tv = view.findViewById(R.id.BoardText);
        tv.setText("You are in Team "+InstanceDAO.teamID);
        ListView listView = (ListView)view.findViewById(R.id.listView1);
        //listView.getItemAtPosition(0);
        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(getContext(),leaderboardList);
        listView.setAdapter(leaderboardAdapter);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu);
    }


    private void initList(){
        System.out.println(4);
        String jsonString = null;
        try {
            jsonString = new MyHttpRequestTask().execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (jsonString==null){
            leaderboardList.add(null);
        }else {
            try {
                JSONArray jsonMainNode = new JSONArray(jsonString);

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String teamName = jsonChildNode.optString("team");
                    int hotspotsComplete = jsonChildNode.optInt("hotspots_completed");
                    String leaderboardEntry ="      Team: "+teamName+"      completed: "+hotspotsComplete;
//
                    leaderboardList.add(leaderboardEntry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://13.229.115.32:3000/team/hotspotStatus?trail_instance_id="+ InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
}

