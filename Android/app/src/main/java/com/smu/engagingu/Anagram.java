package com.smu.engagingu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.Utilities.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
/*
 * Anagram refers to the page used to display the Anagram game mode
 */
public class Anagram extends AppCompatActivity implements View.OnClickListener{

        private TextView wordTv;
        private EditText wordEnteredTv;
        private Button validate, newGame;
        private String wordToFind;
        private String placeName;
        private final Random RANDOM = new Random();

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_anagram);
            setupUI(findViewById(R.id.anagramMain));
            Intent intent = getIntent();
            placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
            wordTv = (TextView) findViewById(R.id.wordTv);
            wordEnteredTv = (EditText) findViewById(R.id.wordEnteredTv);
            wordEnteredTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                /*
                 * real-time update of the current anagram by cancelling away the letters
                 * if character that user inputs corresponds with letters in the shuffled word
                 */
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String temp = wordToFind;
                    String sequence=charSequence.toString().toUpperCase();
                    for(int k =0 ; k < sequence.length();k++){
                        temp=temp.replaceFirst(Pattern.quote(new StringBuilder().append("").append(sequence.charAt(k)).toString()),"");
                    }
                    wordTv.setText(shuffleWord(temp));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            validate = (Button) findViewById(R.id.validate);
            if(!InstanceDAO.isLeader){
                validate.setText("EXIT");
            }
            validate.setOnClickListener(this);
            newGame = (Button) findViewById(R.id.newGame);
            newGame.setOnClickListener(this);

            newGame();
        }

        @Override
        public void onClick (View view){
            if (view == validate) {
                if(InstanceDAO.isLeader) {
                    validate();
                }else{
                    Intent intent = new Intent(this, HomePage.class);
                    InstanceDAO.completedList.add(placeName);
                    startActivity(intent);
                }
            } else if (view == newGame) {
                newGame();
            }
        }
    /*
     * used to check if the user's input is correct by referring to the answer
     * obtained from the anagram end-point
     */
        private void validate () {
            String response = null;
            String w = wordEnteredTv.getText().toString();
            if (wordToFind.equals(w.toUpperCase())) {
                Toast.makeText(this, "Congratulations ! You found the word " + wordToFind, Toast.LENGTH_SHORT).show();
                try {
                    response = new MyHttpRequestTask2().execute("https://amazingtrail.ml/api/team/updateScore").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(response.equals("fail")|| response.equals("")){
                    Toast toast = Toast.makeText(Anagram.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Intent intent = new Intent(this, HomePage.class);
                    InstanceDAO.completedList.add(placeName);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Wrong Word! Please Retry", Toast.LENGTH_SHORT).show();
            }
        }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Anagram.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    /*
     * used to initialise a new anagram game
     */
        private void newGame () {
            String word;
            try {
                word = new MyHttpRequestTask().execute("").get();
                if(word.equals("fail")|| word.equals("")){
                    Toast toast = Toast.makeText(Anagram.this, "Bad Internet Connection, Try Again Later!", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    JSONArray jsonMainNode = new JSONArray(word);
                    for (int i = 0; i < jsonMainNode.length(); i++) {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        if (jsonChildNode.getString("hotspot").equals(placeName)) {
                            wordToFind = jsonChildNode.getString("anagram").toUpperCase();
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
            String wordShuffled = shuffleWord(wordToFind);
            wordTv.setText(wordShuffled);
            wordEnteredTv.setText("");
        }
    /*
     * method to randomly shuffle the character sequence in the word
     */
        private String shuffleWord(String word){
            if (word != null  &&  !"".equals(word)) {
                char a[] = word.toCharArray();

                for (int i = 0; i < a.length; i++) {
                    int j = RANDOM.nextInt(a.length);
                    char tmp = a[i];
                    a[i] = a[j];
                    a[j] = tmp;
                }

                return new String(a);
            }

            return word;
        }
    /*
     * obtain question name and word from endpoint
     */
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("https://amazingtrail.ml/api/anagram/getAnagrams?trail_instance_id="+InstanceDAO.trailInstanceID);
            if (response == null){
                return null;
            }
            return response;
        }
    }
    /*
     * post score obtained from this game mode to the database
     */
    private class MyHttpRequestTask2 extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String message = Integer.toString(1);
            HashMap<String,String> userHash = new HashMap<>();
            userHash.put("team_id",InstanceDAO.teamID);
            userHash.put("trail_instance_id",InstanceDAO.trailInstanceID);
            userHash.put("score",message);
            userHash.put("hotspot",placeName);
            String response = HttpConnectionUtility.post("https://amazingtrail.ml/api/team/updateScore",userHash);
            return response;
        }
    }
}

