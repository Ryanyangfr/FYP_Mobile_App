package com.smu.engagingu.DAO;

import com.smu.engagingu.Adapters.EventAdapter;
import com.smu.engagingu.Objects.Hotspot;

import java.util.ArrayList;
import java.util.HashMap;
/*
 * InstanceDAO is used to store the variables used for the current
 * instance of the amazing race trail.
 * Variables are also constantly stored in a session to be used for session storing
 */
public class InstanceDAO {
    public static String teamID;
    public static String trailInstanceID;
    public static ArrayList<String> completedList = new ArrayList<>();
    public static Boolean firstTime = true;
    public static Boolean isLeader = false;
    public static Boolean startTrail = false;
    public static ArrayList<Hotspot> hotspotList = new ArrayList<>();
    public static Hotspot startingHotspot;
    public static Boolean hasPulled = false;
    public static final String LOGGED_IN_PREF = "logged_in_status";
    public static String userName= "";
    public static ArrayList<String> userList = new ArrayList<>();
    public static EventAdapter adapter=null;
    public static HashMap<String, String> questionTypeMap = new HashMap<>();
}
