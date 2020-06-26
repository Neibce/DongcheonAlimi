package dev.jun0.dcalimi.fragment.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.pm10.library.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dev.jun0.dcalimi.util.MyDate;
import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.SchoolExam;
import dev.jun0.dcalimi.util.SchoolMeal;
import dev.jun0.dcalimi.adapter.SchoolMealViewPagerAdapter;
import dev.jun0.dcalimi.util.TouchDelegateComposite;

public class MainFragment extends Fragment {
    private String mSelYear;
    private String mSelMonth;
    private String mSelDate;
    private String mSelDay;

    private TouchDelegateComposite mTouchDelegateComposite;
    private Context mContext;
    private SharedPreferences mPreferenceSharedPreferences;
    private SharedPreferences mScheduleSharedPreferences;
    private SchoolMeal mSchoolMeal;
    private SchoolExam mSchoolExam;
    private MyDate mMyDate;
    private Calendar mCalendarMealSelected;

    private ViewPager mViewPager;
    private TextView mTvDDayTitle;
    private TextView mTvMealDate;
    private TextView mTvDDayLeft;
    private Spinner mSpinnerSchedule;

    private TextView[][] mTvSc = new TextView[5][7];

    //private BusInfo mBusInfo;

    public MainFragment(){}

    private void setDateValues(Calendar calendar){
        mSelYear = new SimpleDateFormat("yyyy", Locale.KOREAN).format(calendar.getTime());
        mSelMonth = new SimpleDateFormat("MM", Locale.KOREAN).format(calendar.getTime());
        mSelDate = new SimpleDateFormat("dd", Locale.KOREAN).format(calendar.getTime());
        mSelDay = new SimpleDateFormat("E", Locale.KOREAN).format(calendar.getTime());
    }

    public void onDateChanged() {
        mMyDate = new MyDate();
        refreshDDay();
        getMealToday();
    }

    private SchoolMealViewPagerAdapter mSchoolMealViewPagerAdapter;
    public void refreshViewPager(){
        mViewPager.setCurrentItem(0);
        mSchoolMealViewPagerAdapter.setDate(mSelYear, mSelMonth, mSelDate);
        mViewPager.setAdapter(mSchoolMealViewPagerAdapter);
        mSchoolMealViewPagerAdapter.notifyDataSetChanged();
    }

    public void refreshDDay(Set<String> value){
        if(mSchoolExam != null) {
            mSchoolExam.setOptionValues(value);
            refreshDDay();
        }
    }
    public void refreshDDay(){
        if(mSchoolExam != null && mTvDDayTitle != null && mTvDDayLeft != null) {
            Pair<String, Long> pairDDay = mSchoolExam.getDDay(mMyDate.getYear(), mMyDate.getMonth(), mMyDate.getDate());
            if (pairDDay != null && pairDDay.second != null) {
                mTvDDayTitle.setText(String.format(getString(R.string.d_day_title), pairDDay.first));
                if (pairDDay.second == 0)
                    mTvDDayLeft.setText(getString(R.string.d_day));
                else
                    mTvDDayLeft.setText(String.format(getString(R.string.d_day_left), pairDDay.second));
            } else {
                mTvDDayTitle.setText("다운로드 필요");
                mTvDDayLeft.setText("D-DAY");
            }
        }
    }

    public void refreshSchedule(){
        setScheduleSpinnerAdapter();
        if(mScheduleSharedPreferences != null) {
            int selectedGrade = mScheduleSharedPreferences.getInt("selectedGrade", 0);
            int selectedClass = mScheduleSharedPreferences.getInt("selectedClass", 0);
            if(mSpinnerSchedule != null && selectedGrade != 0 && selectedClass != 0) {
                setScheduleTextView(selectedGrade, selectedClass);
                mSpinnerSchedule.setSelection(getScheduleIndex(selectedGrade, selectedClass));
            }
        }
    }

