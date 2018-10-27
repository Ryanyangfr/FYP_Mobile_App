package com.example.ryder.fyp.utility;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class HttpConnectionUtility{

    public static String get(String myurl) {
//        String my_url = params[0];
//        String my_data = params[1];
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(myurl);
//                System.out.println(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // setting the  Request Method Type
            httpURLConnection.setRequestMethod("GET");
            // adding the headers for request
            httpURLConnection.setRequestProperty("Content-Type", "application/text");
            try{
                int responseCode = httpURLConnection.getResponseCode();
//                System.out.println("Response Code: " + responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String input;

                    while((input = in.readLine()) != null){
                        response.append(input);
                    }
                    in.close();
//                    System.out.println(response.toString());
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                // this is done so that there are no open connections left when this task is going to complete
                httpURLConnection.disconnect();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return response.toString();
    }

    public static String post(String myurl, Map<String,String> params){
        StringBuffer response = new StringBuffer();
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(myurl);
//                System.out.println(url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // setting the  Request Method Type
            httpURLConnection.setRequestMethod("POST");
            // adding the headers for request
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            //Create JSON object
            JSONObject jsonParam = new JSONObject();
            Set<String> keys = params.keySet();
            for(String key : keys){
                jsonParam.put(key, params.get(key));
            }

            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            int responseCode = httpURLConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String input;
                while((input = in.readLine()) != null){
                    response.append(input);
                }
                in.close();
            }else{
                response.append("error");
            }


        }catch(Exception e){
            e.getStackTrace();
        }finally{
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return response.toString();
    }
}
