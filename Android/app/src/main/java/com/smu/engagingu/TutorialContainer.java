package com.smu.engagingu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.smu.engagingu.Tutorial.tutorial1;
import com.smu.engagingu.Tutorial.tutorial2;
import com.smu.engagingu.Tutorial.tutorial3;
import com.smu.engagingu.fyp.R;
import com.smu.engagingu.utility.TutorialPagerAdapter;

public class TutorialContainer extends AppCompatActivity implements tutorial1.OnFragmentInteractionListener,tutorial2.OnFragmentInteractionListener,tutorial3.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_container);

        TabLayout tabLayout = findViewById(R.id.tablayout2);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager2);
        final TutorialPagerAdapter pagerAdapter = new com.smu.engagingu.utility.TutorialPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void start(View view){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
