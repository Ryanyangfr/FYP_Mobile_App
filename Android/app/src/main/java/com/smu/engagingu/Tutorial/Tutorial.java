package com.smu.engagingu.Tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.TeamDisplay;
import com.smu.engagingu.fyp.R;
/*
 * Tutorial refers to the page that displays the tutorial page to guide new users about instructions
 * to effectively play out the whole amazing race trail
 * The wordings and tutorials of the tutorial page is fixed and cannot be changed
 */
public class Tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Button nextButton = findViewById(R.id.continueButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }
    private void goNext(){
        Intent intent = new Intent(Tutorial.this, TeamDisplay.class);
        startActivity(intent);
    }
}