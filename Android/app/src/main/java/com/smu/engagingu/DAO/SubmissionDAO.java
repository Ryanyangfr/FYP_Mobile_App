package com.smu.engagingu.DAO;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class SubmissionDAO {

    public static ArrayList<String> HOTSPOTS = new ArrayList<>();
    public static ArrayList<String> QUESTIONS = new ArrayList<>();
    public static ArrayList<String> IMAGEURLS = new ArrayList<>();
    public static ArrayList<String> IMAGEPATHS = new ArrayList<>();
    public static String submissionEndPoint = "http://54.255.245.23:3000/upload/getAllSubmissionURL?team=" + InstanceDAO.teamID + "&trail_instance_id=" + InstanceDAO.trailInstanceID;
    public static String imageEndPoint = "http://54.255.245.23:3000/upload/getSubmission?url=";
}
