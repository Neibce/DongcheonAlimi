package me.tyoj.dcalimi;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class PreferenceFragment extends PreferenceFragmentCompat  {
    private FragmentManager mFragmentManager;
    private Context mContext;
    private String mStrTodayYear;
    private String mStrTodayMonth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        mFragmentManager = getParentFragmentManager();
        mContext = getContext();

        MyDate myDate = new MyDate();
        mStrTodayYear = myDate.getYear();
        mStrTodayMonth = myDate.getMonth();

        Preference schoolMealDownload = findPreference("schoolMealDownload");
        if(schoolMealDownload != null) {
            schoolMealDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolMeal(mFragmentManager, mContext).download(mStrTodayYear, mStrTodayMonth);
                    return false;
                }
            });
        }

        Preference schoolEventDownload = findPreference("schoolEventDownload");
        if(schoolEventDownload != null) {
            schoolEventDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolEvent(mFragmentManager, mContext).download(mStrTodayYear, mStrTodayMonth);
                    return false;
                }
            });
        }

        Preference dDayDownload = findPreference("dDayDownload");
        if(dDayDownload != null) {
            dDayDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolExam(getParentFragmentManager(), getContext()).download(mStrTodayYear);
                    return false;
                }
            });
        }
    }
}
