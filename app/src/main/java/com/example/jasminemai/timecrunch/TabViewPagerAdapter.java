package com.example.jasminemai.timecrunch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;


import java.util.ArrayList;

/**
 * Created by michelle on 11/15/17.
 */

public class TabViewPagerAdapter extends FragmentPagerAdapter {

/**
 * Adopted from Varun's class example
 */
    private ArrayList<Fragment> fragments;

    public TabViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;

    }



    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "todo";
            case 1:
                return "settings";
            default:
                break;
        }
        return null;

    }
}

