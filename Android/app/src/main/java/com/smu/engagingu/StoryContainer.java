    package com.smu.engagingu;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.support.design.widget.TabLayout;
    import android.support.v4.view.PagerAdapter;
    import android.support.v4.view.ViewPager;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;

    import com.smu.engagingu.StoryLine.StoryLine1;
    import com.smu.engagingu.StoryLine.StoryLine2;
    import com.smu.engagingu.StoryLine.StoryLine3;
    import com.smu.engagingu.fyp.R;

    public class StoryContainer extends AppCompatActivity implements StoryLine1.OnFragmentInteractionListener,StoryLine2.OnFragmentInteractionListener,StoryLine3.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_container);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new com.smu.engagingu.utility.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
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
    public void startTrail(View view){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
        @Override
        public void onFragmentInteraction(Uri uri) {

        }
    }
