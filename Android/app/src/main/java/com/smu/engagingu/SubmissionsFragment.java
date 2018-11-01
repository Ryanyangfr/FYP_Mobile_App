package com.smu.engagingu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;


public class SubmissionsFragment extends Fragment {

    String[] HOTSPOTS = {"Li Ka Shing Library", "School of Law"};
    String[] MISSIONS = {"Take a selfie with mission statement", "Take a selfie in the lawyer room"};
    int[] IMAGES = {R.drawable.pixel_island, R.drawable.pixel_link};

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_submissions,container, false);

        ListView listView = view.findViewById(R.id.listView);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        listView.setAdapter(customAdaptor);

        return view;
    }

    class CustomAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_submission_layout, null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_hotspot = (TextView)view.findViewById(R.id.textView_hotspot);
            TextView textView_mission = (TextView)view.findViewById(R.id.textView_question);

            imageView.setImageResource(IMAGES[i]);
            textView_hotspot.setText(HOTSPOTS[i]);
            textView_mission.setText(MISSIONS[i]);

            return view;
        }

    }
}