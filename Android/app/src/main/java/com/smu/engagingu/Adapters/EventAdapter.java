package com.smu.engagingu.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.smu.engagingu.Objects.Event;
import com.smu.engagingu.fyp.R;

import java.util.List;
/*
 * EventAdapter is intended to be used to convert activity feed JSON Data
 * into viewable card views.
 * The data displayed are event name, event ID, timestamp of activity occurrence
 * and the full details of the activity
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> items;

    public EventAdapter(List<Event> items) {
        this.items = items;
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        public TextView event;
        public TextView id;
        public RelativeTimeTextView timestamp;
        public TextView data;

        public EventViewHolder(View v) {
            super(v);
            event = (TextView) v.findViewById(R.id.event);
            id = (TextView) v.findViewById(R.id.id);
            timestamp = (RelativeTimeTextView) v.findViewById(R.id.timestamp);
            data = (TextView) v.findViewById(R.id.data);
        }
    }
    //add event entry into arraylist to be displayed
    public void addEvent(Event event) {
        // Add the event at the beginning of the list
        items.add(0, event);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int i) {
        Event event = items.get(i);
        viewHolder.event.setText(event.getName());
        viewHolder.timestamp.setText(event.getTime());
    }
}