package com.smu.engagingu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.SubmissionDAO;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
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
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class SubmissionsFragment extends Fragment {

    //String[] HOTSPOTS = {"Li Ka Shing Library", "School of Law"};
    //String[] MISSIONS = {"Take a selfie with mission statement", "Take a selfie in the lawyer room"};
    //int[] IMAGES = {R.drawable.engagingulogo, R.drawable.pixel_link};
      private String submissionResponse = null;
//    private ArrayList<String> HOTSPOTS = new ArrayList<>();
//    private ArrayList<String> QUESTIONS = new ArrayList<>();
//    private ArrayList<String> IMAGEURLS = new ArrayList<>();
//    private ArrayList<String> IMAGEPATHS = new ArrayList<>();
//    private int oldImageSize = 0;
//    private boolean needsToBeUpdated = false;
//    private String teamId = InstanceDAO.teamID;
//    private String trailInstanceId = InstanceDAO.trailInstanceID;
//    private String submissionEndPoint = "http://13.229.115.32:3000/upload/getAllSubmissionURL?team=" + teamId + "&trail_instance_id=" + trailInstanceId;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_submissions,container, false);
        ProgressBar pb = view.findViewById(R.id.submissionProgress);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        pb.setVisibility(view.VISIBLE);
        ListView listView = view.findViewById(R.id.listView);
        startHeavyProcessing(view,listView,customAdaptor);


        //listView.setAdapter(customAdaptor);

        return view;
    }

    private void startHeavyProcessing(View view, ListView listView,CustomAdaptor customAdaptor){
            new LongOperation(view,listView,customAdaptor).execute("");
    }
    private class LongOperation extends AsyncTask<String, Void, String> {
        private View view;
        private ListView listView;
        private CustomAdaptor customAdaptor;
        public LongOperation(View view, ListView listView,CustomAdaptor customAdaptor){
            this.view = view;
            this.listView = listView;
            this.customAdaptor = customAdaptor;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String endpointURL = SubmissionDAO.submissionEndPoint;
                submissionResponse = HttpConnectionUtility.get(endpointURL);
                    JSONArray submissionJsonArr = new JSONArray(submissionResponse);
                    System.out.println(submissionJsonArr.length());
                    SubmissionDAO.IMAGEURLS = new ArrayList<>();
                    SubmissionDAO.IMAGEPATHS = new ArrayList<>();
                    SubmissionDAO.HOTSPOTS = new ArrayList<>();
                    SubmissionDAO.QUESTIONS = new ArrayList<>();

                    for (int i = 0; i < submissionJsonArr.length(); i++) {
                        JSONObject jsonObj = submissionJsonArr.getJSONObject(i);
                        String imageURL = jsonObj.getString("submissionURL");
                        String hotspot = jsonObj.getString("hotspot");
                        String question = jsonObj.getString("question");
                        SubmissionDAO.IMAGEURLS.add(imageURL);
                        SubmissionDAO.HOTSPOTS.add(hotspot);
                        SubmissionDAO.QUESTIONS.add(question);
                    }
                    for (int i = 0; i < SubmissionDAO.IMAGEURLS.size(); i++) {

                        String imageUrl = SubmissionDAO.IMAGEURLS.get(i);
                        //Create Image File
                        String imagePath = createImageFile();
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
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(submissionResponse.equals("fail") || submissionResponse.equals("")){
                Toast toast = Toast.makeText(getActivity(), "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }
            ProgressBar pb = view.findViewById(R.id.submissionProgress);
            listView.setAdapter(customAdaptor);
            pb.setVisibility(view.GONE);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
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
    private class CustomAdaptor extends BaseAdapter{

        private ImageView imageView;
        private String imagePath;
        private ImageView popupImageView;

        @Override
        public int getCount() {
            return SubmissionDAO.IMAGEPATHS.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_submission_layout, null);

            imageView = view.findViewById(R.id.imageView);
            imagePath = SubmissionDAO.IMAGEPATHS.get(i);

            loadImageFromFile(imageView, imagePath);
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    imagePath = SubmissionDAO.IMAGEPATHS.get(i);
                    System.out.println("image clicked...");//check logcat

                    onButtonShowPopupWindow(v, imagePath);
                }
            });
            TextView textView_hotspot = view.findViewById(R.id.textView_hotspot);
            TextView textView_mission = view.findViewById(R.id.textView_question);

            textView_hotspot.setText(SubmissionDAO.HOTSPOTS.get(i));
            textView_hotspot.setTextColor(Color.parseColor("#151C55"));
            textView_hotspot.setTypeface(null, Typeface.BOLD);
            textView_mission.setText(SubmissionDAO.QUESTIONS.get(i));

            return view;
        }


        private void onButtonShowPopupWindow(View view, final String imagePath) {

            // inflate the layout of the popup window, must require non null
            LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(LAYOUT_INFLATER_SERVICE);

            if(inflater != null) {
                View popupView = inflater.inflate(R.layout.submission_popup_window, null);
                popupImageView = popupView.findViewById(R.id.popupImageView);
                System.out.println("Popup Image View: " + popupImageView);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                System.out.println("PopupImagePath: " + imagePath);
//                Bitmap bitmap = BitmapFactory.decodeFile(popupImagePath);
//                popupImageView.setImageBitmap(bitmap);
                loadImageFromFile(popupImageView, imagePath);

                Button downloadButton = popupView.findViewById(R.id.btnDownload);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        galleryAddPic(imagePath);
                        Toast toast = Toast.makeText(getActivity(),"Photo Successfully Downloaded!", Toast.LENGTH_SHORT);
                        toast.show();
                        popupWindow.dismiss();
                    }
                });

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                //dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return false;
                    }
                });
            }

        }

        private void loadImageFromFile(ImageView imageView, String imagePath){

            imageView.setVisibility(View.VISIBLE);
            System.out.println("ImageView in loadImageFromFile: " + imageView);

            int targetW = imageView.getDrawable().getIntrinsicWidth();
            int targetH = imageView.getDrawable().getIntrinsicHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            //Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
            imageView.setImageBitmap(bitmap);
        }

        private void galleryAddPic(String photoPath) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(photoPath);
            Uri contentUri = Uri.fromFile(f);
            System.out.println("Uri in galleryAddPic: " + contentUri);
            mediaScanIntent.setData(contentUri);

            //send broadcast out must require non null
            Objects.requireNonNull(getActivity()).sendBroadcast(mediaScanIntent);
        }
    }
}