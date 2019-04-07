package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;
/*
 * MemberSubmissionPage is a general instructional page that is shown to members when they reach
 * the either the CameraPage or the Drawing page. As these submission questions are restricted to
 * only leaders, members would see this informational page.
 */
public class MemberSubmissionPage extends AppCompatActivity {
    private String questionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_submission_page);
        TextView question = findViewById(R.id.memberQuestionView);
        TextView instructions = findViewById(R.id.memberInstructions);
        Button b = findViewById(R.id.button2);
        Intent intent = getIntent();
        questionName = intent.getStringExtra(Drawing.QUESTION);
        if(questionName==null){
            questionName = intent.getStringExtra(CameraPage.QUESTION);
        }
        question.setText(questionName);
        instructions.setText("Do this submission via your team leader's phone!");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberSubmissionPage.this, HomePage.class);
                startActivity(intent);
            }
        });
    }
}