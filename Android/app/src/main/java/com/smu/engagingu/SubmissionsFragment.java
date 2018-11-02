package com.smu.engagingu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public class SubmissionsFragment extends Fragment {

    //String[] HOTSPOTS = {"Li Ka Shing Library", "School of Law"};
    //String[] MISSIONS = {"Take a selfie with mission statement", "Take a selfie in the lawyer room"};
    //int[] IMAGES = {R.drawable.engagingulogo, R.drawable.pixel_link};

    private ArrayList<String> HOTSPOTS = new ArrayList<>();
    private ArrayList<String> QUESTIONS = new ArrayList<>();
    private ArrayList<String> IMAGEURLS = new ArrayList<>();
    private ArrayList<String> IMAGEPATHS = new ArrayList<>();
    private String teamId = "1";
    private String trailInstanceId = "1";
    private String submissionEndPoint = "http://54.255.245.23:3000/upload/getAllSubmissionURL?team=" + teamId + "&trail_instance_id=" + trailInstanceId;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_submissions,container, false);

        //Retrieve Submission details and ImageURLS
        SubmissionRetriever submissionRetriever = new SubmissionRetriever();
        try {
            submissionRetriever.execute(submissionEndPoint).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        System.out.println("Hotspots: " + HOTSPOTS);
        System.out.println("Questions: " + QUESTIONS);
        System.out.println("ImageURLS: " + IMAGEURLS);

        //Retrieve Images and save to phone
        for (int i = 0; i < IMAGEURLS.size(); i++) {

            try {
                //Create Image File
                String imagePath = createImageFile();
                System.out.println(i + " imagePath: " + imagePath);
                IMAGEPATHS.add(imagePath);

                //Download image into Image file
                new ImageRetriever().execute(IMAGEURLS.get(i), imagePath).get();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        ListView listView = view.findViewById(R.id.listView);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        listView.setAdapter(customAdaptor);

        return view;
    }

    private class SubmissionRetriever extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {

            String submissionResponse = HttpConnectionUtility.get(params[0]);
            try {
                JSONArray submissionJsonArr= new JSONArray(submissionResponse);
                for(int i=0; i<submissionJsonArr.length(); i++){
                    JSONObject jsonObj = submissionJsonArr.getJSONObject(i);

                    String imageURL = jsonObj.getString("SubmissionURL");
                    String hotspot = jsonObj.getString("hotspot");
                    String question = jsonObj.getString("question");

                    System.out.println(i + ". imageURL: " + imageURL);
                    System.out.println(i + ". hotspot: " + hotspot);
                    System.out.println(i + ". question: " + question);

                    IMAGEURLS.add(imageURL);
                    HOTSPOTS.add(hotspot);
                    QUESTIONS.add(question);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return submissionResponse;
        }

    }

    private class ImageRetriever extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {

            String imageUrl = params[0];
            String destinationFile = params[1];

            try {
                URL url = new URL("http://54.255.245.23:3000/upload/getSubmission?url=" + imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(is);

                FileOutputStream out = new FileOutputStream(destinationFile);
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                out.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

    }

    private String createImageFile() throws IOException {
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

    private class CustomAdaptor extends BaseAdapter{

        private ImageView imageView;

        @Override
        public int getCount() {
            return IMAGEPATHS.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_submission_layout, null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            String imagePath = IMAGEPATHS.get(i);

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap rotatedBitmap = checkImageIfNeedRotation(bitmap, imagePath);

            imageView.setImageBitmap(rotatedBitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    System.out.println("image clicked...");//check logcat
                }
            });
            TextView textView_hotspot = (TextView)view.findViewById(R.id.textView_hotspot);
            TextView textView_mission = (TextView)view.findViewById(R.id.textView_question);

            textView_hotspot.setText(HOTSPOTS.get(i));
            textView_mission.setText(QUESTIONS.get(i));

            return view;
        }

        private Bitmap checkImageIfNeedRotation(Bitmap img, String imagePath){

            try{
                ExifInterface ei = new ExifInterface(imagePath);
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

//        public void loadImageFromFile(ImageView imageView, String imagePath){
//
//            imageView.setVisibility(View.VISIBLE);
//
//            int targetW = imageView.getWidth();
//            int targetH = imageView.getHeight();
//
//            // Get the dimensions of the bitmap
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imagePath, bmOptions);
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//            //Decode the image file into a Bitmap sized to fill the View
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;
//
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
//            imageView.setImageBitmap(bitmap);
//        }
    }
}