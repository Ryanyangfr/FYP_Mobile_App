package com.smu.engagingu;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.smu.engagingu.Adapters.EventAdapter;
import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.DAO.Session;
import com.smu.engagingu.DAO.SubmissionDAO;
import com.smu.engagingu.Hotspot.Hotspot;
import com.smu.engagingu.Objects.Event;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.Utilities.SocketHandler;
import com.smu.engagingu.fyp.R;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 10;
    private ArrayList<String>userList = new ArrayList<>();
    //private RecyclerView.LayoutManager lManager;
    //private PusherOptions options = new PusherOptions().setCluster("ap1");
    //private Pusher pusher = new Pusher("1721c662be60b9cbd43c", options);
    //private static final String CHANNEL_NAME = "events_to_be_shown";
    private String startingHotspotString = "";
    private String activityFeedString = "";
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InstanceDAO.userName=Session.getUsername(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SocketHandler socketHandler = new SocketHandler();
        mSocket = socketHandler.getSocket();
        mSocket.on("activityFeed",onNewFeedMessage);
        mSocket.on("startTrail",onStartTrail);
        mSocket.on("notification",onNotification);
        mSocket.connect();

        // Use LinearLayout as the layout manager

        List<Event> eventList = new ArrayList<>();
        InstanceDAO.adapter = new EventAdapter(eventList);
        /*options.setCluster("ap1");
        Pusher pusher = new Pusher("1721c662be60b9cbd43c", options);
        Channel channel = pusher.subscribe(CHANNEL_NAME);
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
        SubscriptionEventListener eventListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, final String event, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()  {
                        System.out.println("Received event with data: " + data);
                        //Gson gson = new Gson();
                        JSONObject mainChildNode;
                        try {
                            mainChildNode = new JSONObject(data);
                            String feedTeamID = mainChildNode.getString("team_id");
                            String eventID = mainChildNode.getString("id");
                            String feedTeamName = "Team "+feedTeamID+" has just connected.";
                            Event evt = new Event(feedTeamName,eventID,data,timeStamp);
                            //evt.setName(event + ":");
                            InstanceDAO.adapter.addEvent(evt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        channel.bind("created", eventListener);
        channel.bind("updated", eventListener);
        channel.bind("deleted", eventListener);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
                e.printStackTrace();
            }
        });*/

        showPhoneStatePermission();

        try {
            String response = new getAllUsers().execute("").get();
            startingHotspotString = new getStartingHotspot().execute("").get();
            activityFeedString = new getActivityFeed().execute("").get();
            if(response!=null) {
                JSONArray mainChildNode = new JSONArray(response);
                for(int i =0 ; i < mainChildNode.length();i++){
                    JSONObject firstChildNode = mainChildNode.getJSONObject(i);
                    String userName = firstChildNode.getString("username");
                    userList.add(userName);
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
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        if(getLoginStatus(Session.getUsername(getApplicationContext()))) {
            startHeavyProcessing();
        }else{
            if(!connected){
                Toast.makeText(this, "Bad Wifi Connection! Try again later", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private Emitter.Listener onNewFeedMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String team;
                    String hotspot;
                    String message;
                    String time;
                    try{
                        team = data.getString("team");
                        hotspot = data.getString("hotspot");
                        message = "Team "+team+" has just completed "+hotspot;
                        time = data.getString("time");
                    }catch(JSONException e){
                        return;
                    }
                    //System.out.println(args[0]);
                    //String s = (String)args[0];
                    Event evt = new Event(message,team,hotspot,time);
                    //evt.setName(event + ":");
                    System.out.println("Activity Feed Update");
                    InstanceDAO.adapter.addEvent(evt);
                }
            });
        }
    };
    private Emitter.Listener onStartTrail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InstanceDAO.startTrail = true;
                }
            });
        }
    };
    private Emitter.Listener onNotification= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Received");
                    JSONObject data = (JSONObject) args[0];
                    String message = "";
                    try {
                        message = data.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showNotification("Notification",message);
                }
            });
        }
    };
    private void showNotification(String title, String content){
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "CHANNEL",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InstanceDAO.teamID = Session.getTeamID(getApplicationContext());
            InstanceDAO.trailInstanceID = Session.getTrailInstanceID(getApplicationContext());
            InstanceDAO.firstTime = Session.getFirstTime(getApplicationContext());
            InstanceDAO.isLeader = Session.getIsLeader(getApplicationContext());
                try {
                    String endpointURL = SubmissionDAO.submissionEndPoint;
                    String submissionResponse = HttpConnectionUtility.get(endpointURL);
                    JSONArray submissionJsonArr= new JSONArray(submissionResponse);
                    System.out.println(submissionJsonArr.length());
                    //int jsonArrLength = submissionJsonArr.length();
                   // JSONObject jsonSizeObj = submissionJsonArr.getJSONObject(jsonArrLength-1);

//                    int size = Integer.parseInt(jsonSizeObj.getString("size"));
//                    System.out.println("Size: " + size);

                    for (int i = 0; i < submissionJsonArr.length(); i++) {
                        JSONObject jsonObj = submissionJsonArr.getJSONObject(i);

                        String imageURL = jsonObj.getString("submissionURL");
                        System.out.println("imageURL: "+imageURL);
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

                    JSONArray jsonMainNode2 = new JSONArray(startingHotspotString);
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
                    JSONArray jsonMainNode3 = new JSONArray(activityFeedString);
                    for(int i =0; i < jsonMainNode3.length();i++){
                        JSONObject jsonChildNode3 = jsonMainNode3.getJSONObject(i);
                        String team = jsonChildNode3.getString("team");
                        String hotspot = jsonChildNode3.getString("hotspot");
                        String message = "Team "+team+" has just completed "+hotspot;
                        String time = jsonChildNode3.getString("time");
                        Event evt = new Event(message,team,hotspot,time);
                        InstanceDAO.adapter.addEvent(evt);
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
            String response = HttpConnectionUtility.get("http://13.229.115.32:3000/user/retrieveAllUser");
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
            String response = HttpConnectionUtility.get("http://13.229.115.32:3000/team/startingHotspot?trail_instance_id=" + InstanceDAO.trailInstanceID);
            if (response == null) {
                return null;
            }
            return response;
        }
    }
    private class getActivityFeed extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpConnectionUtility.get("http://13.229.115.32:3000/team/activityfeed");
            if (response == null) {
                return null;
            }
            return response;
        }
    }
}

