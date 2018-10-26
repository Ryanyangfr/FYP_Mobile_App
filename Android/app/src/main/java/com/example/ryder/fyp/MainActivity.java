package com.example.ryder.fyp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


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
        Intent intent = new Intent(this, UsernameActivity.class);
        startActivity(intent);
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String my_url = params[0];
            String my_data = params[1];
            try {
                URL url = new URL(my_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // setting the  Request Method Type
                httpURLConnection.setRequestMethod("GET");
                // adding the headers for request
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                try{
                    //to tell the connection object that we will be wrting some data on the server and then will fetch the output result
                    httpURLConnection.setDoOutput(true);
                    // this is used for just in case we don't know about the data size associated with our request
                    httpURLConnection.setChunkedStreamingMode(0);

                    // to write tha data in our request
                    OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    outputStreamWriter.write(my_data);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();

                    // to log the response code of your request
//                    Log.d(ApplicationConstant.TAG, "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseCode());
//                     to log the response message from your server after you have tried the request.
//                    //Log.d(ApplicationConstant.TAG, "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseMessage());


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // this is done so that there are no open connections left when this task is going to complete
                    httpURLConnection.disconnect();
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
