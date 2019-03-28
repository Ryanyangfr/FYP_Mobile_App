package com.smu.engagingu;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.Objects.GameResultEntry;
import com.smu.engagingu.Results.DragDropResults;
import com.smu.engagingu.Utilities.HttpConnectionUtility;
import com.smu.engagingu.fyp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DragDrop extends AppCompatActivity implements View.OnDragListener, View.OnLongClickListener {
    public static final String CORRECT_ANSWERS_DRAGDROP = "com.smu.engagingu.CORRECTDRAGDROPANSWERS";
    public static final String QUESTION_COUNT_DRAGDROP = "com.smu.engagingu.DRAGDROPQUESTIONCOUNT";
    public static final String QUESTION_DRAGDROP = "com.smu.engagingu.DRAGDROPQUESTION";


    private static final String TAG = DragDrop.class.getSimpleName();
    private int score;
    private String questionName;
    private Button textView1;
    private Button textView2;
    private Button textView3;
    private Button textView4;
    private Button submit;
    private TextView question;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private HashMap<String,String> optionsMap = new HashMap<>();
    private HashMap<String,Boolean> answersMap = new HashMap<>();
    private HashMap<String,Boolean> areaMap = new HashMap<>();
    private HashMap<String,String> resultsMap = new HashMap<>();
    private String placeName;
    private String[] dragOption = new String[5];
    private String[] dragArea = new String[5];

    private static final String TEXT_VIEW_TAG = "DRAG TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop);
        Intent intent = getIntent();
        placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
        getOptions();
        findViews();
        implementEvents();
    }

    //Find all views and set Tag to all draggable views
    private void getOptions(){
        String word;
        try {
            word = new MyHttpRequestTask().execute("").get();
            JSONArray mainChildNode = new JSONArray(word);
            for(int i =0; i < mainChildNode.length();i++){
                JSONObject firstChildNode = mainChildNode.getJSONObject(i);
                if(firstChildNode.getString("hotspot").equals(placeName)){
                    questionName = firstChildNode.getString("question");
                    JSONArray secondChildNode = firstChildNode.getJSONArray("drag_and_drop");
                    for(int j = 0 ; j<secondChildNode.length();j++){
                        JSONObject option = secondChildNode.getJSONObject(j);
                        dragOption[j]=option.getString("drag_and_drop_answer");
                        dragArea[j]=option.getString("drag_and_drop_question");
                        optionsMap.put(option.getString("drag_and_drop_question"),option.getString("drag_and_drop_answer"));
                    }
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
    private void findViews() {
        textView1 = (Button) findViewById(R.id.label1);
        textView1.setTag(dragOption[0]);
        textView1.setText(dragOption[0]);
        textView2 = (Button) findViewById(R.id.label2);
        textView2.setTag(dragOption[1]);
        textView2.setText(dragOption[1]);
        textView3 = (Button) findViewById(R.id.label3);
        textView3.setTag(dragOption[2]);
        textView3.setText(dragOption[2]);
        textView4 = (Button) findViewById(R.id.label4);
        textView4.setTag(dragOption[3]);
        textView4.setText(dragOption[3]);


        textView6 = (TextView)findViewById(R.id.textView8);
        textView6.setText(dragArea[0]);
        textView7 = (TextView)findViewById(R.id.textView9);
        textView7.setText(dragArea[1]);
        textView8 = (TextView)findViewById(R.id.textView10);
        textView8.setText(dragArea[2]);
        textView9 = (TextView)findViewById(R.id.textView11);
        textView9.setText(dragArea[3]);

        question = (TextView) findViewById(R.id.dragAndDropQuestion);
        question.setText(questionName);

        submit = (Button) findViewById(R.id.button5);
        /*imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setTag(IMAGE_VIEW_TAG);
        button = (Button) findViewById(R.id.button);
        button.setTag(BUTTON_VIEW_TAG);*/
    }


    //Implement long click and drag listener
    private void implementEvents() {
        //add or remove any view that you don't want to be dragged
        textView1.setOnLongClickListener(this);
        textView2.setOnLongClickListener(this);
        textView3.setOnLongClickListener(this);
        textView4.setOnLongClickListener(this);
        /*imageView.setOnLongClickListener(this);
        button.setOnLongClickListener(this);*/

        //add or remove any layout view that you don't want to accept dragged view
        findViewById(R.id.top_layout).setOnDragListener(this);

        findViewById(R.id.right_layout1).setOnDragListener(this);
        findViewById(R.id.right_layout1).setTag(dragArea[0]);

        findViewById(R.id.right_layout2).setOnDragListener(this);
        findViewById(R.id.right_layout2).setTag(dragArea[1]);

        findViewById(R.id.right_layout3).setOnDragListener(this);
        findViewById(R.id.right_layout3).setTag(dragArea[2]);

        findViewById(R.id.right_layout4).setOnDragListener(this);
        findViewById(R.id.right_layout4).setTag(dragArea[3]);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitScore();
            }
        });

    }

    @Override
    public boolean onLongClick(View view) {
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);

        // Instantiates the drag shadow builder.
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

        // Starts the drag
        view.startDrag(data//data to be dragged
                , shadowBuilder //drag shadow
                , view//local data about the drag and drop operation
                , 0//no needed flags
        );

        //Set view visibility to INVISIBLE as we are going to drag the view
        //view.setVisibility(View.INVISIBLE);
        return true;
    }

    // This is the method that the system calls when it dispatches a drag event to the
    // listener.
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        int action = event.getAction();
        // Handles each of the expected events
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // if you want to apply color when drag started to your view you can uncomment below lines
                    // to give any color tint to the View to indicate that it can accept
                    // data.

                    //view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);//set background color to your view

                    view.invalidate();

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                // Applies a YELLOW or any color tint to the View, when the dragged view entered into drag acceptable view
                // Return true; the return value is ignored.

                view.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                // Invalidate the view to force a redraw in the new tint

                // Invalidate the view to force a redraw in the new tint
                view.invalidate();

                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // Re-sets the color tint to blue, if you had set the BLUE color or any color in ACTION_DRAG_STARTED. Returns true; the return value is ignored.

                //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

                //If u had not provided any color in ACTION_DRAG_STARTED then clear color filter.
                String dragAreaName;
                if(view.getTag()!=null) {
                    dragAreaName = view.getTag().toString();
                    System.out.println("here: "+dragAreaName);
                    areaMap.put(dragAreaName, false);
                }
                view.getBackground().clearColorFilter();
                // Invalidate the view to force a redraw in the new tint
                view.invalidate();

                return true;
            case DragEvent.ACTION_DROP:
                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);
                if(view.getTag()!=null){
                    String temp = view.getTag().toString();
                    System.out.println(temp);
                    if(areaMap.get(temp)!=null) {
                        if (areaMap.get(temp) == true) {
                            return false;
                        }
                    }
                }
                // Gets the text data from the item.
                String dragData = item.getText().toString();
                System.out.println(dragData);
                //String dragAreaName="";
                if(view.getTag()!=null) {
                    dragAreaName = view.getTag().toString();
                    System.out.println("There: "+dragAreaName);
                    areaMap.put(dragAreaName,true);
                    if(optionsMap.get(dragAreaName).equals(dragData)){
                        answersMap.put(dragAreaName,true);
                        resultsMap.put(dragAreaName,dragData);
                    }else{
                        answersMap.put(dragAreaName,false);
                        resultsMap.put(dragAreaName,dragData);
                    }
                }
                // Displays a message containing the dragged data.
                //Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_SHORT).show();

                // Turns off any color tints
                view.getBackground().clearColorFilter();

                // Invalidates the view to force a redraw
                view.invalidate();

                View v = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) v.getParent();
                owner.removeView(v);//remove the dragged view
                //if (view.getId().equals(top_layout))
                LinearLayout container = (LinearLayout) view;//caste the view into LinearLayout as our drag acceptable layout is LinearLayout
                container.addView(v);//Add the dragged view
                v.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE

                // Returns true. DragEvent.getResult() will return true.
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                // Turns off any color tinting
                view.getBackground().clearColorFilter();

                // Invalidates the view to force a redraw
                view.invalidate();

                // Does a getResult(), and displays what happened.
                if (event.getResult()) {
                    //Toast.makeText(this, "The drop was handled.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_SHORT).show();


                // returns true; the value is ignored.
                return true;

            // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }
    private void submitScore() {
        String response = null;
        if (InstanceDAO.isLeader) {
            ArrayList<GameResultEntry> resultsList = new ArrayList<>();

            for (String key : answersMap.keySet()) {
                if (answersMap.get(key) == true) {
                    score++;
                }
            }

            for (String key : resultsMap.keySet()) {
                String option = resultsMap.get(key);
                String answer = optionsMap.get(key);
                resultsList.add(new GameResultEntry("1", key, answer, option));
            }
            try {
                response = new MyHttpRequestTask2().execute("http://13.229.115.32:3000/team/updateScore").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (answersMap.size() == 4) {
                Intent intent = new Intent(DragDrop.this, DragDropResults.class);
                intent.putExtra("resultsList", resultsList);
                intent.putExtra(QUESTION_COUNT_DRAGDROP, Integer.toString(optionsMap.size()));
                intent.putExtra(CORRECT_ANSWERS_DRAGDROP, Integer.toString(score));
                intent.putExtra(QUESTION_DRAGDROP, questionName);
                InstanceDAO.completedList.add(placeName);
                startActivity(intent);
            } else {
                Context context = getApplicationContext();
                CharSequence text = "You have not completed the challenge!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }else {
            if (response.equals("fail") || response.equals("")) {
                Toast toast = Toast.makeText(DragDrop.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Intent intent = new Intent(DragDrop.this, HomePage.class);
                InstanceDAO.completedList.add(placeName);
                startActivity(intent);
            }
        }
    }
    private class MyHttpRequestTask2 extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String message = Integer.toString(score);
            System.out.println("Score:"+score);
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("team_id",InstanceDAO.teamID);
            System.out.println("tid: "+InstanceDAO.teamID);
            userHash.put("trail_instance_id",InstanceDAO.trailInstanceID);
            userHash.put("score",message);
            userHash.put("hotspot",placeName);
            System.out.println("message: "+message);
            String response = HttpConnectionUtility.post("http://13.229.115.32:3000/team/updateScore",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://13.229.115.32:3000/draganddrop/getDragAndDrop?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }


}