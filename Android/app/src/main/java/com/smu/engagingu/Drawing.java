package com.smu.engagingu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.DAO.SubmissionDAO;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.Utilities.PaintView;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
/*
 * Drawing refers to the page that displays the drawing game mode. This game mode is also only available
 * for leaders. If user is not a leader, an informational page would be displayed to inform the user of
 * the next viable steps.
 */
public class Drawing extends AppCompatActivity {
    String mCurrentPhotoPath;
    File photoFile;

    public static final String QUESTION= "com.smu.engagingu.QUESTION";
    private String targetQuestion;
    private String placeName;
    private PaintView paintView;
    private TextView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
        //paintView is the canvas object
        paintView = (PaintView) findViewById(R.id.paintView);
        questionView = findViewById(R.id.textView13);
        getQuestion();
        if(!InstanceDAO.isLeader){
            Intent intent2 = new Intent(Drawing.this, MemberSubmissionPage.class);
            intent2.putExtra(QUESTION,targetQuestion);
            InstanceDAO.completedList.add(placeName);
            startActivity(intent2);
        }
        questionView.setText(targetQuestion);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        Button btn = (Button) findViewById(R.id.submitButton);
        Button clearBtn = findViewById(R.id.clear);
        Button redBtn = findViewById(R.id.red);
        Button blueBtn = findViewById(R.id.blue);
        Button greenBtn = findViewById(R.id.green);
        Button yellowBtn = findViewById(R.id.yellow);
        Button blackBtn = findViewById(R.id.black);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
            }
        });
        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.setCurrentColor(Color.RED);
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.setCurrentColor(Color.BLUE);
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.setCurrentColor(Color.GREEN);
            }
        });
        yellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.setCurrentColor(Color.YELLOW);
            }
        });
        blackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.setCurrentColor(Color.BLACK);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Drawing.this);
                builder.setCancelable(true);
                builder.setTitle("Drawing Challenge");
                builder.setMessage("Are you sure you want to submit now?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String responseCode = null;
                                paintView.setDrawingCacheEnabled(true);
                                Bitmap b = paintView.getDrawingCache();
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    FileOutputStream ostream = null;
                                    try {
                                        ostream = new FileOutputStream(photoFile);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    b.compress(Bitmap.CompressFormat.PNG, 50, ostream);
                                    try {
                                        Objects.requireNonNull(ostream).close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        responseCode = new PictureUploader().execute(InstanceDAO.teamID, InstanceDAO.trailInstanceID, targetQuestion, placeName).get();
                                        SubmissionDAO.HOTSPOTS.add(placeName);
                                        SubmissionDAO.QUESTIONS.add(targetQuestion);
                                        SubmissionDAO.IMAGEPATHS.add(mCurrentPhotoPath);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    if(responseCode.equals("fail")|| responseCode.equals("")) {
                                        Toast toast = Toast.makeText(Drawing.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }else {
                                        Toast toast = Toast.makeText(Drawing.this, "Photo Successfully Uploaded!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(Drawing.this, HomePage.class);
                                        InstanceDAO.completedList.add(placeName);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    /*
     * method to get drawing question from the database
     */
    private void getQuestion(){
        String word;
        try {
            word = new MyHttpRequestTask().execute("").get();
            JSONArray jsonMainNode = new JSONArray(word);
            for (int i = 0 ; i < jsonMainNode.length();i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                if (jsonChildNode.getString("hotspot").equals(placeName)) {
                    targetQuestion = jsonChildNode.getString("question");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*
     * create the image file for the canvas object
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,    /*prefix */
                ".jpg",    /*suffix */
                storageDir    /*directory*/
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /*
     * uploads drawing picture onto the database via multipart post
     */
    private class PictureUploader extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            //team_id, trail_instance_id, question to get from database
            Map<String,String> jsonMap = new HashMap<>();
            jsonMap.put("team_id", params[0]);
            jsonMap.put("trail_instance_id", params[1]);
            jsonMap.put("question", params[2]);
            jsonMap.put("hotspot",params[3]);
            String responseCode = HttpConnectionUtility.multipartPost("https://amazingtrail.ml/api/upload/uploadSubmission", jsonMap, mCurrentPhotoPath, "image", "image/png");
            return responseCode;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.paintmain, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //get drawing question mission
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/upload/getDrawingQuestion?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
