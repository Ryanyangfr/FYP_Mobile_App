package com.smu.engagingu.StoryLine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.fyp.R;

public class StoryBoard2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_board2);
        Button nextButton = findViewById(R.id.storyButton2);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }
    private void goNext(){
        Intent intent = new Intent(StoryBoard2.this,StoryBoard3.class);
        startActivity(intent);
    }
}