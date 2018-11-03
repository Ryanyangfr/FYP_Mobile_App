package com.smu.engagingu.Tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.fyp.R;

public class TutorialPage6 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page6);
        Button nextButton = findViewById(R.id.TutorialButton6);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }
    private void goNext(){
        Intent intent = new Intent(TutorialPage6.this,TutorialPage7.class);
        startActivity(intent);
    }
}