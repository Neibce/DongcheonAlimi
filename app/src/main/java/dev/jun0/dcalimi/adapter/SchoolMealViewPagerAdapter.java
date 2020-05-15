package dev.jun0.dcalimi.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import dev.jun0.dcalimi.fragment.SchoolMealFragment;

public class SchoolMealViewPagerAdapter extends FragmentStatePagerAdapter {
    private String mSelYear;
    private String mSelMonth;
    private String mSelDate;

    public SchoolMealViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void setDate(String selYear, String selMonth, String selDate){
        mSelYear = selYear;
        mSelMonth = selMonth;
        mSelDate = selDate;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SchoolMealFragment.newInstance(position, mSelYear, mSelMonth, mSelDate);
    }

    @Override
    public int getCount() {
        return 2;
    }


}
