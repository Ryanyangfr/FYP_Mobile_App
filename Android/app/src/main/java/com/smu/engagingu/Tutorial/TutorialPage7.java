package com.smu.engagingu.Tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.HomePage;
import com.smu.engagingu.fyp.R;

public class TutorialPage7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page7);
        Button nextButton = findViewById(R.id.TutorialButton7);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }
    private void goNext(){
        Intent intent = new Intent(TutorialPage7.this,HomePage.class);
        startActivity(intent);
    }
}