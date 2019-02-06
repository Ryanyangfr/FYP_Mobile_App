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

import com.smu.engagingu.Adapters.DragDropResultAdapter;
import com.smu.engagingu.DragDrop;
import com.smu.engagingu.GameResultEntry;
import com.smu.engagingu.HomePage;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class DragDropResults extends AppCompatActivity {
    private ArrayList<GameResultEntry> resultList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resultList = (ArrayList<GameResultEntry>)getIntent().getSerializableExtra("resultsList");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop_results);
        String correctAnswers = getIntent().getStringExtra(DragDrop.CORRECT_ANSWERS_DRAGDROP);
        String numberOfQuestions = getIntent().getStringExtra(DragDrop.QUESTION_COUNT_DRAGDROP);
        String question = getIntent().getStringExtra(DragDrop.QUESTION_DRAGDROP);

        ListView listView = findViewById(R.id.quizResultList);
        TextView tv = findViewById(R.id.questionTextView);
        tv.setText(question);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.quiz_results_button,listView,false);
        listView.addFooterView(footer,null,false);

        Button footerButton = footer.findViewById(R.id.quizResultsFooterButton);
        TextView footerScore = footer.findViewById(R.id.quizResultsScore);
        footerScore.setText("You got "+correctAnswers+" / "+numberOfQuestions +" correct");
        DragDropResultAdapter ddResultAdapter = new DragDropResultAdapter(this,resultList);
        listView.setAdapter(ddResultAdapter);

        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DragDropResults.this, HomePage.class);
                startActivity(intent);
            }
        });



    }
}
