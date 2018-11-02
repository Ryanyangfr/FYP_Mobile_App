package com.smu.engagingu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class UserName extends AppCompatActivity {
    public static String userID;
    private String message;
    public static final String UserName_EXTRA_MESSAGE = "com.smu.engagingu.UserName_MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
    }
    public void sendUserName(View view) throws ExecutionException, InterruptedException {
        userID= new MyHttpRequestTask().execute("http://54.255.245.23:3000/user/register").get();
        if(message.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a valid username!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else {
            Intent intent = new Intent(this, TeamDisplay.class);
            intent.putExtra(UserName_EXTRA_MESSAGE, userID);
            startActivity(intent);
        }
    }

    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            EditText editText = (EditText) findViewById(R.id.editUsername);
            message = editText.getText().toString();
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
