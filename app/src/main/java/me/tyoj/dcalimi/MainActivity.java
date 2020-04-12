package me.tyoj.dcalimi;

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
import androidx.transition.TransitionInflater;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new MainFragment()).commitAllowingStateLoss();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    public void showFragmentWithTransition(Fragment current, Fragment newFragment, String tag, View sharedView, int position) {
        Bundle bundle = new Bundle();

        bundle.putString("TRANS_TITLE_NAME", "transTitle" + position);
        bundle.putString("TRANS_UPLOADER_NAME", "transUploader" + position);
        bundle.putString("TRANS_UPLOAD_DATE_NAME", "transUploadDate" + position);

        bundle.putString("TITLE", String.valueOf(((TextView)sharedView.findViewById(R.id.title)).getText()));
        bundle.putString("UPLOADER", String.valueOf(((TextView)sharedView.findViewById(R.id.uploader)).getText()));
        bundle.putString("UPLOAD_DATE", String.valueOf(((TextView)sharedView.findViewById(R.id.uploadDate)).getText()));

        newFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // check if the fragment is in back stack
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(tag, 0);
        if (fragmentPopped) {
            // fragment is pop from backStack
        } else {
            current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

            newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
            newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, newFragment, tag);
            fragmentTransaction.addToBackStack(tag);

            fragmentTransaction.addSharedElement(sharedView.findViewById(R.id.title), "transTitle" + position);
            fragmentTransaction.addSharedElement(sharedView.findViewById(R.id.uploader), "transUploader" + position);
            fragmentTransaction.addSharedElement(sharedView.findViewById(R.id.uploadDate), "transUploadDate" + position);

            fragmentTransaction.commit();

            Log.i("MA", "showFragmentWithTransition: OK " + position);
        }
    }

    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            fragmentTransaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()){
                case R.id.main_item:
                    fragmentTransaction.replace(R.id.frameLayout, new MainFragment()).commitAllowingStateLoss();
                    break;
                case R.id.notice_item:
                    fragmentTransaction.replace(R.id.frameLayout, new NoticeFragment()).commitAllowingStateLoss();
                    break;
                case R.id.calender_item:
                    fragmentTransaction.replace(R.id.frameLayout, new SchoolEventFragment()).commitAllowingStateLoss();
                    break;
                case R.id.setting_item:
                    fragmentTransaction.replace(R.id.frameLayout, new SettingFragment()).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
