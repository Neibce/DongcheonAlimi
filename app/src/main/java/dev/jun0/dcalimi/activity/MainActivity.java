package dev.jun0.dcalimi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import dev.jun0.dcalimi.fragment.main.BoardFragment;
import dev.jun0.dcalimi.util.CustomTheme;
import dev.jun0.dcalimi.fragment.main.MainFragment;
import dev.jun0.dcalimi.fragment.main.PreferenceFragment;
import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.fragment.main.SchoolEventFragment;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private Fragment mActiveFragment;

    private MainFragment mMainFragment;
    private BoardFragment mBoardFragment;
    private SchoolEventFragment mSchoolEventFragment;
    private PreferenceFragment mPreferenceFragment;

    private ActionBar mActionBar;
    private float mDensity;
    private int mSelectedItemId;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MA", "onReceive: !!");
            mMainFragment.onDateChanged();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);

        Log.i("MA", "ftUrgsted");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItemId", mSelectedItemId);
        mFragmentManager.putFragment(outState, "mainFragment", mMainFragment);
        mFragmentManager.putFragment(outState, "boardFragment", mBoardFragment);
        mFragmentManager.putFragment(outState, "schoolEventFragment", mSchoolEventFragment);
        mFragmentManager.putFragment(outState, "preferenceFragment", mPreferenceFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CustomTheme(this).setThemeByPreference();

        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(receiver, filter);
        Log.i("MA", "ftrgsted");

        FirebaseInstanceId.getInstance().getInstanceId();

        mActionBar = getSupportActionBar();
        mDensity = getResources().getDisplayMetrics().density;

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        if (savedInstanceState == null) {
            mMainFragment = new MainFragment();
            mBoardFragment = new BoardFragment();
            mSchoolEventFragment = new SchoolEventFragment();
            mPreferenceFragment = new PreferenceFragment(mMainFragment, mSchoolEventFragment);

            fragmentTransaction.add(R.id.frameLayoutMainActivity, mPreferenceFragment, "preference");
            fragmentTransaction.add(R.id.frameLayoutMainActivity, mSchoolEventFragment, "schoolEvent");
            fragmentTransaction.add(R.id.frameLayoutMainActivity, mBoardFragment, "board");
            fragmentTransaction.add(R.id.frameLayoutMainActivity, mMainFragment, "main");

            mSelectedItemId = R.id.main_item;
        }else {
            mMainFragment = (MainFragment) mFragmentManager.getFragment(savedInstanceState, "mainFragment");
            mBoardFragment = (BoardFragment) mFragmentManager.getFragment(savedInstanceState, "boardFragment");
            mSchoolEventFragment = (SchoolEventFragment) mFragmentManager.getFragment(savedInstanceState, "schoolEventFragment");
            mPreferenceFragment = (PreferenceFragment) mFragmentManager.getFragment(savedInstanceState, "preferenceFragment");

            mSelectedItemId = savedInstanceState.getInt("selectedItemId");
        }

        fragmentTransaction.hide(mPreferenceFragment);
        fragmentTransaction.hide(mSchoolEventFragment);
        fragmentTransaction.hide(mBoardFragment);
        fragmentTransaction.hide(mMainFragment);
        fragmentTransaction.commit();

        switch (mSelectedItemId) {
            case R.id.main_item:
                mActiveFragment = mMainFragment;
            case R.id.board_item:
                mActiveFragment = mBoardFragment;
            case R.id.calender_item:
                mActiveFragment = mSchoolEventFragment;
            case R.id.setting_item:
                mActiveFragment = mPreferenceFragment;
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        bottomNavigation.setSelectedItemId(mSelectedItemId);
    }

    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            int menuItemId = menuItem.getItemId();
            mSelectedItemId = menuItem.getItemId();

            switch (menuItemId){
                case R.id.main_item:
                    fragmentTransaction.hide(mActiveFragment).show(mMainFragment).commit();
                    mMainFragment.checkSchoolMealAutoDownload();
                    mActiveFragment = mMainFragment;
                    mActionBar.setElevation(4 * mDensity);
                    break;
                case R.id.board_item:
                    fragmentTransaction.hide(mActiveFragment).show(mBoardFragment).commit();
                    mActiveFragment = mBoardFragment;
                    mActionBar.setElevation(0);
                    break;
                case R.id.calender_item:
                    fragmentTransaction.hide(mActiveFragment).show(mSchoolEventFragment).commit();
                    mSchoolEventFragment.checkSchoolEventAutoDownload();
                    mActiveFragment = mSchoolEventFragment;
                    mActionBar.setElevation(4 * mDensity);
                    break;
                case R.id.setting_item:
                    fragmentTransaction.hide(mActiveFragment).show(mPreferenceFragment).commit();
                    mActiveFragment = mPreferenceFragment;
                    mActionBar.setElevation(4 * mDensity);
                    break;
            }
            return true;
        }
    }
}
