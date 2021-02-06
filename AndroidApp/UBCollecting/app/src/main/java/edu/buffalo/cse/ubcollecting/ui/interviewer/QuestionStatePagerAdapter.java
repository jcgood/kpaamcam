package edu.buffalo.cse.ubcollecting.ui.interviewer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * An adapter that manages all the question fragments for a questionnaire.
 */

public class QuestionStatePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentList;

    public QuestionStatePagerAdapter(FragmentManager fm){
        super(fm);
        fragmentList = new ArrayList<>();
    }

    public void addFragement(Fragment fragment){
        fragmentList.add(fragment);
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
