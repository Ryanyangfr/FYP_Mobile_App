package com.smu.engagingu;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class UserName extends AppCompatActivity {
    public static String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
    }
    public void sendUserName(View view) throws ExecutionException, InterruptedException {
        userID= new MyHttpRequestTask().execute("http://54.255.245.23:3000/user/register").get();
        Intent intent = new Intent(this, StoryContainer.class);
        startActivity(intent);
    }

    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            EditText editText = (EditText) findViewById(R.id.editUsername);
            String message = editText.getText().toString();
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("username",message);
            String response = HttpConnectionUtility.post("http://54.255.245.23:3000/user/register",userHash);
            try {
                JSONObject userObject = new JSONObject(response);
                return userObject.getString("team_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
