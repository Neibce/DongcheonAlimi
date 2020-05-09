package dev.jun0.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.pm10.library.CircleIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainFragment extends Fragment {
    private String mSelYear;
    private String mSelMonth;
    private String mSelDate;
    private String mSelDay;

    private TouchDelegateComposite mTouchDelegateComposite;
    private SharedPreferences mPreferenceSharedPreferences;
    private SchoolMeal mSchoolMeal;
    private SchoolExam mSchoolExam;
    private MyDate mMyDate;
    private Calendar mCalendarMealSelected;

    private ViewPager mViewPager;
    private TextView mTvDDayTitle;
    private TextView mTvDDayLeft;

    private TextView mTvScMon[] = new TextView[7];
    private TextView mTvScTue[] = new TextView[7];
    private TextView mTvScWed[] = new TextView[7];
    private TextView mTvScThu[] = new TextView[7];
    private TextView mTvScFri[] = new TextView[7];

    //private BusInfo mBusInfo;

    private void setDateValues(Calendar calendar){
        mSelYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        mSelMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
        mSelDate = new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime());
        mSelDay = new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime());
    }

    public void onDateChanged() {
        mMyDate = new MyDate();
        refreshDDay();
        //TODO: 급식 날짜 변경 관련 판단 추가
        refreshViewPager();
    }

    private SchoolMealViewPagerAdapter mSchoolMealViewPagerAdapter;
    public void refreshViewPager(){
        mViewPager.setCurrentItem(0);
        mSchoolMealViewPagerAdapter.setDate(mSelYear, mSelMonth, mSelDate);
        mViewPager.setAdapter(mSchoolMealViewPagerAdapter);
        mSchoolMealViewPagerAdapter.notifyDataSetChanged();
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

    private void initScheduleTextView(View view){
        mTvScMon[0] = view.findViewById(R.id.tvScMon1);
        mTvScMon[1] = view.findViewById(R.id.tvScMon2);
        mTvScMon[2] = view.findViewById(R.id.tvScMon3);
        mTvScMon[3] = view.findViewById(R.id.tvScMon4);
        mTvScMon[4] = view.findViewById(R.id.tvScMon5);
        mTvScMon[5] = view.findViewById(R.id.tvScMon6);
        mTvScMon[6] = view.findViewById(R.id.tvScMon7);

        mTvScTue[0] = view.findViewById(R.id.tvScTue1);
        mTvScTue[1] = view.findViewById(R.id.tvScTue2);
        mTvScTue[2] = view.findViewById(R.id.tvScTue3);
        mTvScTue[3] = view.findViewById(R.id.tvScTue4);
        mTvScTue[4] = view.findViewById(R.id.tvScTue5);
        mTvScTue[5] = view.findViewById(R.id.tvScTue6);
        mTvScTue[6] = view.findViewById(R.id.tvScTue7);

        mTvScWed[0] = view.findViewById(R.id.tvScWed1);
        mTvScWed[1] = view.findViewById(R.id.tvScWed2);
        mTvScWed[2] = view.findViewById(R.id.tvScWed3);
        mTvScWed[3] = view.findViewById(R.id.tvScWed4);
        mTvScWed[4] = view.findViewById(R.id.tvScWed5);
        mTvScWed[5] = view.findViewById(R.id.tvScWed6);
        mTvScWed[6] = view.findViewById(R.id.tvScWed7);

        mTvScThu[0] = view.findViewById(R.id.tvScThu1);
        mTvScThu[1] = view.findViewById(R.id.tvScThu2);
        mTvScThu[2] = view.findViewById(R.id.tvScThu3);
        mTvScThu[3] = view.findViewById(R.id.tvScThu4);
        mTvScThu[4] = view.findViewById(R.id.tvScThu5);
        mTvScThu[5] = view.findViewById(R.id.tvScThu6);
        mTvScThu[6] = view.findViewById(R.id.tvScThu7);

        mTvScFri[0] = view.findViewById(R.id.tvScFri1);
        mTvScFri[1] = view.findViewById(R.id.tvScFri2);
        mTvScFri[2] = view.findViewById(R.id.tvScFri3);
        mTvScFri[3] = view.findViewById(R.id.tvScFri4);
        mTvScFri[4] = view.findViewById(R.id.tvScFri5);
        mTvScFri[5] = view.findViewById(R.id.tvScFri6);
        mTvScFri[6] = view.findViewById(R.id.tvScFri7);
    }

    private void getMealNext(){
        int amount = 1;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            amount = 3;

        mCalendarMealSelected.add(Calendar.DATE, amount);

        setDateValues(mCalendarMealSelected);
        refreshViewPager();

    }

    private void getMealBefore(){
        int amount = -1;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            amount = -3;

        mCalendarMealSelected.add(Calendar.DATE, amount);

        setDateValues(mCalendarMealSelected);
        refreshViewPager();
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
        Context context = view.getContext();

        mMyDate = new MyDate();

        mPreferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mSchoolMeal = new SchoolMeal(fragmentManager, view);
        mSchoolMealViewPagerAdapter = new SchoolMealViewPagerAdapter(fragmentManager);

        mTvDDayTitle = view.findViewById(R.id.tvDDayTitle);
        mTvDDayLeft = view.findViewById(R.id.tvDDayLeft);

        mSchoolExam = new SchoolExam(context);
        refreshDDay();

        initScheduleTextView(view);


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

        mCalendarMealSelected = Calendar.getInstance();
        int amount = 0;
        if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            amount = 2;
        else if (mCalendarMealSelected.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            amount = 1;
        mCalendarMealSelected.add(Calendar.DATE, amount);
        setDateValues(mCalendarMealSelected);
        refreshViewPager();

        CircleIndicator circleIndicator = view.findViewById(R.id.indicatorMeal);
        circleIndicator.setupWithViewPager(mViewPager);

        final TextView tvMealDate = view.findViewById(R.id.tvMealDate);
        tvMealDate.setText(String.format(Locale.getDefault(),"%s월 %s일 (%s)", mSelMonth, mSelDate, mSelDay));

        final ImageButton btnMealDateNext = view.findViewById(R.id.btnMealDateNext);
        final ImageButton btnMealDateBefore = view.findViewById(R.id.btnMealDateBefore);

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

                tvMealDate.setText(String.format(Locale.getDefault(),"%s월 %s일 (%s)", mSelMonth, mSelDate, mSelDay));
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

                tvMealDate.setText(String.format(Locale.getDefault(),"%s월 %s일 (%s)", mSelMonth, mSelDate, mSelDay));
            }
        });

        mTouchDelegateComposite = new TouchDelegateComposite((View)btnMealDateBefore.getParent());
        increaseImageButtonArea(btnMealDateBefore);
        increaseImageButtonArea(btnMealDateNext);

        return view;
    }
}
