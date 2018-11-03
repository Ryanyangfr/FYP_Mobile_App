package com.smu.engagingu.StoryLine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.Tutorial.TutorialPage1;
import com.smu.engagingu.fyp.R;

public class StoryBoard3 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_board3);
        Button nextButton = findViewById(R.id.storyButton3);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }
    private void goNext(){
        Intent intent = new Intent(StoryBoard3.this,TutorialPage1.class);
        startActivity(intent);
    }
}