package com.smu.engagingu.Quiz;

import android.os.AsyncTask;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QuestionType {
    private HashMap<String,String> QuestionTypeMap;
    private HashMap<String,String> SelfieQuestionsMap;

    public QuestionType() {
        QuestionTypeMap = new HashMap<>();
        String responseQuiz = null;
        String responseSelfie = null;
        String responseDrawing = null;
        String responseAnagram = null;
        String responseDragAndDrop = null;
        try {
            responseQuiz = new QuestionType.MyHttpRequestTaskQuiz().execute().get();
            responseSelfie = new QuestionType.MyHttpRequestTaskSelfie().execute().get();
            responseDrawing = new QuestionType.MyHttpRequestTaskDrawing().execute().get();
            responseAnagram = new QuestionType.MyHttpRequestTaskAnagram().execute().get();
            responseDragAndDrop = new QuestionType.MyHttpRequestTaskDragAndDrop().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonMainNode = null;
        JSONArray jsonMainNode2 = null;
        JSONArray jsonMainNode3 = null;
        JSONArray jsonMainNode4 = null;
        JSONArray jsonMainNode5 = null;
        try {
            jsonMainNode = new JSONArray(responseQuiz);
            for(int i = 0; i< jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                System.out.println("quiz: "+placeName);
                QuestionTypeMap.put(placeName,"1");
            }
            jsonMainNode2 = new JSONArray(responseSelfie);
            for(int i = 0; i< jsonMainNode2.length();i++){
                JSONObject jsonChildNode = jsonMainNode2.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                System.out.println("selfie: "+placeName);
                QuestionTypeMap.put(placeName,"2");
            }
            jsonMainNode3 = new JSONArray(responseDrawing);
            for(int i = 0; i< jsonMainNode3.length();i++){
                JSONObject jsonChildNode = jsonMainNode3.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                System.out.println("drawing: "+placeName);
                QuestionTypeMap.put(placeName,"3");
            }
            jsonMainNode4 = new JSONArray(responseAnagram);
            for(int i = 0; i< jsonMainNode4.length();i++){
                JSONObject jsonChildNode = jsonMainNode4.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                System.out.println("anagram: "+placeName);
                QuestionTypeMap.put(placeName,"4");
            }
            jsonMainNode5 = new JSONArray(responseDragAndDrop);
            for(int i = 0; i< jsonMainNode5.length();i++){
                JSONObject jsonChildNode = jsonMainNode5.getJSONObject(i);
                String placeName = jsonChildNode.getString("hotspot");
                System.out.println("dragndrop: "+placeName);
                QuestionTypeMap.put(placeName,"5");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyHttpRequestTaskQuiz extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/quiz/getQuizzes?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private class MyHttpRequestTaskSelfie extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/upload/getSubmissionQuestion?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private class MyHttpRequestTaskDrawing extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/upload/getDrawingQuestion?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private class MyHttpRequestTaskAnagram extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/anagram/getAnagrams?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private class MyHttpRequestTaskDragAndDrop extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/draganddrop/getDragAndDrop?trail_instance_id="+InstanceDAO.trailInstanceID);
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
