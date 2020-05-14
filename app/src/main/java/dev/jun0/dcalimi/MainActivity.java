package dev.jun0.dcalimi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mActiveFragment;

    private MainFragment mMainFragment = new MainFragment();
    private BoardFragment mNoticeFragment = new BoardFragment();
    private SchoolEventFragment mSchoolEventFragment = new SchoolEventFragment();
    private PreferenceFragment mPreferenceFragment = new PreferenceFragment(mMainFragment, mSchoolEventFragment);

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

        mFragmentManager = getSupportFragmentManager();
        mActiveFragment = mMainFragment;
        mFragmentManager.beginTransaction().add(R.id.frameLayout, mPreferenceFragment, "preference").hide(mPreferenceFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayout, mSchoolEventFragment, "schoolEvent").hide(mSchoolEventFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayout, mNoticeFragment, "notice").hide(mNoticeFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.frameLayout, mMainFragment, "main").hide(mMainFragment).commit();

        int selectedItemId = R.id.main_item;
        Bundle intentExtras = getIntent().getExtras();
        if(intentExtras != null)
            selectedItemId = intentExtras.getInt("selectedItemId", R.id.main_item);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        bottomNavigation.setSelectedItemId(selectedItemId);
    }

    public void showFragmentWithTransition(Fragment current, Fragment newFragment, String tag, View sharedView, int position) {
        Bundle bundle = new Bundle();

        bundle.putString("TRANS_CARD_NAME", "transCard" + position);

        bundle.putString("TITLE", String.valueOf(((TextView) sharedView.findViewById(R.id.title)).getText()));
        bundle.putString("UPLOADER", String.valueOf(((TextView) sharedView.findViewById(R.id.uploader)).getText()));
        bundle.putString("UPLOAD_DATE", String.valueOf(((TextView) sharedView.findViewById(R.id.uploadDate)).getText()));

        newFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

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

        transaction
                .replace(R.id.frameLayout, newFragment, tag)
                .addToBackStack(null /* name */)
                .commit();
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
                    break;
                case R.id.notice_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mNoticeFragment).commit();
                    mActiveFragment = mNoticeFragment;
                    break;
                case R.id.calender_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mSchoolEventFragment).commit();
                    mSchoolEventFragment.checkSchoolEventAutoDownload();
                    mActiveFragment = mSchoolEventFragment;
                    break;
                case R.id.setting_item:
                    mFragmentTransaction.hide(mActiveFragment).show(mPreferenceFragment).commit();
                    mActiveFragment = mPreferenceFragment;
                    break;
            }
            return true;
        }
    }
}
