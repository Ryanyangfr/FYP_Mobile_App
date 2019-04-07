package com.smu.engagingu.Results;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.smu.engagingu.HomePage;
import com.smu.engagingu.Objects.PuzzlePoint;
import com.smu.engagingu.WordsSearch;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;
import java.util.HashMap;
/*
 * WordSearchResults refers to the page that displays the questions
 * and answers of the wordsearch activity
 */
public class WordSearchResults extends AppCompatActivity {
    private char [][] puzzle;
    private String score;
    private ArrayList<String> answerList;
    private HashMap<String,ArrayList<PuzzlePoint>> wordPointMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search_results);

        puzzle = (char [][])getIntent().getSerializableExtra("puzzle");
        answerList = (ArrayList<String>) getIntent().getSerializableExtra("resultsList");
        wordPointMap = (HashMap<String,ArrayList<PuzzlePoint>>) getIntent().getSerializableExtra("wordPointMap");
        score = getIntent().getStringExtra(WordsSearch.CORRECT_ANSWERS);
        Button b = findViewById(R.id.wsButton);
        TextView tv = findViewById(R.id.wsTextView);
        createFinishedGrid(puzzle);
        tv.setText("You got "+score+" / "+ 5 +" correct");

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordSearchResults.this, HomePage.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
    /*
     * createFinishedGrid takes in a wordsarch 2D char array and displays the finished grid
     * with the correct answers highlighted in green and the words missed out
     * in red
     */
    public void createFinishedGrid(char[][] input){
        TableLayout table = findViewById(R.id.mainLayout);
        int count = table.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = table.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        for(int i = 0; i < input.length; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);

            for(int j = 0; j < input.length; j++){
                TextView text = new TextView(this);
                Character temp = input[i][j];
                text.setText(temp.toString());
                Boolean b = false;
                Boolean b2 = false;
                for(String s: wordPointMap.keySet()){
                    ArrayList<PuzzlePoint> tempList = wordPointMap.get(s);
                    for(PuzzlePoint p : tempList) {
                        if (p.getRow() == i && p.getColumn() == j) {
                            b = true;
                            for(String s2: answerList){
                                if (s2.equals(s)){
                                    b2=true;
                                }
                            }
                        }
                    }
                }
                if(b){
                    text.setBackgroundColor(Color.parseColor("#E85858"));
                }
                if(b2){
                    text.setBackgroundColor(Color.parseColor("#92d050"));
                }
                text.setPadding(10, 5, 10, 5);
                text.setTextSize(25);
                text.setGravity(Gravity.CENTER);
                row.addView(text);
            }
            table.addView(row);
        }
    }
}
