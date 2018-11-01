package com.smu.engagingu.Quiz;

import android.os.AsyncTask;

import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QuestionDatabase {
    private HashMap<String,ArrayList<Question>> QuestionsMap;

    public QuestionDatabase() {
        QuestionsMap = new HashMap<>();
        String response = null;
        try {
            response = new QuestionDatabase.MyHttpRequestTask().execute().get();
            System.out.println(response);
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonMainNode = null;
        try {
            jsonMainNode = new JSONArray(response);
            for(int i = 0; i< jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                JSONArray quiz = jsonChildNode.getJSONArray("quiz");
                ArrayList<Question>questionsList = new ArrayList<>();
                for(int j = 0; j<quiz.length();j++){
                    JSONObject quizChildNode = quiz.getJSONObject(j);
                    String question= quizChildNode.getString("quiz_question");
                    String answerString = quizChildNode.getString("quiz_answer");
                    int answer = Integer.parseInt(answerString);
                    JSONArray optionsArray = quizChildNode.getJSONArray("quiz_options");
                    String op1 = optionsArray.getString(0);
                    String op2 = optionsArray.getString(1);
                    String op3 = optionsArray.getString(2);
                    String op4 = optionsArray.getString(3);
                    questionsList.add(new Question(question,op1,op2,op3,op4,answer));
                }
                QuestionsMap.put(placeName,questionsList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/quiz/getQuizzes?trail_instance_id=1");
            if (response == null){
                return null;
            }
            return response;
        }
    }

    public HashMap<String, ArrayList<Question>> getQuestionsMap() {
        return QuestionsMap;
    }
}