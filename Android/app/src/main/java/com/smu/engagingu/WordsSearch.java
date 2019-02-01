package com.smu.engagingu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.smu.engagingu.DAO.InstanceDAO;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.HttpConnectionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class WordsSearch extends Activity {

    private int SIZE = 10;
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;
    private final static int DIAGONALLEFTTORIGHT = 2;
    private final static int DIAGONALRIGHTTOLEFT = 3;
    private final static int FORWARD = 0;
    private final static int BACKWARD = 1;
    private int direction;
    private int orientation;
    private EditText e1;
    private EditText e2;
    private EditText e3;
    private EditText e4;
    private EditText e5;
    private Button b;

    private ArrayList<String> answerList = new ArrayList<>();
    private int score;
    private String placeName;
    private int clickCheck = 0;

    private char[][] puzzle;
    String[] wordList = new String[5];
    HashMap<String,ArrayList<PuzzlePoint>> wordPointMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search);
        setupUI(findViewById(R.id.wordSearchMain));
        b = findViewById(R.id.wordSearchSubmit);
        e1 = findViewById(R.id.editText);
        e2 = findViewById(R.id.editText2);
        e3 = findViewById(R.id.editText4);
        e4 = findViewById(R.id.editText5);
        e5 = findViewById(R.id.editText6);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer1 = e1.getText().toString().toUpperCase();
                String answer2 = e2.getText().toString().toUpperCase();
                String answer3 = e3.getText().toString().toUpperCase();
                String answer4 = e4.getText().toString().toUpperCase();
                String answer5 = e5.getText().toString().toUpperCase();
                answerList.add(answer1);
                answerList.add(answer2);
                answerList.add(answer3);
                answerList.add(answer4);
                answerList.add(answer5);
                if (clickCheck==0){
                    createFinishedGrid(puzzle);
                    b.setText("Go to Homepage");
                    clickCheck++;
                }else if (clickCheck==1) {
                    try {
                        String response = new MyHttpRequestTask2().execute("http://54.255.245.23:3000/team/updateScore").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(WordsSearch.this, HomePage.class);
                    InstanceDAO.completedList.add(placeName);
                    startActivity(intent);
                }
            }
        });

        //Populating List of Words from dictionary
        String word;
        try {
            word = new MyHttpRequestTask().execute("").get();
            JSONArray mainChildNode = new JSONArray(word);
            for(int i = 0; i < mainChildNode.length();i++){
                JSONArray firstChildNode = mainChildNode.getJSONArray(i);
                    JSONObject hotspotName = firstChildNode.getJSONObject(0);
                    placeName = hotspotName.getString("hotspot");
                    JSONObject wordsArrayObject = firstChildNode.getJSONObject(1);
                    JSONArray wordsArray = wordsArrayObject.getJSONArray("words");
                    for(int x = 0 ; x < wordsArray.length();x++){
                        System.out.println(x+" "+wordsArray.getString(x));
                        wordList[x] = wordsArray.getString(x);
                    }

                }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //initializing the puzzle
        puzzle = new char[SIZE][SIZE];
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++){
                puzzle[i][j] = ' ';
            }
        }
        int maxCharacters = 56;
        int characterCount = 0;

        //adding words to the puzzle
        for(int i = 0; i < wordList.length; i++){
            addWord(wordList[i], puzzle);
            //System.out.println(wordList[i] + " successfully added!");
            characterCount += wordList[i].length();
            if(characterCount > maxCharacters)
                break;
        }

        //printing the puzzle
        /*System.out.println("PUZZLE\n");
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++){
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.print("\n");
        }*/

        //filling empty spaces
        puzzle = fill(puzzle);
        //System.out.println("\n");

        //printing the complete puzzle
        /*
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++){
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.print("\n");
        }
        //creating the grid
        */
        createGrid(puzzle);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(WordsSearch.this);
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
    //creating the user layout
    public void createGrid(char[][] input){
        TableLayout table = (TableLayout) findViewById(R.id.mainLayout);

        for(int i = 0; i < input.length; i++){
            //LinearLayout rowLayout = new LinearLayout(this);
            //rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            //rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);

            for(int j = 0; j < input.length; j++){
                TextView text = new TextView(this);
                Character temp = input[i][j];
                text.setText(temp.toString());
                text.setPadding(10, 5, 10, 5);
                text.setTextSize(25);
                text.setGravity(Gravity.CENTER);
                row.addView(text);
            }
            table.addView(row);
        }
    }
    public void createFinishedGrid(char[][] input){
        TableLayout table = (TableLayout) findViewById(R.id.mainLayout);
        int count = table.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = table.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        for(int i = 0; i < input.length; i++){
            //LinearLayout rowLayout = new LinearLayout(this);
            //rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            //rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);

            for(int j = 0; j < input.length; j++){
                TextView text = new TextView(this);
                Character temp = input[i][j];
                text.setText(temp.toString());
                Boolean b = false;
                Boolean b2 = false;
                for(String s: wordPointMap.keySet()){
                    ArrayList<PuzzlePoint> tempList = wordPointMap.get(s);
                    for(PuzzlePoint p : tempList) {
                        if (p.getRow() == i && p.getColumn() == j) {
                            b = true;
                            for(String s2: answerList){
                                if (s2.equals(s)){
                                    b2=true;
                                }
                            }
                        }
                    }
                }
                if(b){
                    text.setBackgroundColor(Color.parseColor("#E85858"));
                }
                if(b2){
                    text.setBackgroundColor(Color.parseColor("#92d050"));
                    score++;//SCORE FOR CHALLENGE
                }
                text.setPadding(10, 5, 10, 5);
                text.setTextSize(25);
                text.setGravity(Gravity.CENTER);
                row.addView(text);
            }
            table.addView(row);
        }
    }

    public void addWord(String word, char puzzle[][]){
        word = word.toUpperCase(Locale.US);
        String originalWord = word;
        Random random = new Random();
        int row = 0;
        int col = 0;
        char originalPuzzle[][] = new char[puzzle.length][puzzle.length];

        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++){
                originalPuzzle[i][j] = puzzle[i][j];
            }
        }

        int flag = 0;
        for(int tries = 1; tries <= 20; tries++){
            //deciding the orientation of the word
            orientation = random.nextInt(2);
            //deciding the direction of the word
            direction = random.nextInt(2);

            if(orientation == BACKWARD){
                word = flipWord(word);
            }
            if(direction == HORIZONTAL){
                row = random.nextInt(puzzle.length);
                col = random.nextInt(puzzle.length - word.length());
            }
            else if(direction == VERTICAL){
                row = random.nextInt(puzzle.length - word.length());
                col = random.nextInt(puzzle.length);
            }/*
            else if(direction == DIAGONALLEFTTORIGHT) {
                row = random.nextInt(puzzle.length - word.length());
                col = random.nextInt(puzzle.length - word.length());
            }
			else if(direction == DIAGONALRIGHTTOLEFT) {
				row = random.nextInt(puzzle.length - word.length());
				col = random.nextInt(puzzle.length - (puzzle.length - word.length()));
			}*/
            ArrayList<PuzzlePoint> wordPoints = new ArrayList<>();
            for(int i = 0; i < word.length(); i++){
                if(puzzle[row][col] == ' ' || puzzle[row][col] == word.charAt(i)){
                    wordPoints.add(new PuzzlePoint(row,col));
                    puzzle[row][col] = word.charAt(i);
                    flag++;
                    if(direction == HORIZONTAL)		col++;
                    if(direction == VERTICAL)		row++;
                    //if(direction == DIAGONALLEFTTORIGHT) { col++; row++; }
                    //if(direction == DIAGONALLEFTTORIGHT) { col--; row++; }
                }
                else {
                    for(int j = i; j >= 0; j--){
                        puzzle[row][col] = originalPuzzle[row][col];
                        wordPoints = new ArrayList<>();
                        if(direction == HORIZONTAL)		col--;
                        if(direction == VERTICAL)		row--;
                       // if(direction == DIAGONALLEFTTORIGHT) {		col--; row--; }
                        //if(direction == DIAGONALRIGHTTOLEFT) {	col++; row--; }
                    }
                    flag = 0;
                    break;
                }
            }
            wordPointMap.put(originalWord,wordPoints);
            if(flag == word.length())
                break;
        }
    }

    public String flipWord(String input) {
        StringBuilder output = new StringBuilder();
        for(int i = input.length()-1; i >= 0; i--){
            output.append(input.charAt(i));
        }
        return output.toString();
    }

    public char[][] fill(char[][] input){
        char output[][] = new char[input.length][input.length];
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();

        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++){
                if(puzzle[i][j] == ' ') {
                    output[i][j] = characters.charAt(random.nextInt(characters.length()));
                }
                else {
                    output[i][j] = puzzle[i][j];
                }
            }
        }
        return output;
    }
    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> req = new HashMap<>();
            String response = HttpConnectionUtility.get("http://54.255.245.23:3000/wordsearch/getWordSearchWords?trail_instance_id=175239");
            if (response == null){
                return null;
            }
            return response;
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
            String response = HttpConnectionUtility.post("http://54.255.245.23:3000/team/updateScore",userHash);
            if (response == null){
                return null;
            }
            return response;
        }
    }
}
