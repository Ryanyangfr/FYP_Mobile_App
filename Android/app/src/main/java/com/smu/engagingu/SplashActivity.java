package com.smu.engagingu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.DAO.Session;
import com.smu.engagingu.DAO.SubmissionDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 10;
    private ArrayList<String>userList = new ArrayList<>();
    private String response2 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InstanceDAO.userName=Session.getUsername(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showPhoneStatePermission();

        try {
            String response = new getAllUsers().execute("").get();
            response2 = new getStartingHotspot().execute("").get();
            if(response!=null) {
                JSONObject mainObject = new JSONObject(response);
                JSONArray mainChildNode = mainObject.getJSONArray("username");
                for(int i =0 ; i < mainChildNode.length();i++){
                    userList.add(mainChildNode.getString(i));
                }
                InstanceDAO.userList = userList;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getLoginStatus(Session.getUsername(getApplicationContext()))) {
            startHeavyProcessing();
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InstanceDAO.teamID = Session.getTeamID(getApplicationContext());
            InstanceDAO.trailInstanceID = Session.getTrailInstanceID(getApplicationContext());
            InstanceDAO.firstTime = Session.getFirstTime(getApplicationContext());
                try {
                    String endpointURL = SubmissionDAO.submissionEndPoint;
                    String submissionResponse = HttpConnectionUtility.get(endpointURL);
                    JSONArray submissionJsonArr= new JSONArray(submissionResponse);
                    int jsonArrLength = submissionJsonArr.length();
                    JSONObject jsonSizeObj = submissionJsonArr.getJSONObject(jsonArrLength-1);

                    int size = Integer.parseInt(jsonSizeObj.getString("size"));
                    System.out.println("Size: " + size);

                    for (int i = 0; i < submissionJsonArr.length() - 1; i++) {
                        JSONObject jsonObj = submissionJsonArr.getJSONObject(i);

                        String imageURL = jsonObj.getString("SubmissionURL");
                        String hotspot = jsonObj.getString("hotspot");
                        String question = jsonObj.getString("question");

                        System.out.println(i + ". imageURL: " + imageURL);
                        System.out.println(i + ". hotspot: " + hotspot);
                        System.out.println(i + ". question: " + question);

                        SubmissionDAO.IMAGEURLS.add(imageURL);
                        SubmissionDAO.HOTSPOTS.add(hotspot);
                        SubmissionDAO.QUESTIONS.add(question);
                    }

                    for (int i=0; i<SubmissionDAO.IMAGEURLS.size(); i++) {

                        String imageUrl = SubmissionDAO.IMAGEURLS.get(i);

                        //Create Image File
                        String imagePath = createImageFile();
                        System.out.println(i + " imagePath: " + imagePath);
                        SubmissionDAO.IMAGEPATHS.add(imagePath);

                        //Download Image
                        URL url = new URL(SubmissionDAO.imageEndPoint + imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream is = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(is);

                        FileOutputStream out = new FileOutputStream(imagePath);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.close();
                        is.close();
                    }
                    JSONArray jsonMainNode2 = new JSONArray(response2);
                    for (int i = 0; i < jsonMainNode2.length(); i++) {
                        JSONObject jsonChildNode2 = jsonMainNode2.getJSONObject(i);
                        String teamID = jsonChildNode2.getString("team");
                        if (teamID.equals(InstanceDAO.teamID)) {
                            String startingHotspot2 = jsonChildNode2.getString("startingHotspot");
                            JSONArray latlng2 = jsonChildNode2.getJSONArray("coordinates");
                            String latString2 = latlng2.getString(0);
                            Double lat2 = Double.parseDouble(latString2);
                            String lngString2 = latlng2.getString(1);
                            Double lng2 = Double.parseDouble(lngString2);
                            String narrativeString2 = jsonChildNode2.getString("narrative");
                            InstanceDAO.startingHotspot = new Hotspot(startingHotspot2,lat2,lng2,narrativeString2);
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        } else {
//            Toast.makeText(MainActivity.this, "The app was not allowed to read your store.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    private static Boolean getLoginStatus(String userName){
        if(InstanceDAO.userList.contains(userName)){
            return true;
        }else{
            return false;
        }
    }
    private class getAllUsers extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/user/retrieveAllUsers");
            if (response == null) {
                return null;
            }
            return response;
        }
    }
    private static String createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        System.out.println("Storage DIR" + storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,    /*prefix */
                ".jpg",    /*suffix */
                storageDir    /*directory*/
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image.getAbsolutePath();
    }
    private class getStartingHotspot extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/team/startingHotspot?trail_instance_id=" + InstanceDAO.trailInstanceID);
            if (response == null) {
                return null;
            }
            return response;
        }
    }
}

