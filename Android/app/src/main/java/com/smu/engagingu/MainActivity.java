package com.smu.engagingu;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.DAO.Session;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPhoneStatePermission();
        //Session.setLoggedIn(getApplicationContext(),false);
        if(Session.getLoggedStatus(getApplicationContext())) {
            populateInstanceDAO();
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
        } else {

        }
    }

    public void sendMessage(View view) {
        String jsonString = null;
        try {
            jsonString= new MyHttpRequestTask().execute("http://54.255.245.23:3000/getInstance").get();
            JSONObject jsonObject = new JSONObject(jsonString);
            InstanceDAO.trailInstanceID = jsonObject.getString("trail_instance_id");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EditText editText = (EditText) findViewById(R.id.editText3);
        String message = editText.getText().toString();
        //String message = ;
        //String my_url = "http://10.124.5.151:3000/upload/uploadSubmission";
        //new MyHttpRequestTask().execute(my_url,"");

        if(message.equals(InstanceDAO.trailInstanceID)) {
            Intent intent = new Intent(this, UserName.class);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Wrong PIN! Please try again.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
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
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
    private void populateInstanceDAO(){
        InstanceDAO.teamID = Session.getTeamID(getApplicationContext());
        System.out.println("teamID: "+InstanceDAO.teamID);
        InstanceDAO.trailInstanceID = Session.getTrailInstanceID(getApplicationContext());
        System.out.println("trailInstanceID: "+InstanceDAO.trailInstanceID);
        InstanceDAO.firstTime = Session.getFirstTime(getApplicationContext());


    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/getInstance");
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
