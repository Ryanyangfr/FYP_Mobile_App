package com.smu.engagingu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.smu.engagingu.fyp.R;


public class UserName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
    }
    public void sendUserName(View view) {
        Intent intent = new Intent(this, StoryContainer.class);
        startActivity(intent);
    }
}
