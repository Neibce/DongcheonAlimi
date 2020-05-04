package me.tyoj.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class SchoolExam {
    private Context mContext;
    private View mView;
    private FragmentManager mFragmentManager;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences mPreferenceSharedPreferences;

    SchoolExam(Context context){
        mSharedPreferences = context.getSharedPreferences("exams", MODE_PRIVATE);
        mPreferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    SchoolExam(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mContext = view.getContext();
        mView = view;
        mSharedPreferences = mContext.getSharedPreferences("exams", MODE_PRIVATE);
        mPreferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Nullable
    public Pair<String, Long> getDDay(String year, String month, String date) {
        String strJSONEvent = mSharedPreferences.getString(year,null);
        if(strJSONEvent == null)
            return null;
        try {
            JSONArray jsonArray = new JSONArray(strJSONEvent);

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date nowDate = transFormat.parse(year+"-"+month+"-"+date);
            Calendar nowDateCalender = Calendar.getInstance();
            nowDateCalender.setTime(nowDate);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String strExamStart = jsonObject.getString("startDate");
                String strExamEnd = jsonObject.getString("endDate");
                int examType = jsonObject.getInt("type");

                Set<String> optionValues = mPreferenceSharedPreferences.getStringSet("dDayOption", null);
                if (optionValues != null) {
                    if (!optionValues.contains("1") && examType == 0)
                        continue;
                    else if (!optionValues.contains("2") && examType == 1)
                        continue;
                    else if (!optionValues.contains("3") && examType == 2)
                        continue;
                    else if (!optionValues.contains("4") && examType == 3)
                        continue;
                }


                Date dateExamStart = transFormat.parse(strExamStart);
                Calendar calendarExamStart = Calendar.getInstance();
                calendarExamStart.setTime(dateExamStart);

                Date dateExamEnd = transFormat.parse(strExamEnd);
                Calendar calendarExamEnd = Calendar.getInstance();
                calendarExamEnd.setTime(dateExamEnd);

                long diffBtwTES = (calendarExamStart.getTimeInMillis() - nowDateCalender.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                long diffBtwTEE = (calendarExamEnd.getTimeInMillis() - nowDateCalender.getTimeInMillis()) / (24 * 60 * 60 * 1000);

                if(diffBtwTES >= 0)
                    return Pair.create(jsonObject.getString("title"), diffBtwTES);
                else if(diffBtwTEE >= 0)
                    return Pair.create(jsonObject.getString("title"), 0L);

                Log.i("SE", strExamStart + ": " + diffBtwTEE);
            }
            Calendar nextYearCalender = Calendar.getInstance();
            nextYearCalender.add(Calendar.YEAR, 1);
            nextYearCalender.set(Calendar.MONTH, 1);
            nextYearCalender.set(Calendar.DATE, 1);
            long diffBtw = (nextYearCalender.getTimeInMillis() - nowDateCalender.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            return Pair.create(nextYearCalender.get(Calendar.YEAR)+"년", diffBtw);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean hasList(String year){
        return (mSharedPreferences.getString(year,null) != null);
    }

    public void download(String year){
        Runnable runnable = new SchoolExamDownloadRunnable(mFragmentManager, mView, year);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static class SchoolExamDownloadRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final String mYear;

        SchoolExamDownloadRunnable(FragmentManager fragmentManager, View view, String year){
            mHandler = new MyHandler(fragmentManager, view);
            mContext = view.getContext();
            mYear = year;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
                    throw new Exception();
                }

                sendHandlerShowDialog(mContext.getString(R.string.info), mContext.getString(R.string.downloading_d_day_info), false,false);
                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("https://dc-api.jun0.dev/exams/" + mYear, 15000);

                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(mYear, jsonObjectResponse.get("exams").toString());
                editor.apply();

                Log.d("TAG", "doInBackground: " + jsonObjectResponse.get("exams").toString());

                sendHandlerHideDialog();
                sendHandlerShowSnackbar(mContext.getString(R.string.download_successfully));
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowSnackbar(mContext.getString(R.string.download_failed));
                e.printStackTrace();
            }
        }

        private void sendHandlerShowDialog(String dialogTitle, String dialogMessage, boolean hasPositiveButton, boolean cancelable){
            Bundle data = new Bundle();
            Message msg = new Message();
            data.putString("title", dialogTitle);
            data.putString("msg", dialogMessage);
            data.putBoolean("hasPositive", hasPositiveButton);
            data.putBoolean("cancelable", cancelable);

            msg.setData(data);
            msg.what = MyHandler.SHOW_DIALOG;
            mHandler.sendMessage(msg);
        }

        private void sendHandlerHideDialog(){
            Message msg = new Message();
            msg.what = MyHandler.HIDE_DIALOG;
            mHandler.sendMessage(msg);
        }

        private void sendHandlerShowSnackbar(String text){
            Message msg = new Message();
            msg.obj = text;
            msg.what = MyHandler.SHOW_SNACKBAR;
            mHandler.sendMessage(msg);
        }
    }
}
