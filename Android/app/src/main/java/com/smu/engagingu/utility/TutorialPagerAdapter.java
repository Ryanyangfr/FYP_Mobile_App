package com.smu.engagingu.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.smu.engagingu.Tutorial.tutorial1;
import com.smu.engagingu.Tutorial.tutorial2;
import com.smu.engagingu.Tutorial.tutorial3;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public TutorialPagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                tutorial1 tutorial1 = new tutorial1();
                return tutorial1;
            case 1:
                tutorial2 tutorial2 = new tutorial2();
                return tutorial2;
            case 2:
                tutorial3 tutorial3 = new tutorial3();
                return tutorial3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
