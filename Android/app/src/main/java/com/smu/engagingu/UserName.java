package com.smu.engagingu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.StoryLine.Welcome;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.Utilities.HttpConnectionUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class UserName extends AppCompatActivity {
    private Socket mSocket;
    private String message;
    private EditText mInputMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        attemptSend();
    }

    public void sendUserName(View view) throws ExecutionException, InterruptedException {
        InstanceDAO.teamID = new MyHttpRequestTask().execute("https://amazingtrail.ml/api/user/register").get();
        if(message.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a valid username!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else {
            Intent intent = new Intent(this, Welcome.class);
            InstanceDAO.userName=message;
            startActivity(intent);
        }
    }
    private void attemptSend() {
       // String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        //mInputMessageView.setText("Hello World");
        mSocket.emit("new message", "Hello World - from Ryder");
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            EditText editText = (EditText) findViewById(R.id.editUsername);
            message = editText.getText().toString();
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("username",message);
            String response = HttpConnectionUtility.post("https://amazingtrail.ml/api/user/register",userHash);
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
