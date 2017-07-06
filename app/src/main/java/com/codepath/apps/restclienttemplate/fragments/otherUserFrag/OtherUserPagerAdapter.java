package com.codepath.apps.restclienttemplate.fragments.otherUserFrag;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;

/**
 * Created by gabesaruhashi on 7/5/17.
 */

public class OtherUserPagerAdapter extends FragmentPagerAdapter {

    private String tabsTitles[] = new String[] {"Tweets", "Followers", "Follwing"};
    private Context context;
    private String screeName;
    private long uid;

    public OtherUserPagerAdapter(FragmentManager fm, String name, long uid, Context context) {
        super(fm);
        this.context = context;
        this.screeName = name;
        this.uid = uid;
    }

    // return the total # of fragment
    @Override
    public int getCount() {
        return 3;
    }


    // return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {
        if  (position == 0) {
            return UserTimelineFragment.newInstance(screeName);

        } else if (position == 1) {
            return FollowersListFragment.newInstance(uid);
        } else if (position == 2) {
            return FollowingListFragment.newInstance(uid);
        }
        else {
            return null;
        }
    }

    // return title
    @Override
    public CharSequence getPageTitle(int position) {
        // generate title based on item postion
        return tabsTitles[position];
    }


}
