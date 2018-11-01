package com.smu.engagingu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;



public class QuizStartingPage extends AppCompatActivity {
    private String placeName;
    public static final String EXTRA_MESSAGE3 = "com.smu.engagingu.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_starting);

        Button buttonStartQuiz = findViewById(R.id.start_quiz);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
        TextView quizTextView = findViewById(R.id.quizTextView);
        quizTextView.setText(placeName+" quiz ");
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }
    private void startQuiz(){
        Intent intent = new Intent(QuizStartingPage.this,QuizActivity.class);
        intent.putExtra(EXTRA_MESSAGE3, placeName);
        startActivity(intent);
    }
}
