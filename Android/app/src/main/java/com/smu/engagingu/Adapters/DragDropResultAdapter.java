//Adapter to display drag and drop results
package com.smu.engagingu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smu.engagingu.Objects.GameResultEntry;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;
import java.util.Objects;
/*
 * DragDropResultAdapter is intended to be used to convert drag and drop
 * results into a listview to be displayed on the results page
 * The data displayed are actual answer, user's answers and the
 * score obtained by the user
 */

public class DragDropResultAdapter extends ArrayAdapter<GameResultEntry> {
    public DragDropResultAdapter(Context context, ArrayList<GameResultEntry> dragDropResults) {
        super(context,0,dragDropResults);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
        if(Objects.requireNonNull(entry).getAnswer().equals(entry.getUserAnswer())){
            answerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
            userAnswerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
        }else{
            answerName.setTextColor(Color.parseColor("#92d050"));//Green Colour
            userAnswerName.setTextColor(Color.parseColor("#E85858"));//Red Colour
        }
        userAnswerName.setText("Your Answer: "+entry.getUserAnswer());
        questionName.setText("Option: "+entry.getQuestion());
        answerName.setText("Answer: "+entry.getAnswer());

        // Return the completed view to render on screen
        return convertView;
    }
}
