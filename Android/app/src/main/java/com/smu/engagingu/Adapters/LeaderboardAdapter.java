package com.smu.engagingu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<String> {
    public LeaderboardAdapter(Context context, ArrayList<String> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String entry = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_list, parent, false);
        }
        if (position == 0) {
            convertView.setBackgroundColor(Color.parseColor("#FFD700"));
        } else if(position ==1) {
            convertView.setBackgroundColor(Color.parseColor("#C0C0C0"));
        } else if(position == 2){
            convertView.setBackgroundColor(Color.parseColor("#cd7f32"));
        }else{
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.textView3);
        //TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(entry);
        // Return the completed view to render on screen
        return convertView;
    }
}
