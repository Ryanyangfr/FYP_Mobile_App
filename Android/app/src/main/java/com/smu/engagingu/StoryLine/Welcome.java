package com.smu.engagingu.StoryLine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smu.engagingu.Tutorial.Tutorial;
import com.smu.engagingu.fyp.R;
/*
 * Welcome refers to the page that displays the welcome message that greets all new users
 * of the amazing race trail.
 * The wording on this particular page is fixed.
 */
public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button nextButton = findViewById(R.id.storyButton1);
        nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goNext();
                }
            });
        }
        private void goNext () {
            Intent intent = new Intent(Welcome.this, Tutorial.class);
            startActivity(intent);
        }
}
