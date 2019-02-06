package com.smu.engagingu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smu.engagingu.GameResultEntry;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class QuizResultAdapter extends ArrayAdapter<GameResultEntry> {
    public QuizResultAdapter(Context context, ArrayList<GameResultEntry> quizResults) {
        super(context,0,quizResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GameResultEntry entry = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.quiz_result_entry, parent, false);
        }
        // Lookup view for data population

        TextView questionName = (TextView) convertView.findViewById(R.id.questionView);
        TextView answerName = (TextView) convertView.findViewById(R.id.answerView);
        TextView userAnswerName = (TextView) convertView.findViewById(R.id.userAnswerView);
        if(entry.getAnswer().equals(entry.getUserAnswer())){
            answerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
            //userAnswerName.setBackgroundColor(Color.parseColor("#E85858"));
            userAnswerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
        }else{
            answerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
            userAnswerName.setTextColor(Color.parseColor("#E85858"));//Red Colour
        }
        userAnswerName.setText("Your Answer: "+entry.getUserAnswer());
        questionName.setText("Q: "+entry.getQuestion());
        answerName.setText("Answer: "+entry.getAnswer());

        // Return the completed view to render on screen
        return convertView;
    }
}
