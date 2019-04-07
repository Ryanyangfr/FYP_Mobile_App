package com.smu.engagingu.DAO;

import java.util.ArrayList;
/*
 * SubmissionDAO stores all the links and arraylists used for
 * submissions downloading and uploading
 */
public class SubmissionDAO {

    public static ArrayList<String> HOTSPOTS = new ArrayList<>();
    public static ArrayList<String> QUESTIONS = new ArrayList<>();
    public static ArrayList<String> IMAGEURLS = new ArrayList<>();
    public static ArrayList<String> IMAGEPATHS = new ArrayList<>();
    public static String submissionEndPoint = "https://amazingtrail.ml/api/upload/getAllSubmissionURL?team=" + InstanceDAO.teamID + "&trail_instance_id=" + InstanceDAO.trailInstanceID;
    public static String imageEndPoint = "https://amazingtrail.ml/api/upload/getSubmission?url=";
}
