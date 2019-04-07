package com.smu.engagingu.Objects;
/*
 * Event is the object used to store all the information pertaining to each
 * activity feed entry
 */
public class Event  {
    private String name;
    private String id;
    private String data;
    private String time;

    public Event(String name, String eventId, String data, String time) {
        this.name = name;
        this.id = eventId;
        this.data = data;
        this.time = time;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getTime(){return time;}
}