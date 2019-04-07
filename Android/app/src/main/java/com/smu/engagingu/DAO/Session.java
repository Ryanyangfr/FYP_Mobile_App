package com.smu.engagingu.DAO;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.smu.engagingu.Adapters.EventAdapter;

import static com.smu.engagingu.DAO.InstanceDAO.LOGGED_IN_PREF;
/*
 * SharedPreferences is used to store information and details in regards to the user's session.
 * SharedPreferences persist even after user closes the application. Only integers and strings
 * are allowed to be saved.
 */
public class Session {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }


    public static void setTeamID(Context context,String teamID) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("teamID", teamID).commit();
        editor.apply();
    }

    public static String getTeamID(Context context) {
        return getPreferences(context).getString("teamID","");
    }

    public static void setIsLeader(Context context, Boolean b){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean("isLeader",b).commit();
        editor.apply();
    }
    public static Boolean getIsLeader (Context context) {
        return getPreferences(context).getBoolean("isLeader",true);
    }

    public static void setEventAdapter(Context context, EventAdapter eventAdapter) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(eventAdapter);
        editor.putString("eventAdapter", json).commit();
        editor.apply();
    }

    public static EventAdapter getEventAdapter(Context context) {
        Gson gson = new Gson();
        String json = getPreferences(context).getString("eventAdapter","");
        return gson.fromJson(json, EventAdapter.class);
    }

    public static void setTrailInstanceID(Context context,String trailInstanceID) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("trailInstanceID", trailInstanceID).commit();
        editor.apply();
    }

    public static String getTrailInstanceID(Context context) {
        return getPreferences(context).getString("trailInstanceID","");
    }

    public static void setFirstTime(Context context,Boolean firstTime) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean("firstTime",firstTime).commit();
        editor.apply();
    }

    public static Boolean getFirstTime(Context context) {
        return getPreferences(context).getBoolean("firstTime",true);
    }
    public static void setUserName(Context context,String userName) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("userName",userName).commit();
        editor.apply();
    }

    public static String getUsername(Context context) {
        return getPreferences(context).getString("userName","");
    }

}

