package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.fyp.R;

public class Narrative extends AppCompatActivity {
    public static final String EXTRA_MESSAGE2 = "com.smu.engagingu.MESSAGE";
    private String placeName;
    private String narrative;
    private String selfieCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrative);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(HomeFragment.EXTRA_MESSAGE);
        narrative = findNarrative(placeName);
        selfieCheck = intent.getStringExtra(HomeFragment.SELFIE_CHECK);
        TextView narrativeView = findViewById(R.id.narrativeView);
        narrativeView.setText(narrative);
        TextView placeNameView = findViewById(R.id.placeNameView);
        placeNameView.setText(placeName);
        Button buttonStartQuiz = findViewById(R.id.go_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToQuiz();
            }
        });
    }
    private void goToQuiz(){
        Intent intent = null;
        if(selfieCheck.equals("1")) {
            intent = new Intent(Narrative.this, QuizActivity .class);
        }else{
            intent = new Intent(Narrative.this, CameraPage.class);
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
}

