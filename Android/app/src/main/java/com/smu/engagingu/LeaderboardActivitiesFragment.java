package com.smu.engagingu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.fyp.R;

public class LeaderboardActivitiesFragment extends Fragment {
    private RecyclerView.LayoutManager lManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_activities, container, false);
        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView)view.findViewById(R.id.recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);
        //InstanceDAO.adapter = Session.getEventAdapter(getActivity().getApplicationContext());
        recycler.setAdapter(InstanceDAO.adapter);
        return view;
    }
}
