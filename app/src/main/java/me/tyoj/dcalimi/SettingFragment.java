package me.tyoj.dcalimi;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingFragment extends PreferenceFragmentCompat {
    private String mStrNowYear;
    private String mStrNowMonth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        Calendar calendar = Calendar.getInstance();
        mStrNowYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        mStrNowMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());

        Preference schoolMealDownload = findPreference("school_meal_download");
        schoolMealDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new SchoolMeal(getFragmentManager(), getContext()).download(mStrNowYear, mStrNowMonth);
                return false;
            }
        });
        Preference schoolEventDownload = findPreference("school_event_download");
        schoolEventDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new SchoolEvent(getFragmentManager(), getContext()).download(mStrNowYear, mStrNowMonth);
                return false;
            }
        });
    }
}
