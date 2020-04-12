package me.tyoj.dcalimi;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SchoolMealViewPagerAdapter extends FragmentStatePagerAdapter {
    private String mSelYear;
    private String mSelMonth;
    private String mSelDate;

    SchoolMealViewPagerAdapter(@NonNull FragmentManager fm, String selYear, String selMonth, String selDate) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
