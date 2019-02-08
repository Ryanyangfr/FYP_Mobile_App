package com.smu.engagingu.Results;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.smu.engagingu.Adapters.QuizResultAdapter;
import com.smu.engagingu.HomePage;
import com.smu.engagingu.QuizActivity;
import com.smu.engagingu.Objects.GameResultEntry;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class QuizResults extends AppCompatActivity {
    private ArrayList<GameResultEntry> resultsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resultsList = (ArrayList<GameResultEntry>)getIntent().getSerializableExtra("resultsList");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        String correctAnswers = getIntent().getStringExtra(QuizActivity.CORRECT_ANSWERS);
        String numberOfQuestions = getIntent().getStringExtra(QuizActivity.QUESTION_COUNT);

        ListView listView = findViewById(R.id.quizResultList);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.quiz_results_button,listView,false);
        listView.addFooterView(footer,null,false);

        Button footerButton = footer.findViewById(R.id.quizResultsFooterButton);
        TextView footerScore = footer.findViewById(R.id.quizResultsScore);
        footerScore.setText("You got "+correctAnswers+" / "+numberOfQuestions +" correct");
        QuizResultAdapter quizResultAdapter = new QuizResultAdapter(this,resultsList);
        listView.setAdapter(quizResultAdapter);

        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizResults.this, HomePage.class);
                startActivity(intent);
            }
        });



    }
}
