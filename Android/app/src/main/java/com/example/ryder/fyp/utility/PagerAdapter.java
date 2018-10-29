package com.example.ryder.fyp.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ryder.fyp.StoryLine.StoryLine1;
import com.example.ryder.fyp.StoryLine.StoryLine2;
import com.example.ryder.fyp.StoryLine.StoryLine3;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                StoryLine1 storyLine1 = new StoryLine1();
                return storyLine1;
            case 1:
                StoryLine2 storyLine2 = new StoryLine2();
                return storyLine2;
            case 2:
                StoryLine3 storyLine3 = new StoryLine3();
                return storyLine3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
