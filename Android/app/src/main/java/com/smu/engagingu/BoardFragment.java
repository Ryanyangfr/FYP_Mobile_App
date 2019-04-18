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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smu.engagingu.Adapters.LeaderboardAdapter;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/*
 * The Leaderboard fragment is split into 2 tabs, the BoardFragment and the
 * Leaderboard Activities fragment
 * BoardFragment refers to the fragment that displays the leaderboard table, ranked from
 * 1st to 3rd positions
 */
public class BoardFragment extends Fragment {
    ArrayList<String>leaderboardList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        TextView tv = view.findViewById(R.id.BoardText);
        tv.setText("You are in Team "+InstanceDAO.teamID);
        ProgressBar pb = view.findViewById(R.id.progressBar2);
        pb.setVisibility(view.VISIBLE);
        ListView listView = (ListView)view.findViewById(R.id.listView1);
        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(getContext(),leaderboardList);
        initList(view,listView,leaderboardAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu);
    }

    /*
     * Initialise the leaderboard List
     */
    private void initList(View view, ListView listView, LeaderboardAdapter leaderboardAdapter){
        new MyHttpRequestTask(view,listView, leaderboardAdapter).execute("");
    }
    /*
     * Obtain leaderboard JSONArray from endpoint
     */
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        private View view;
        private ListView listView;
        private LeaderboardAdapter leaderboardAdapter;
        public MyHttpRequestTask(View view, ListView listView, LeaderboardAdapter leaderboardAdapter){
            this.view = view;
            this.listView = listView;
            this.leaderboardAdapter = leaderboardAdapter;
        }
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/team/hotspotStatus?trail_instance_id="+ InstanceDAO.trailInstanceID);
            if (response==null || response.equals("fail") || response.equals("")){
                leaderboardList.add(null);
                //TextView BadConnTv = view.findViewById(R.id.textView12);
            }else {
                try {
                    JSONArray jsonMainNode = new JSONArray(response);

                    for (int i = 0; i < jsonMainNode.length(); i++) {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        String teamName = jsonChildNode.optString("team");
                        int hotspotsComplete = jsonChildNode.optInt("hotspots_completed");
                        String leaderboardEntry ="      TEAM "+teamName+"    "+ hotspotsComplete+" / "+InstanceDAO.hotspotList.size()+" COMPLETED";
                        leaderboardList.add(leaderboardEntry);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (response == null){
                return null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("fail")){
                TextView BadConnTv = view.findViewById(R.id.textView12);
                BadConnTv.setVisibility(view.VISIBLE);
            }
            ProgressBar pb = view.findViewById(R.id.progressBar2);
            listView.setAdapter(leaderboardAdapter);
            pb.setVisibility(view.GONE);
        }
    }
}

