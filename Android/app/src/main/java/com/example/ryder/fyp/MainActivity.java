package com.example.ryder.fyp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.ryder.fyp.utility.HttpConnectionUtility;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {

        EditText editText = (EditText) findViewById(R.id.editText3);
        //String message = ;
        String my_url = "http://54.255.245.23:3000/user/register";
        new MyHttpRequestTask().execute(my_url,"");
        Intent intent = new Intent(this, UserName.class);
        startActivity(intent);
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
//            System.out.println("activated");
            Map<String, String> req = new HashMap<>();
//            req.put("username", "THINN");
//            req.put("user_id", "10");
//            req.put("team_id", "1");
            String response = HttpConnectionUtility.post(params[0], req);
            System.out.print("response: " + response);
            return response;
        }
    }
}
