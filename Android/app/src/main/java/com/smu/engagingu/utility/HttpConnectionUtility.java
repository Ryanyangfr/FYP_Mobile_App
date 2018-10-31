package com.smu.engagingu.utility;

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
                }else{
                    return null;
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

            // get outputstreamwriter from connection and write json parameters to its body
            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
            out.write(jsonParam.toString());
            out.close();

            //sends and gets the response code
            int responseCode = httpURLConnection.getResponseCode();
            //checks if request is successful
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

    //urlTo = the url to send to (Server IP+Port)
    //params = any information along with the content(image)
    //filepath = path of where the media is stored in the phone
    //filefield = "image"
    //fileMimeType = "image/png"
    public static String multipartPost(String urlTo, Map<String, String> params, String filepath, String filefield, String fileMimeType){
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        System.out.println(filepath);
        System.out.println(params);

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
                System.out.println("ResponseCode: " + connection.getResponseCode());
//                throw new CustomException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
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
//            logger.error(e);
//            throw new CustomException(e);
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
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






// public static String multipartPost(String myurl, String filepath){
//        String boundary = "===" + System.currentTimeMillis() + "===";
//        final String LINE_FEED = "\r\n";
//        HttpURLConnection httpURLConnection = null;
//        PrintWriter writer;
//        StringBuffer response = new StringBuffer();
//
//        try {
//            URL url = new URL(myurl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setRequestMethod("post");
//            httpURLConnection.setRequestProperty("Content-Type",
//                    "multipart/form-data; boundary=" + boundary);
//
//            System.out.println("headers : " + httpURLConnection.getHeaderFields().toString());
//
//            OutputStream outputstream = httpURLConnection.getOutputStream();
//            writer = new PrintWriter(new OutputStreamWriter(outputstream),
//                    true);
//
//            File file = new File(filepath);
//            System.out.println(file.getPath());
//            HttpConnectionUtility.addFilePart(httpURLConnection, boundary, LINE_FEED, writer, file, outputstream);
//            writer.append(LINE_FEED).flush();
//            writer.append("--" + boundary + "--").append(LINE_FEED);
//            writer.close();
//
//            int responseCode = httpURLConnection.getResponseCode();
//            if(responseCode == httpURLConnection.HTTP_OK){
//                String input;
//                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                while((input = in.readLine()) != null){
//                    response.append(input);
//                }
//            } else{
//                System.out.println("Response Status: " + responseCode);
//            }
//
//
//        } catch (Exception e){
//            System.out.println("There is an error");
//            e.getStackTrace();
//            System.out.println(e);
//        }
//        return response.toString();
//    }
//
//    public static void addFilePart(HttpURLConnection urlConnection, String boundary, String LINE_FEED, PrintWriter writer, File uploadFile, OutputStream outputStream)
//            throws IOException {
//        String fileName = uploadFile.getName();
//        System.out.println("Content-Disposition: form-data; name=\"" + "image"
//                + "\"; filename=\"" + fileName + "\"");
//        writer.append("--" + boundary).append(LINE_FEED);
//        writer.append(
//                "Content-Disposition: form-data; name=\"" + "image"
//                        + "\"; filename=\"" + fileName + "\"")
//                .append(LINE_FEED);
//        writer.append(
//                "Content-Type: "
//                        + urlConnection.guessContentTypeFromName(fileName))
//                .append(LINE_FEED);
//        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
//        writer.append(LINE_FEED);
//        writer.flush();
//
//        FileInputStream inputStream = new FileInputStream(uploadFile);
//        byte[] buffer = new byte[4096];
//        int bytesRead = -1;
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, bytesRead);
//        }
//        outputStream.flush();
//        inputStream.close();
//        writer.append(LINE_FEED);
//        writer.flush();
//    }
//
//    public static void addFormField(PrintWriter writer, String LINE_FEED, String boundary, String name, String value) {
//        writer.append("--" + boundary).append(LINE_FEED);
//        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
//                .append(LINE_FEED);
////        writer.append("Content-Type: text/plain; charset=" + charset).append(
////                LINE_FEED);
//        writer.append(LINE_FEED);
//        writer.append(value).append(LINE_FEED);
//        writer.flush();
//    }
}
