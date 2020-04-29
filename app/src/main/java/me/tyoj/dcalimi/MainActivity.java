package me.tyoj.dcalimi;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialContainerTransform;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView bottomNavigation;

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
                    fragmentTransaction.replace(R.id.frameLayout, new PreferenceFragment()).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
