package com.smu.engagingu.Game;

import android.os.AsyncTask;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Objects.Question;
import com.smu.engagingu.Utilities.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
/*
 * QuestionDatabase is used for pulling quiz and submission questions from the database
 * questions and answers are then stored in a hashmap to be used for the conduct
 * of the amazing race trail
 */
public class QuestionDatabase {
    private HashMap<String,ArrayList<Question>> QuestionsMap;
    private HashMap<String,String> SelfieQuestionsMap;

    public QuestionDatabase() {
        QuestionsMap = new HashMap<>();
        String response = null;
        try {
            response = new QuestionDatabase.MyHttpRequestTask().execute().get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonMainNode;
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
                    if(optionsArray.length()==4) {
                        String op1 = optionsArray.getString(0);
                        String op2 = optionsArray.getString(1);
                        String op3 = optionsArray.getString(2);
                        String op4 = optionsArray.getString(3);
                        questionsList.add(new Question(question,op1,op2,op3,op4,answer));
                    }else if(optionsArray.length()==3) {
                        String op1 = optionsArray.getString(0);
                        String op2 = optionsArray.getString(1);
                        String op3 = optionsArray.getString(2);
                        String op4 = "None of the above";
                        questionsList.add(new Question(question, op1, op2, op3, op4, answer));
                    }
                }
                QuestionsMap.put(placeName,questionsList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public QuestionDatabase(Boolean b) {
        String response = null;
        QuestionsMap = new HashMap<>();
        SelfieQuestionsMap = new HashMap<>();
        try {
            response = new MyHttpRequestTask2().execute().get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            JSONArray mainChildNode = new JSONArray(response);
            for(int i =0 ; i < mainChildNode.length();i++){
                JSONObject newChildNode = mainChildNode.getJSONObject(i);
                String hotspot = newChildNode.getString("hotspot");
                String question = newChildNode.getString("question");
                SelfieQuestionsMap.put(hotspot,question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Get Quiz questions JSON
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/quiz/getQuizzes?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    //Get Submission questions JSON
private class MyHttpRequestTask2 extends AsyncTask<String,Integer,String> {
    @Override
    protected String doInBackground(String... params) {
        Map<String, String> req = new HashMap<>();
        String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/upload/getSubmissionQuestion?trail_instance_id="+InstanceDAO.trailInstanceID);
        if (response == null){
            return null;
        }
        return response;
    }
}

    public HashMap<String, String> getSelfieQuestionsMap() {
        return SelfieQuestionsMap;
    }

    public HashMap<String, ArrayList<Question>> getQuestionsMap() {
        return QuestionsMap;
    }
}
