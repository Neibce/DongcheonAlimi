package me.tyoj.dcalimi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.thebluealliance.spectrum.SpectrumPreferenceCompat;


public class PreferenceFragment extends PreferenceFragmentCompat  {
    private FragmentManager mFragmentManager;
    private Context mContext;
    private String mStrTodayYear;
    private String mStrTodayMonth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, final String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        mFragmentManager = getParentFragmentManager();
        mContext = getContext();

        MyDate myDate = new MyDate();
        mStrTodayYear = myDate.getYear();
        mStrTodayMonth = myDate.getMonth();

        Preference themeColor = findPreference("themeColor");
        if(themeColor != null) {
            themeColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Activity activity = getActivity();
                    if(activity != null) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("selectedItemId", R.id.setting_item);
                        startActivity(intent);

                        activity.finish();
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    return true;
                }
            });
        }

        Preference schoolMealDownload = findPreference("schoolMealDownload");
        if(schoolMealDownload != null) {
            schoolMealDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolMeal(mFragmentManager, getView()).download(mStrTodayYear, mStrTodayMonth);
                    return false;
                }
            });
        }

        Preference schoolEventDownload = findPreference("schoolEventDownload");
        if(schoolEventDownload != null) {
            schoolEventDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolEvent(mFragmentManager, getView()).download(mStrTodayYear, mStrTodayMonth);
                    return false;
                }
            });
        }

        Preference dDayDownload = findPreference("dDayDownload");
        if(dDayDownload != null) {
            dDayDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolExam(mFragmentManager, getView()).download(mStrTodayYear);
                    return false;
                }
            });
        }
    }

    @Override public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
