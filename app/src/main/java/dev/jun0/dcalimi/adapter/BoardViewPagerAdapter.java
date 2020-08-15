package dev.jun0.dcalimi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import dev.jun0.dcalimi.fragment.board.NoticeFragment;
import dev.jun0.dcalimi.fragment.board.SuggestionFragment;

public class BoardViewPagerAdapter extends FragmentStatePagerAdapter {

    public BoardViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return SuggestionFragment.newInstance();
            case 0:
            default:
                return NoticeFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}