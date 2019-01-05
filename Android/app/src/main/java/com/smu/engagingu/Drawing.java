package com.smu.engagingu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.smu.engagingu.DAO.SubmissionDAO;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;
import com.smu.engagingu.utility.PaintView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Drawing extends AppCompatActivity {
    Button submitButton;
    TextView question;
    String mCurrentPhotoPath;
    File photoFile;
    Uri photoURI;
    private String targetQuestion = "drawingTest";
    private String placeName;
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*PaintView paintView = new PaintView(this);
        setContentView(paintView);*/
        setContentView(R.layout.activity_drawing);


        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        //RelativeLayout parent = (RelativeLayout)findViewById(R.id.signImageParent);
        //parent.addView(paintView);


        Button btn = (Button) findViewById(R.id.submitButton);
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
                                        ostream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String team_id = "3";
                                    String trail_instance_id = "175239";
                                    String question = "Locate the Kwa Geok Choo Library and take a wefie next to the scenic elevator!";
                                    placeName = "School of Law";
                                    try {
                                        String responseCode = new PictureUploader().execute(team_id, trail_instance_id, question, placeName).get();

                                        SubmissionDAO.HOTSPOTS.add(placeName);
                                        SubmissionDAO.QUESTIONS.add(question);
                                        SubmissionDAO.IMAGEPATHS.add(mCurrentPhotoPath);

                                        System.out.println("Response Code: " + responseCode);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    Toast toast = Toast.makeText(Drawing.this, "Photo Successfully Uploaded!", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Intent intent = new Intent(Drawing.this, HomePage.class);
                                    paintView.clear();
                                    //intent.putExtra(EXTRA_MESSAGE, placeName);

                                    //*** startActivity(intent);
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
    private File createImageFile() throws IOException {
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private class PictureUploader extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            //team_id, trail_instance_id, question to get from database
            Map<String,String> jsonMap = new HashMap<>();
            jsonMap.put("team_id", params[0]);
            jsonMap.put("trail_instance_id", params[1]);
            jsonMap.put("question", params[2]);
            jsonMap.put("hotspot",params[3]);
            String responseCode = HttpConnectionUtility.multipartPost("http://54.255.245.23:3000/upload/uploadSubmission", jsonMap, mCurrentPhotoPath, "image", "image/png");
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
}
