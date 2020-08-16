package dev.jun0.dcalimi.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialContainerTransform;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.activity.MainActivity;
import dev.jun0.dcalimi.adapter.BoardViewPagerAdapter;
import dev.jun0.dcalimi.fragment.board.NoticeFragment;
import dev.jun0.dcalimi.item.PostItem;

public class BoardFragment extends Fragment {
    private static final int REQUEST_CODE = 0;

    private ActionBar mActionBar;
    private FragmentManager mFragmentManager;
    private float mDestiny;

    private String mLastActionBarTitle;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("LastActionBarTitle", mLastActionBarTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentManager = getChildFragmentManager();
        if (savedInstanceState != null) {
            mLastActionBarTitle = savedInstanceState.getString("LastActionBarTitle");
        }

        View view = inflater.inflate(R.layout.fragment_board, container, false);

        final ViewPager viewpager = view.findViewById(R.id.viewPager);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        mActionBar = ((MainActivity)requireActivity()).getSupportActionBar();
        mDestiny = getResources().getDisplayMetrics().density;

        BoardViewPagerAdapter boardViewPagerAdapter = new BoardViewPagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(boardViewPagerAdapter);

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = mFragmentManager.getBackStackEntryCount();
                if(count == 0) {
                    mActionBar.setDisplayHomeAsUpEnabled(false);
                    mActionBar.setElevation(0);
                    mActionBar.setTitle(R.string.app_name);
                }
            }
        });

        setActionBar();

        return view;
    }

    public void showFragmentWithTransition(Fragment current, Fragment newFragment, String tag, View sharedView, PostItem postItem, int position) {
        Bundle bundle = new Bundle();

        bundle.putString("TRANS_CARD_NAME", "transCard" + position);

        bundle.putInt("POST_ID", postItem.getId());
        bundle.putString("TITLE", postItem.getTitle());
        bundle.putString("UPLOADER", postItem.getUploader());
        bundle.putString("UPLOAD_DATE", postItem.getDate());

        newFragment.setArguments(bundle);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        current.setExitTransition(new Hold());

        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setContainerColor(MaterialColors.getColor(sharedView, R.attr.colorSurface));
        transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        transform.setScrimColor(0);
        newFragment.setSharedElementEnterTransition(transform);

        transaction.addSharedElement(sharedView.findViewById(R.id.materialCardView), "transCard" + position);

        transaction.setCustomAnimations(
                R.anim.abc_grow_fade_in_from_bottom,
                R.anim.abc_fade_out,
                R.anim.abc_fade_in,
                R.anim.abc_shrink_fade_out_from_bottom);

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setElevation(4 * mDestiny);

        if(current instanceof NoticeFragment)
            mLastActionBarTitle = "공지사항";
        else
            mLastActionBarTitle = "건의사항";
        mActionBar.setTitle(mLastActionBarTitle);

        setHasOptionsMenu(true);

        newFragment.setTargetFragment(current, REQUEST_CODE);
        transaction
                .replace(R.id.frameLayoutBoard, newFragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void setActionBar(){
        if(mFragmentManager == null){
            mFragmentManager = getChildFragmentManager();
        }
        if(mActionBar == null){
            mActionBar = ((MainActivity)requireActivity()).getSupportActionBar();
        }

        if(mFragmentManager.getBackStackEntryCount() > 0){
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setElevation(4 * mDestiny);

            mActionBar.setTitle(mLastActionBarTitle);
            setHasOptionsMenu(true);
        }else {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setElevation(0);
            mActionBar.setTitle(R.string.app_name);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(mFragmentManager != null) {
                mFragmentManager.popBackStack();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
