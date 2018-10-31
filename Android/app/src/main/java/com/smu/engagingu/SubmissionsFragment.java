package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smu.engagingu.fyp.R;


public class SubmissionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_submissions,container, false);

        final Button button = (Button) rootView.findViewById(R.id.btnToCamera);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent i = new Intent(rootView.getContext(), CameraPage.class);
                startActivity(i);
            }
        });

        return rootView;
    }

}