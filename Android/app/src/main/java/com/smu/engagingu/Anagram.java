package com.smu.engagingu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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
            Intent intent = getIntent();
            placeName = intent.getStringExtra(Narrative.EXTRA_MESSAGE2);
            wordTv = (TextView) findViewById(R.id.wordTv);
            wordEnteredTv = (EditText) findViewById(R.id.wordEnteredTv);
            validate = (Button) findViewById(R.id.validate);
            validate.setOnClickListener(this);
            newGame = (Button) findViewById(R.id.newGame);
            newGame.setOnClickListener(this);

            newGame();
        }

        @Override
        public void onClick (View view){
            if (view == validate) {
                validate();
            } else if (view == newGame) {
                newGame();
            }
        }

        private void validate () {
            String w = wordEnteredTv.getText().toString();

            if (wordToFind.equals(w)) {
                Toast.makeText(this, "Congratulations ! You found the word " + wordToFind, Toast.LENGTH_SHORT).show();
                try {
                    String response = new MyHttpRequestTask().execute("http://54.255.245.23:3000/team/updateScore").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, HomePage.class);
                InstanceDAO.completedList.add(placeName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Retry !", Toast.LENGTH_SHORT).show();
            }
        }

        private void newGame () {
            String word;
            try {
                word = new MyHttpRequestTask().execute("").get();
                JSONArray jsonMainNode = new JSONArray(word);
                for (int i = 0 ; i < jsonMainNode.length();i++){
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    if(jsonChildNode.getString("hotspot").equals(placeName)) {
                        wordToFind = jsonChildNode.getString("anagram");
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
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/anagram/getAnagrams?trail_instance_id=175239");
            if (response == null){
                return null;
            }
            return response;
        }
    }
}

