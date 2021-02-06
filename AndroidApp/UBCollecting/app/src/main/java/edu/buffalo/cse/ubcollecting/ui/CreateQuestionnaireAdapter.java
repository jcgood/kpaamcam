package edu.buffalo.cse.ubcollecting.ui;

//import androidx.core.app.Fragment;
//import androidx.core.app.FragmentManager;
//import androidx.core.app.FragmentPagerAdapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
