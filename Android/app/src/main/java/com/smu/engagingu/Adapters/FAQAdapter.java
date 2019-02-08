package com.smu.engagingu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smu.engagingu.Objects.FaqEntry;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;

public class FAQAdapter extends ArrayAdapter<FaqEntry> {
    public FAQAdapter(Context context, ArrayList<FaqEntry> faqResults) {
        super(context,0,faqResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FaqEntry entry = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.faq_entry, parent, false);
        }
        // Lookup view for data population

        TextView questionName = (TextView) convertView.findViewById(R.id.questionView);
        TextView answerName = (TextView) convertView.findViewById(R.id.answerView);

        questionName.setText((position+1)+" . "+entry.getQuestion());
        answerName.setText("Answer: "+entry.getAnswer());

        // Return the completed view to render on screen
        return convertView;
    }
}
