package com.smu.engagingu.DAO;

import java.util.ArrayList;

public class SubmissionDAO {

    public static ArrayList<String> HOTSPOTS = new ArrayList<>();
    public static ArrayList<String> QUESTIONS = new ArrayList<>();
    public static ArrayList<String> IMAGEURLS = new ArrayList<>();
    public static ArrayList<String> IMAGEPATHS = new ArrayList<>();
    public static String submissionEndPoint = "http://13.229.115.32:3000/upload/getAllSubmissionURL?team=" + InstanceDAO.teamID + "&trail_instance_id=" + InstanceDAO.trailInstanceID;
    public static String imageEndPoint = "http://13.229.115.32:3000/upload/getSubmission?url=";
}