    public void checkSchoolMealAutoDownload(){
        if(mPreferenceSharedPreferences != null && mSchoolMeal != null) {
            boolean isGetMealAuto = mPreferenceSharedPreferences.getBoolean("schoolMealAutoDownload", false);
            if (isGetMealAuto) {
                boolean hasMealList = mSchoolMeal.hasList(mMyDate.getYear(), mMyDate.getMonth());

                if (!hasMealList) {
                    mSchoolMeal.download(mMyDate.getYear(), mMyDate.getMonth(), new SchoolMeal.OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            refreshViewPager();
                        }
                    });
                }
            }
        }
    }

    List<Pair<Integer, Integer>> mScheduleSpinnerRawList =  new ArrayList<>();
    private void setScheduleSpinnerAdapter(){
        if(mContext != null && mSpinnerSchedule != null && mScheduleSharedPreferences != null) {
            List<String> spinnerArray = new ArrayList<>();
            mScheduleSpinnerRawList =  new ArrayList<>();

            String strDownloadedList = mScheduleSharedPreferences.getString("list", "[]");
            try {
                JSONArray jsonArray = new JSONArray(strDownloadedList);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int _grade = jsonObject.getInt("grade");
                    int _class = jsonObject.getInt("class");

                    spinnerArray.add(_grade + "학년 " + _class + "반");
                    mScheduleSpinnerRawList.add(Pair.create(_grade, _class));
                }
                if (jsonArray.length() == 0)
                    spinnerArray.add("없음");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            mSpinnerSchedule.setAdapter(adapter);
        }
    }

    private void initScheduleTextView(View view){
        mTvSc[0][0] = view.findViewById(R.id.tvScMon1);
        mTvSc[0][1] = view.findViewById(R.id.tvScMon2);
        mTvSc[0][2] = view.findViewById(R.id.tvScMon3);
        mTvSc[0][3] = view.findViewById(R.id.tvScMon4);
        mTvSc[0][4] = view.findViewById(R.id.tvScMon5);
        mTvSc[0][5] = view.findViewById(R.id.tvScMon6);
        mTvSc[0][6] = view.findViewById(R.id.tvScMon7);

        mTvSc[1][0] = view.findViewById(R.id.tvScTue1);
        mTvSc[1][1] = view.findViewById(R.id.tvScTue2);
        mTvSc[1][2] = view.findViewById(R.id.tvScTue3);
        mTvSc[1][3] = view.findViewById(R.id.tvScTue4);
        mTvSc[1][4] = view.findViewById(R.id.tvScTue5);
        mTvSc[1][5] = view.findViewById(R.id.tvScTue6);
        mTvSc[1][6] = view.findViewById(R.id.tvScTue7);

        mTvSc[2][0] = view.findViewById(R.id.tvScWed1);
        mTvSc[2][1] = view.findViewById(R.id.tvScWed2);
        mTvSc[2][2] = view.findViewById(R.id.tvScWed3);
        mTvSc[2][3] = view.findViewById(R.id.tvScWed4);
        mTvSc[2][4] = view.findViewById(R.id.tvScWed5);
        mTvSc[2][5] = view.findViewById(R.id.tvScWed6);
        mTvSc[2][6] = view.findViewById(R.id.tvScWed7);

        mTvSc[3][0] = view.findViewById(R.id.tvScThu1);
        mTvSc[3][1] = view.findViewById(R.id.tvScThu2);
        mTvSc[3][2] = view.findViewById(R.id.tvScThu3);
        mTvSc[3][3] = view.findViewById(R.id.tvScThu4);
        mTvSc[3][4] = view.findViewById(R.id.tvScThu5);
        mTvSc[3][5] = view.findViewById(R.id.tvScThu6);
        mTvSc[3][6] = view.findViewById(R.id.tvScThu7);

        mTvSc[4][0] = view.findViewById(R.id.tvScFri1);
        mTvSc[4][1] = view.findViewById(R.id.tvScFri2);
        mTvSc[4][2] = view.findViewById(R.id.tvScFri3);
        mTvSc[4][3] = view.findViewById(R.id.tvScFri4);
        mTvSc[4][4] = view.findViewById(R.id.tvScFri5);
        mTvSc[4][5] = view.findViewById(R.id.tvScFri6);
        mTvSc[4][6] = view.findViewById(R.id.tvScFri7);
    }

    private void setScheduleTextView(int _grade, int _class){
        if(mScheduleSharedPreferences != null) {
            String strScheduleJson = mScheduleSharedPreferences.getString(_grade + "-" + _class, "[]");

            try {
                JSONArray jsonArray = new JSONArray(strScheduleJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int day = jsonObject.getInt("day") - 1;
                    int period = jsonObject.getInt("period") - 1;
                    String strSubject = jsonObject.getString("subject");
                    String strTeacher = jsonObject.getString("teacher");
                    Log.d("MF", "setScheduleTextView: " + strSubject + strTeacher);

                    if(mTvSc[day][period] != null)
                        mTvSc[day][period].setText(strSubject + "\n" + strTeacher);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getScheduleIndex(int _grade, int _class){
        for (int i = 0; i < mScheduleSpinnerRawList.size(); i++) {
            Pair<Integer, Integer> schedule = mScheduleSpinnerRawList.get(i);
            if (schedule.first == _grade && schedule.second == _class)
                return i;
        }
        return -1;
    }

    private void getMealNext(){
        int amount = 1;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            amount = 3;

        mCalendarMealSelected.add(Calendar.DATE, amount);

        setDateValues(mCalendarMealSelected);
        refreshViewPager();
        updateMealDateTvText();
    }

    private void getMealToday(){
        mCalendarMealSelected = Calendar.getInstance();
        int amount = 0;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            amount = 2;
        else if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            amount = 1;
        mCalendarMealSelected.add(Calendar.DATE, amount);

        setDateValues(mCalendarMealSelected);
        refreshViewPager();
        updateMealDateTvText();
    }

    private void getMealBefore(){
        int amount = -1;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            amount = -3;

        mCalendarMealSelected.add(Calendar.DATE, amount);

        setDateValues(mCalendarMealSelected);
        refreshViewPager();
        updateMealDateTvText();
    }

    private void updateMealDateTvText(){
        if(mTvMealDate != null)
            mTvMealDate.setText(String.format(Locale.getDefault(),"%s월 %s일 (%s)", mSelMonth, mSelDate, mSelDay));
    }

    private boolean canGetMealBefore(){
        return !(mCalendarMealSelected.get(Calendar.DATE) == 1
                || (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && mCalendarMealSelected.get(Calendar.DATE) - 3 < 1));
    }

    private boolean canGetMealNext(){
        return !(mCalendarMealSelected.get(Calendar.DATE) == mCalendarMealSelected.getActualMaximum(Calendar.DAY_OF_MONTH)
                || (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && mCalendarMealSelected.get(Calendar.DATE) + 3 > mCalendarMealSelected.getActualMaximum(Calendar.DAY_OF_MONTH)));
    }

    private void increaseImageButtonArea(final ImageButton button) {
        View parent = (View) button.getParent();

        parent.post(new Runnable() {
            public void run() {
                Rect rect = new Rect();
                button.getHitRect(rect);
                rect.top -= 25;
                rect.left -= 25;
                rect.bottom += 25;
                rect.right += 25;
                mTouchDelegateComposite.addDelegate(new TouchDelegate(rect, button));
            }
        });
        parent.setTouchDelegate(mTouchDelegateComposite);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FragmentManager fragmentManager = getParentFragmentManager();
        mContext = view.getContext();

        mMyDate = new MyDate();

        mPreferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mScheduleSharedPreferences = mContext.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        final SharedPreferences.Editor mScheduleSharedPreferencesEditor = mScheduleSharedPreferences.edit();

        mSchoolMeal = new SchoolMeal(fragmentManager, view);
        mSchoolMealViewPagerAdapter = new SchoolMealViewPagerAdapter(fragmentManager);

        mTvDDayTitle = view.findViewById(R.id.tvDDayTitle);
        mTvDDayLeft = view.findViewById(R.id.tvDDayLeft);

        mSchoolExam = new SchoolExam(mContext);
        refreshDDay();

        mSpinnerSchedule = view.findViewById(R.id.spinnerSchedule);
        initScheduleTextView(view);
        mSpinnerSchedule.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mScheduleSpinnerRawList != null && mScheduleSpinnerRawList.size() != 0 && mScheduleSpinnerRawList.get(position).first != null && mScheduleSpinnerRawList.get(position).second != null) {
                    Log.d("MF", "onItemSelected: " + mScheduleSpinnerRawList.get(position).first + mScheduleSpinnerRawList.get(position).second);
                    setScheduleTextView(mScheduleSpinnerRawList.get(position).first, mScheduleSpinnerRawList.get(position).second);

                    mScheduleSharedPreferencesEditor.putInt("selectedGrade", mScheduleSpinnerRawList.get(position).first);
                    mScheduleSharedPreferencesEditor.putInt("selectedClass", mScheduleSpinnerRawList.get(position).second);
                    mScheduleSharedPreferencesEditor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        refreshSchedule();

        /*ImageButton btnBusInfoRefresh = view.findViewById(R.id.btnBusInfoRefresh);
        mBusInfo = new BusInfo(getParentFragmentManager(), getContext(), view);
        mBusInfo.get();
        btnBusInfoRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mBusInfo.get();
            }
        });*/

        checkSchoolMealAutoDownload();

        mViewPager = view.findViewById(R.id.viewPagerMeal);

        getMealToday();

        CircleIndicator circleIndicator = view.findViewById(R.id.indicatorMeal);
        circleIndicator.setupWithViewPager(mViewPager);

        mTvMealDate = view.findViewById(R.id.tvMealDate);
        mTvMealDate.setText(String.format(Locale.getDefault(),"%s월 %s일 (%s)", mSelMonth, mSelDate, mSelDay));

        final ImageButton btnMealDateNext = view.findViewById(R.id.btnMealDateNext);
        final ImageButton btnMealDateBefore = view.findViewById(R.id.btnMealDateBefore);

        if(!canGetMealBefore()){
            btnMealDateBefore.setEnabled(false);
            btnMealDateBefore.setVisibility(View.INVISIBLE);
        }
        if(!canGetMealNext()){
            btnMealDateNext.setEnabled(false);
            btnMealDateNext.setVisibility(View.INVISIBLE);
        }


        btnMealDateBefore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnMealDateNext.setEnabled(true);
                btnMealDateNext.setVisibility(View.VISIBLE);

                getMealBefore();

                if(!canGetMealBefore()){
                    btnMealDateBefore.setEnabled(false);
                    btnMealDateBefore.setVisibility(View.INVISIBLE);
                }

                updateMealDateTvText();
            }
        });

        btnMealDateNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnMealDateBefore.setEnabled(true);
                btnMealDateBefore.setVisibility(View.VISIBLE);

                getMealNext();

                if(!canGetMealNext()){
                    btnMealDateNext.setEnabled(false);
                    btnMealDateNext.setVisibility(View.INVISIBLE);
                }

                updateMealDateTvText();
            }
        });

        mTouchDelegateComposite = new TouchDelegateComposite((View)btnMealDateBefore.getParent());
        increaseImageButtonArea(btnMealDateBefore);
        increaseImageButtonArea(btnMealDateNext);

        return view;
    }
}
