package com.smu.engagingu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Quiz.Question;
import com.smu.engagingu.Quiz.QuestionDatabase;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuizActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewScore;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    private List<Question> questionsList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private String placeName;
    public static final String EXTRA_MESSAGE = "com.smu.engagingu.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score = 0;
        Intent intent = getIntent();
        placeName = intent.getStringExtra(QuizStartingPage.EXTRA_MESSAGE3);
        System.out.println("QuizActivity: "+placeName);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        QuestionDatabase qnsDB = new QuestionDatabase();
        questionsList = qnsDB.getQuestionsMap().get(placeName);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        if(questionsList!=null) {
            questionCountTotal = questionsList.size();
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Oops! Choose another Hotspot please";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        showNextQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rb1.isChecked()|| rb2.isChecked() || rb3.isChecked()|| rb4.isChecked()){
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
    private void showNextQuestion(){
        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);
        if(questionCounter<questionCountTotal) {
            currentQuestion = questionsList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            questionCounter++;
            answered = false;
            buttonConfirmNext.setText("Confirm");
        }else{
            finishQuiz();

        }
    }
    private void checkAnswer(){
        answered=true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNum = rbGroup.indexOfChild(rbSelected) + 1;

        if(answerNum == currentQuestion.getAnswerNr()){
            score++;
            textViewScore.setText("Score: "+ score);
        }
        textViewScore.setText("Score: "+ score);
        showSolution();
    }
    private void showSolution(){
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        switch(currentQuestion.getAnswerNr()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Option 1 was the correct answer");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Option 2 was the correct answer");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Option 3 was the correct answer");
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                textViewQuestion.setText("Option 4 was the correct answer");
                break;
        }

        if(questionCounter < questionCountTotal){
            buttonConfirmNext.setText("Next");
        }else{
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz(){
        try {
            String response = new MyHttpRequestTask().execute("http://54.255.245.23:3000/team/updateScore").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(QuizActivity.this,HomePage.class);
        intent.putExtra(EXTRA_MESSAGE, placeName);
        startActivity(intent);
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String message = Integer.toString(score);
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("team_id",InstanceDAO.teamID);
            System.out.println("tid: "+InstanceDAO.teamID);
            userHash.put("trail_instance_id",InstanceDAO.trailInstanceID);
            userHash.put("score",message);
            userHash.put("hotspot",placeName);
            System.out.println("message: "+message);
            String response = HttpConnectionUtility.post("http://54.255.245.23:3000/team/updateScore",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
