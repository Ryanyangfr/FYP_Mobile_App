package com.smu.engagingu.DAO;

import com.smu.engagingu.Hotspot.Hotspot;

import java.util.ArrayList;

public class InstanceDAO {
    public static String teamID;
    public static String trailInstanceID;
    public static ArrayList<String> completedList = new ArrayList<>();
    public static Boolean firstTime = true;
    public static Boolean isLeader = false;
    public static ArrayList<Hotspot> hotspotList = new ArrayList<>();
    public static Hotspot startingHotspot;



}
