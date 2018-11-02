package com.smu.engagingu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.Quiz.QuestionDatabase;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CameraPage extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.smu.engagingu.MESSAGE";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button takePictureButton;
    Button uploadButton;
    ImageView mImageView;
    Uri photoURI;
    String mCurrentPhotoPath;
    private String targetQuestion;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_page);
        takePictureButton = findViewById(R.id.btnTakePicture);
        String jsonResponse = null;
        Intent intent = getIntent();
        placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
        TextView selfieQuestionView = findViewById(R.id.selfieQuestionView);
        QuestionDatabase qDB = new QuestionDatabase(true);
        HashMap<String,String> selfieQuestionMap = qDB.getSelfieQuestionsMap();
        targetQuestion = selfieQuestionMap.get(placeName);
        selfieQuestionView.setText(targetQuestion);
        takePictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                try {
                    dispatchTakePictureIntent();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        uploadButton = findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String team_id = UserName.userID;
                String trail_instance_id = "1";
                String question = targetQuestion;
                try {
                    String responseCode = new PictureUploader().execute(team_id,trail_instance_id,question).get();
                    System.out.println("Response Code: " + responseCode);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(CameraPage.this,"Photo Successfully Uploaded!", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(CameraPage.this,HomePage.class);
                intent.putExtra(EXTRA_MESSAGE, placeName);
                startActivity(intent);
            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException e){
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null){
                photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }else{
            Log.d("dispatchTakePictureIntent", "No applications with camera functionality");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            galleryAddPic();
            loadImageFromFile();
        }
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

    private Bitmap checkImageIfNeedRotation(Bitmap img){

        try{
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    private Bitmap rotateImage(Bitmap img, int degree) {

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public void loadImageFromFile(){

        mImageView = this.findViewById(R.id.imageView);
        mImageView.setVisibility(View.VISIBLE);

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        //Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = checkImageIfNeedRotation(bitmap);
        mImageView.setImageBitmap(rotatedBitmap);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        System.out.println("Uri in galleryAddPic: " + contentUri);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private class PictureUploader extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            //team_id, trail_instance_id, question to get from database
            Map<String,String> jsonMap = new HashMap<>();
            jsonMap.put("team_id", params[0]);
            jsonMap.put("trail_instance_id", params[1]);
            jsonMap.put("question", params[2]);
            String responseCode = HttpConnectionUtility.multipartPost("http://54.255.245.23:3000/upload/uploadSubmission", jsonMap, mCurrentPhotoPath, "image", "image/png");
            return responseCode;
        }
    }
}
