package dev.jun0.dcalimi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.firebase.iid.FirebaseInstanceId;

import dev.jun0.dcalimi.fragment.main.BoardFragment;
import dev.jun0.dcalimi.util.CustomTheme;
import dev.jun0.dcalimi.fragment.main.MainFragment;
import dev.jun0.dcalimi.fragment.main.PreferenceFragment;
import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.fragment.main.SchoolEventFragment;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mActiveFragment;

    private MainFragment mMainFragment = new MainFragment();
    private BoardFragment mBoardFragment = new BoardFragment();
    private SchoolEventFragment mSchoolEventFragment = new SchoolEventFragment();
    private PreferenceFragment mPreferenceFragment = new PreferenceFragment(mMainFragment, mSchoolEventFragment);

    private ActionBar mActionBar;
    private float density;

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
        density = getResources().getDisplayMetrics().density;

        mFragmentManager = getSupportFragmentManager();
        mActiveFragment = mMainFragment;
        mFragmentManager.beginTransaction().add(R.id.frameLayoutMain, mPreferenceFragment, "preference").hide(mPreferenceFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayoutMain, mSchoolEventFragment, "schoolEvent").hide(mSchoolEventFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayoutMain, mBoardFragment, "notice").hide(mBoardFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayoutMain, mMainFragment, "main").hide(mMainFragment).commit();

        int selectedItemId = R.id.main_item;
        Bundle intentExtras = getIntent().getExtras();
        if(intentExtras != null)
            selectedItemId = intentExtras.getInt("selectedItemId", R.id.main_item);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        bottomNavigation.setSelectedItemId(selectedItemId);
    }

    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            mFragmentTransaction = mFragmentManager.beginTransaction();

            switch (menuItem.getItemId()){
                case R.id.main_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mMainFragment).commit();
                    mMainFragment.checkSchoolMealAutoDownload();
                    mActiveFragment = mMainFragment;
                    mActionBar.setElevation(4 * density);
                    break;
                case R.id.notice_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mBoardFragment).commit();
                    mActiveFragment = mBoardFragment;
                    mActionBar.setElevation(0);
                    break;
                case R.id.calender_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mSchoolEventFragment).commit();
                    mSchoolEventFragment.checkSchoolEventAutoDownload();
                    mActiveFragment = mSchoolEventFragment;
                    mActionBar.setElevation(4 * density);
                    break;
                case R.id.setting_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mPreferenceFragment).commit();
                    mActiveFragment = mPreferenceFragment;
                    mActionBar.setElevation(4 * density);
                    break;
            }
            return true;
        }
    }
}
