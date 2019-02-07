package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.StoryLine.Welcome;
import com.smu.engagingu.fyp.R;

public class TeamDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_display);
        TextView groupNumberView = findViewById(R.id.GroupNumberView);
        String toDisplay = "TEAM "+InstanceDAO.teamID;
        groupNumberView.setText(toDisplay);

        Button confirmationButton = findViewById(R.id.groupNumberButton);
        confirmationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                try {
                    goToStoryPage();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void goToStoryPage(){
        Intent intent = new Intent(this, Welcome.class);
        startActivity(intent);
    }

}
