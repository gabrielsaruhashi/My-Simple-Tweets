package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by gabesaruhashi on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabsTitles[] = new String[] {"Home", "Mentions"};
    private Context context;

    private HomeTimelineFragment timelinefragment;
    private MentionsTimelineFragment mentionsfragment;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    // return the total # of fragment
    @Override
    public int getCount() {
        return 2;
    }


    // return the fragment to use depending on the position

    @Override
    public Fragment getItem(int position) {
        if  (position == 0) {
            timelinefragment = getTimelineInstance();
            return timelinefragment;
        } else if (position == 1) {
            mentionsfragment = getMentionsInstance();
            return mentionsfragment;
        } else {
            return null;
        }
    }

    // return title
    @Override
    public CharSequence getPageTitle(int position) {

        // generate title based on item postion
        return tabsTitles[position];
    }

    private HomeTimelineFragment getTimelineInstance() {
        if (timelinefragment == null) {
            timelinefragment = new HomeTimelineFragment();
        }
        return timelinefragment;
    }

    private MentionsTimelineFragment getMentionsInstance() {
        if (mentionsfragment == null) {
            mentionsfragment = new MentionsTimelineFragment();
        }
        return mentionsfragment;
    }

}
