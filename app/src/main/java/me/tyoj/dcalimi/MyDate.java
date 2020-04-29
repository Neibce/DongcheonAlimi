package me.tyoj.dcalimi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyDate {
    private String mStrYear;
    private String mStrMonth;
    private String mStrDate;

    MyDate(Calendar calendar){
        setDate(calendar);
    }

    MyDate(){
        setDate(Calendar.getInstance());
    }

    private void setDate(Calendar calendar){
        mStrYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        mStrMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());
        mStrDate = new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime());
    }

    public String getYear(){
        return mStrYear;
    }

    public String getMonth(){
        return mStrMonth;
    }

    public String getDate(){
        return mStrDate;
    }
}
