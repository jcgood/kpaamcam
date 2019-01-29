package edu.buffalo.cse.ubcollecting.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class CreateQuestionnaireAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList;
    private final List<String>   fragmentTitleList;


    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }
    public CreateQuestionnaireAdapter(FragmentManager fm){
        super(fm);
        fragmentList = new ArrayList<Fragment>();
        fragmentTitleList = new ArrayList<String>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
