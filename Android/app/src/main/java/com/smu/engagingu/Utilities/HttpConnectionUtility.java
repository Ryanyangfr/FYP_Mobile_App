package com.smu.engagingu.Utilities;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/*
 * HTTPConnectionUtility contains all the RESTful methods used to post and get JSONData from
 * the database
 */
public class HttpConnectionUtility{
    /*
     * The get method is used to get data from the database. The input parameter is the url that
     * the program would have to get the information from.
     */
    public static String get(String myurl) {
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(myurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //a connection timeout is set to prevent the program from hanging
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setReadTimeout(4000);
            // setting the  Request Method Type
            httpURLConnection.setRequestMethod("GET");
            // adding the headers for request
            httpURLConnection.setRequestProperty("Content-Type", "application/text");
            try{
                int responseCode = httpURLConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String input;
                    while((input = in.readLine()) != null){
                        response.append(input);
                    }
                    in.close();
                }else{
                    return "fail";
                }

            }catch(java.net.SocketTimeoutException e){
                e.printStackTrace();
                return "fail";
            }
            catch (Exception e){
                e.printStackTrace();
                return "fail";
            }finally {
                // this is done so that there are no open connections left when this task is going to complete
                httpURLConnection.disconnect();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return response.toString();
    }
    /*
     * The post method is used to upload score to the database
     * The input parameters are the url that the program would have to post to and
     * a hashmap containing the specific information that the program intends to upload
     */
    public static String post(String myurl, Map<String,String> params){
        StringBuffer response = new StringBuffer();
        HttpURLConnection httpURLConnection = null;


        try {
            URL url = new URL(myurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setReadTimeout(4000);
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

            // get outputstreamwriter from connection and write json parameters to its body
            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            //sends and gets the response code
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

    /*
     * MultipartPost is used to upload images to the database
     * The input parameters are as follows:
     * urlTo = the url to send to (Server IP+Port)
     * params = any information along with the content(image)
     * filepath = path of where the media is stored in the phone
     * filefield = "image"
     * fileMimeType = "image/png"
     */
    public static String multipartPost(String urlTo, Map<String, String> params, String filepath, String filefield, String fileMimeType){
        HttpURLConnection connection = null;
        DataOutputStream outputStream;
        InputStream inputStream;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(4000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                return "fail";
            }


            inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
