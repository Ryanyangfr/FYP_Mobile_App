package com.smu.engagingu.Game;

import android.os.AsyncTask;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Utilities.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
/*
 * QuestionType is used for pulling the information needed for all the game modes from the database
 * this is used to determine the relationship between hotspot and their respective game-modes
 * to be used for future narrative to game redirection.
 */
public class QuestionType {
    private HashMap<String,String> QuestionTypeMap;

    public QuestionType() {
        QuestionTypeMap = new HashMap<>();
        String responseQuiz = null;
        String responseSelfie = null;
        String responseDrawing = null;
        String responseAnagram = null;
        String responseDragAndDrop = null;
        String responseWordsSearch = null;
        try {
            responseQuiz = new QuestionType.MyHttpRequestTaskQuiz().execute().get();
            responseSelfie = new QuestionType.MyHttpRequestTaskSelfie().execute().get();
            responseDrawing = new QuestionType.MyHttpRequestTaskDrawing().execute().get();
            responseAnagram = new QuestionType.MyHttpRequestTaskAnagram().execute().get();
            responseDragAndDrop = new QuestionType.MyHttpRequestTaskDragAndDrop().execute().get();
            responseWordsSearch = new QuestionType.MyHttpRequestTaskWordsSearch().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonMainNode;
        JSONArray jsonMainNode2;
        JSONArray jsonMainNode3;
        JSONArray jsonMainNode4;
        JSONArray jsonMainNode5;
        JSONArray jsonMainNode6;
        try {
            jsonMainNode = new JSONArray(responseQuiz);
            if(jsonMainNode.length()!=0) {
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String placeName = jsonChildNode.getString("hotspot");
                    QuestionTypeMap.put(placeName, "1");
                }
            }
            jsonMainNode2 = new JSONArray(responseSelfie);
            if(jsonMainNode2.length()!=0) {
                for (int i = 0; i < jsonMainNode2.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode2.getJSONObject(i);
                    String placeName = jsonChildNode.getString("hotspot");
                    QuestionTypeMap.put(placeName, "2");
                }
            }
            jsonMainNode3 = new JSONArray(responseDrawing);
            if(jsonMainNode3.length()!=0) {
                for (int i = 0; i < jsonMainNode3.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode3.getJSONObject(i);
                    String placeName = jsonChildNode.getString("hotspot");
                    QuestionTypeMap.put(placeName, "3");
                }
            }
            jsonMainNode4 = new JSONArray(responseAnagram);
            if(jsonMainNode4.length()!=0) {
                for (int i = 0; i < jsonMainNode4.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode4.getJSONObject(i);
                    String placeName = jsonChildNode.getString("hotspot");
                    QuestionTypeMap.put(placeName, "4");
                }
            }
            jsonMainNode5 = new JSONArray(responseDragAndDrop);
            if(jsonMainNode5.length()!=0) {
                for (int i = 0; i < jsonMainNode5.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode5.getJSONObject(i);
                    String placeName = jsonChildNode.getString("hotspot");
                    QuestionTypeMap.put(placeName, "5");
                }
            }
            jsonMainNode6 = new JSONArray(responseWordsSearch);
            if(jsonMainNode6.length()!=0) {
                for (int i = 0; i < jsonMainNode6.length(); i++) {
                    JSONObject hotspot = jsonMainNode6.getJSONObject(i);
                    String placeName = hotspot.getString("hotspot");
                    QuestionTypeMap.put(placeName, "6");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //pull all quiz questions
    private class MyHttpRequestTaskQuiz extends AsyncTask<String,Integer,String> {
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
    //pull all selfie questions
    private class MyHttpRequestTaskSelfie extends AsyncTask<String,Integer,String> {
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
    //pull all drawing questions
    private class MyHttpRequestTaskDrawing extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/upload/getDrawingQuestion?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    //pull all anagram questions
    private class MyHttpRequestTaskAnagram extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/anagram/getAnagrams?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    //pull all drag and drop questions
    private class MyHttpRequestTaskDragAndDrop extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/draganddrop/getDragAndDrop?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    //pull all word search questions
    private class MyHttpRequestTaskWordsSearch extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/wordsearch/getWordSearchWords?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }

    public HashMap<String, String> getQuestionTypeMap() {
        return QuestionTypeMap;
    }
}
