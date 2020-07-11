package dev.jun0.dcalimi.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import java.util.ArrayList;
import java.util.Set;

import dev.jun0.dcalimi.util.MyDate;
import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.SchoolClass;
import dev.jun0.dcalimi.util.SchoolEvent;
import dev.jun0.dcalimi.util.SchoolExam;
import dev.jun0.dcalimi.util.SchoolMeal;
import dev.jun0.dcalimi.util.SchoolTimeSchedule;
import dev.jun0.dcalimi.activity.MainActivity;


public class PreferenceFragment extends PreferenceFragmentCompat  {
    private FragmentManager mFragmentManager;
    private String mStrTodayYear;
    private String mStrTodayMonth;

    private MainFragment mMainFragment;
    private SchoolEventFragment mSchoolEventFragment;

    public PreferenceFragment(){}

    public PreferenceFragment(MainFragment mainFragment, SchoolEventFragment schoolEventFragment){
        mMainFragment = mainFragment;
        mSchoolEventFragment = schoolEventFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentManager.putFragment(outState, "mainFragment", mMainFragment);
        mFragmentManager.putFragment(outState, "schoolEventFragment", mSchoolEventFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mMainFragment = (MainFragment) mFragmentManager.getFragment(savedInstanceState, "mainFragment");
            mSchoolEventFragment = (SchoolEventFragment) mFragmentManager.getFragment(savedInstanceState, "schoolEventFragment");
        }
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

        Preference dDayOption = findPreference("dDayOption");
        if(dDayOption != null) {
            dDayOption.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mMainFragment.refreshDDay((Set<String>)newValue);
                    return true;
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
                                                                    mMainFragment.refreshSchedule();
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

        Preference ossLicenseInfo = findPreference("ossLicenseInfo");
        if(ossLicenseInfo != null) {
            ossLicenseInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title));
                    startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
                    return false;
                }
            });
        }

        EditTextPreference enterCode = findPreference("enterCode");
        if(enterCode != null) {
            enterCode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(((String)newValue).matches("^kU59dQ.{18}$")) {
                        Toast.makeText(getView().getContext(), "관리자 모드가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                        return true;
                    }else
                        return false;
                }
            });
        }

        Preference contactToDeveloper = findPreference("contactToDeveloper");
        if(contactToDeveloper != null) {
            contactToDeveloper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.developer_email), null));
                    startActivity(Intent.createChooser(emailIntent, "이메일 보내기"));
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
