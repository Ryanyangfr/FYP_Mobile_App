package com.smu.engagingu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

public class LeaderboardFragment extends Fragment {
    //json string
//    private String jsonString = "";
    private boolean hasGottenInput = false;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            //System.out.println("hello"+jsonString);
            View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
            initList();

            ListView listView = (ListView)view.findViewById(R.id.listView1);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    teamList );
            listView.setAdapter(arrayAdapter);

            return view;
        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu);
    }

    List<String> teamList = new ArrayList<>();
    private void initList(){
        String jsonString = null;
        try {
            jsonString = new MyHttpRequestTask().execute("http://54.255.245.23:3000/user/retrieveAllUser").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (jsonString==null){
            teamList.add("No Records available at the moment");
        }else {
            try {
                JSONArray jsonMainNode = new JSONArray(jsonString);

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String name = jsonChildNode.optString("username");
//                    System.out.println()
                    teamList.add(name);
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
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/user/retrieveAllUser");
//            jsonString = response;
//            System.out.println("Debug:"+ jsonString+ "2"+response);
            hasGottenInput = true;
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
