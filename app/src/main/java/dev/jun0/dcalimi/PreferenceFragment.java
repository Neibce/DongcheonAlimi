package dev.jun0.dcalimi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import java.util.ArrayList;


public class PreferenceFragment extends PreferenceFragmentCompat  {
    private FragmentManager mFragmentManager;
    private String mStrTodayYear;
    private String mStrTodayMonth;

    private MainFragment mMainFragment;
    private SchoolEventFragment mSchoolEventFragment;

    PreferenceFragment(MainFragment mainFragment, SchoolEventFragment schoolEventFragment){
        mMainFragment = mainFragment;
        mSchoolEventFragment = schoolEventFragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, final String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        mFragmentManager = getParentFragmentManager();

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
                    new SchoolMeal(mFragmentManager, getView())
                            .download(mStrTodayYear, mStrTodayMonth, new SchoolMeal.OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            mMainFragment.refreshViewPager();
                        }
                    });
                    return false;
                }
            });
        }

        Preference schoolEventDownload = findPreference("schoolEventDownload");
        if(schoolEventDownload != null) {
            schoolEventDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SchoolEvent schoolEvent = new SchoolEvent(mFragmentManager, getView());
                    schoolEvent.setOnDownloadCompleteListener(new SchoolEvent.OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            mSchoolEventFragment.refreshRecyclerView();
                        }
                    });
                    schoolEvent.download(mStrTodayYear, mStrTodayMonth);
                    return false;
                }
            });
        }

        Preference dDayDownload = findPreference("dDayDownload");
        if(dDayDownload != null) {
            dDayDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SchoolExam(mFragmentManager, getView())
                            .download(mStrTodayYear, new SchoolExam.OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            mMainFragment.refreshDDay();
                        }
                    });
                    return false;
                }
            });
        }

        Preference timeScheduleDownload = findPreference("timeScheduleDownload");
        if(timeScheduleDownload != null) {
            timeScheduleDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference){
                    String[] items = {"1학년", "2학년", "3학년"};
                    showDialogWithItems(getView().getContext(),"학년 선택", items,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, final int gradeSelected) {
                                    dialog.dismiss();

                                    new SchoolClass(mFragmentManager, getView()).downloadTotalNumber(gradeSelected + 1, new SchoolClass.OnDownloadTotalNumberCompleteListener() {
                                        @Override
                                        public void onDownloadComplete(int count) {
                                            ArrayList<String> strClassArrayList = new ArrayList<>();

                                            for(int i = 1; i <= count; i++)
                                                strClassArrayList.add(gradeSelected + 1 + "학년 " + i + "반");

                                            showDialogWithItems(getView().getContext(),"학반 선택", strClassArrayList.toArray(new String[0]),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int classSelected) {
                                                            new SchoolTimeSchedule(mFragmentManager, getView())
                                                                    .download(gradeSelected + 1, classSelected + 1, new SchoolTimeSchedule.OnDownloadCompleteListener(){
                                                                @Override
                                                                public void onDownloadComplete() {
                                                                    //TODO 시간표 새로고침
                                                                }
                                                            });
                                                            dialog.dismiss();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });

                    return false;
                }
            });
        }
    }

    private void showDialogWithItems(Context context, String title, String[] items, DialogInterface.OnClickListener onClickListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, onClickListener)
                .show();
    }

    @Override public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
