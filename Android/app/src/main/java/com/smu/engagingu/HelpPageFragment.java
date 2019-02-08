package com.smu.engagingu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smu.engagingu.Adapters.FAQAdapter;
import com.smu.engagingu.Objects.FaqEntry;
import com.smu.engagingu.fyp.R;

import java.util.ArrayList;


public class HelpPageFragment extends Fragment {
   @Nullable
   @Override

    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
       View v = inflater.inflate(R.layout.fragment_helppage, container, false);
       ArrayList<FaqEntry> faqList = new ArrayList<>();
       faqList.add(new FaqEntry("Which hotspot should I go?","After you have completed the first hotspot, all the hotspots will be shown. It is up to your group to plan the most efficient way of visiting all the hotspot in the given time."));
       faqList.add(new FaqEntry("I have completed all hotspots. What’s next? ","Head back to the Admin building. The team who have the shortest timing may have an advantage"));
       faqList.add(new FaqEntry("The Mission questions are not found in the narrative. How?","Narrative are meant to provide background information regarding the school.\n" +
               "Mission questions are meant for you to explore the faculty to search for the answers within the hotspot."));
       faqList.add(new FaqEntry("How do I know if my team is leading?","Check the leaderboard for number of completed hotspots by each team"));
       faqList.add(new FaqEntry("What is activity feed for?","It is to give you real time updates each time a team has completed a hotspot"));
       faqList.add(new FaqEntry("I am a member, I want to download the wefie taken by my leader.","Yes, you can do so by clicking on submissions. Choose which picture you would like to download."));
       faqList.add(new FaqEntry("Why am I unable to submit?","Each group will have a randomised appointed leader. Only the leader from each team are allowed to submit all responses. This is to prevent double counting of points."));
       ListView lv = v.findViewById(R.id.faqList);
       FAQAdapter faqAdapter = new FAQAdapter(getContext(),faqList);
       lv.setAdapter(faqAdapter);

       return v;
   }
}
