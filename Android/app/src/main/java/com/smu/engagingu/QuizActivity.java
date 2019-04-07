package com.smu.engagingu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Game.QuestionDatabase;
import com.smu.engagingu.Objects.GameResultEntry;
import com.smu.engagingu.Objects.Question;
import com.smu.engagingu.Results.QuizResults;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
/*
 * QuizActivity refers to the page that displays the quiz game mode. The textview that displays
 * the question and the buttons that displays the answer options are predetermined. The relevant
 * information is obtained from the database through a RESTful get method and the text of the
 * corresponding text views are set accordingly.
 */
public class QuizActivity extends AppCompatActivity {
    public static final String CORRECT_ANSWERS = "com.smu.engagingu.CORRECTQUIZANSWERS";
    public static final String QUESTION_COUNT = "com.smu.engagingu.QUIZQUESTIONCOUNT";
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView answerView;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button buttonConfirmNext;
    private Boolean b1Check = false;
    private Boolean b2Check = false;
    private Boolean b3Check = false;
    private Boolean b4Check = false;

    private List<Question> questionsList;
    private ArrayList<GameResultEntry> resultsList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private String placeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score = 0;
        Intent intent = getIntent();
        placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        QuestionDatabase qnsDB = new QuestionDatabase();
        questionsList = qnsDB.getQuestionsMap().get(placeName);
        textViewQuestion = findViewById(R.id.text_view_question);
        answerView = findViewById(R.id.correctAnswerView);
        textViewScore = findViewById(R.id.text_view_score);
        b1 = findViewById(R.id.Option1);
        b1.setTypeface(null, Typeface.BOLD);
        b2 = findViewById(R.id.Option2);
        b2.setTypeface(null, Typeface.BOLD);
        b3 = findViewById(R.id.Option3);
        b3.setTypeface(null, Typeface.BOLD);
        b4 = findViewById(R.id.Option4);
        b4.setTypeface(null, Typeface.BOLD);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        if(InstanceDAO.isLeader) {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b1Check = true;
                    b2Check = false;
                    b3Check = false;
                    b4Check = false;
                    //selected option has a different colour from unselected options
                    b1.setBackgroundColor(Color.parseColor("#151C55"));
                    b2.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b3.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b4.setBackgroundColor(Color.parseColor("#A9A9A9"));
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b1Check = false;
                    b2Check = true;
                    b3Check = false;
                    b4Check = false;
                    b1.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b2.setBackgroundColor(Color.parseColor("#151C55"));
                    b3.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b4.setBackgroundColor(Color.parseColor("#A9A9A9"));
                }
            });
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b1Check = false;
                    b2Check = false;
                    b3Check = true;
                    b4Check = false;
                    b1.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b2.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b3.setBackgroundColor(Color.parseColor("#151C55"));
                    b4.setBackgroundColor(Color.parseColor("#A9A9A9"));
                }
            });
            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b1Check = false;
                    b2Check = false;
                    b3Check = false;
                    b4Check = true;
                    b1.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b2.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b3.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    b4.setBackgroundColor(Color.parseColor("#151C55"));
                }
            });
            //a different format would be displayed if the current user is not a Leader
        }else{
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    CharSequence text = "Only the leader is allowed to submit his quiz! Feel free to discuss the answers!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    CharSequence text = "Only the leader is allowed to submit his quiz! Feel free to discuss the answers!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    CharSequence text = "Only the leader is allowed to submit his quiz! Feel free to discuss the answers!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    CharSequence text = "Only the leader is allowed to submit his quiz! Feel free to discuss the answers!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }
        if(questionsList!=null) {
            questionCountTotal = questionsList.size();
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Oops! Choose another Hotspot please";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        resultsList = new ArrayList<>();
        showNextQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered && InstanceDAO.isLeader){
                    if(b1Check|| b2Check || b3Check|| b4Check){
                        checkAnswer();
                    }else{
                        Toast.makeText(QuizActivity.this,"Please choose something",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showNextQuestion();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
   //Method called when user presses next. The textviews have their text newly set to prepare for
    //next question
    private void showNextQuestion(){
        b1.setBackgroundColor(Color.parseColor("#A9A9A9"));
        b2.setBackgroundColor(Color.parseColor("#A9A9A9"));
        b3.setBackgroundColor(Color.parseColor("#A9A9A9"));
        b4.setBackgroundColor(Color.parseColor("#A9A9A9"));

        if(questionCounter<questionCountTotal) {
            currentQuestion = questionsList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            b1.setText(currentQuestion.getOption1());
            b2.setText(currentQuestion.getOption2());
            b3.setText(currentQuestion.getOption3());
            b4.setText(currentQuestion.getOption4());
            answerView.setText("");
            questionCounter++;
            answered = false;
            if(InstanceDAO.isLeader) {
                buttonConfirmNext.setText("Confirm");
            }else{
                buttonConfirmNext.setText("Next");
            }
        }else{
            finishQuiz();

        }
    }
    //user checks his/her answer against the answer obtained from database
    private void checkAnswer(){
        String usersAnswer = "";
        String answer = "";

        answered=true;

        int answerNum=0;
        if(b1Check){
            answerNum=1;
            usersAnswer = currentQuestion.getOption1();
        }else if(b2Check){
            answerNum=2;
            usersAnswer = currentQuestion.getOption2();
        }else if(b3Check){
            answerNum=3;
            usersAnswer = currentQuestion.getOption3();
        }else if(b4Check){
            answerNum=4;
            usersAnswer = currentQuestion.getOption4();
        }

        switch (currentQuestion.getAnswerNr()){
            case 1:
                answer = currentQuestion.getOption1();
                break;
            case 2:
                answer = currentQuestion.getOption2();
                break;
            case 3:
                answer = currentQuestion.getOption3();
                break;
            case 4:
                answer = currentQuestion.getOption4();
                break;
        }

        if(answerNum == currentQuestion.getAnswerNr()){
            score++;
            textViewScore.setText("Score: "+ score);
            answerView.setTextColor(Color.parseColor("#92d050"));
            answerView.setText("Correct!");
        }else {
            textViewScore.setText("Score: " + score);
            answerView.setTextColor(Color.parseColor("#E85858"));
            answerView.setText("Wrong!");
        }
        resultsList.add(new GameResultEntry("1",currentQuestion.getQuestion(),answer,usersAnswer));
        showSolution();
    }
    //Actual answer is displayed to the user
    private void showSolution(){
        b1.setBackgroundColor(Color.parseColor("#E85858"));//Color RED
        b2.setBackgroundColor(Color.parseColor("#E85858"));
        b3.setBackgroundColor(Color.parseColor("#E85858"));
        b4.setBackgroundColor(Color.parseColor("#E85858"));

        switch(currentQuestion.getAnswerNr()){
            case 1:
                b1.setBackgroundColor(Color.parseColor("#92d050"));// Color GREEN
                break;
            case 2:
                b2.setBackgroundColor(Color.parseColor("#92d050"));
                break;
            case 3:
                b3.setBackgroundColor(Color.parseColor("#92d050"));
                break;
            case 4:
                b4.setBackgroundColor(Color.parseColor("#92d050"));
                break;
        }

        if(questionCounter < questionCountTotal){
            buttonConfirmNext.setText("Next");
        }else{
            buttonConfirmNext.setText("Finish");
        }
    }
    // score is tabulated and sent to the database via RESTful post request
    private void finishQuiz(){
        String response = "a";
        Intent intent;
        if(InstanceDAO.isLeader){
           intent = new Intent(QuizActivity.this, QuizResults.class);
            intent.putExtra("resultsList", resultsList);
            intent.putExtra(QUESTION_COUNT, Integer.toString(questionsList.size()));
            intent.putExtra(CORRECT_ANSWERS, Integer.toString(score));
        }else{
            intent = new Intent(QuizActivity.this, HomePage.class);
        }
        if (InstanceDAO.isLeader) {
            try {
                response = new MyHttpRequestTask().execute("https://amazingtrail.ml/api/team/updateScore").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (response.equals("fail") || response.equals("")){
            Toast toast = Toast.makeText(QuizActivity.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
            toast.show();
        }else {
            InstanceDAO.completedList.add(placeName);
            startActivity(intent);
        }

    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String message = Integer.toString(score);
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("team_id",InstanceDAO.teamID);
            userHash.put("trail_instance_id",InstanceDAO.trailInstanceID);
            userHash.put("score",message);
            userHash.put("hotspot",placeName);
            String response = HttpConnectionUtility.post("https://amazingtrail.ml/api/team/updateScore",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    
}
