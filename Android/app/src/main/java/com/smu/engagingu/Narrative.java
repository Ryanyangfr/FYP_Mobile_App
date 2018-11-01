package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;

public class Narrative extends AppCompatActivity {
    public static final String EXTRA_MESSAGE2 = "com.smu.engagingu.MESSAGE";
    private String placeName;
    private String narrative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_narrative);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(HomeFragment.EXTRA_MESSAGE);
        narrative = intent.getStringExtra(HomeFragment.NARRATIVE_MESSAGE);
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
        Intent intent = new Intent(Narrative.this,QuizStartingPage.class);
        intent.putExtra(EXTRA_MESSAGE2, placeName);
        startActivity(intent);
    }
}

