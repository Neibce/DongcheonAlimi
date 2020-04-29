package me.tyoj.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class SchoolExam {
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private static SharedPreferences pref;

    SchoolExam(FragmentManager fragmentManager, Context context){
        mFragmentManager = fragmentManager;
        mContext = context;
        pref = mContext.getSharedPreferences("exams", MODE_PRIVATE);
    }

    @Nullable
    public Pair<String, Long> getDDay(String year, String month, String date) {
        String strJSONEvent = pref.getString(year,null);
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
                String strDate = jsonObject.getString("startDate");

                Date examDate = transFormat.parse(strDate);
                Calendar examDateCalender = Calendar.getInstance();
                examDateCalender.setTime(examDate);

                long diff = (examDateCalender.getTimeInMillis() - nowDateCalender.getTimeInMillis()) / (24 * 60 * 60 * 1000);

                if(diff >= 0)
                    return Pair.create(jsonObject.getString("title"), diff);

                Log.i("SE", strDate + ": " + diff);
            }
            return null;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean hasList(String year){
        return (pref.getString(year,null) != null);
    }

    public void download(String year){
        int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
        if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            DialogFragment dialogFragment = new MyDialogFragment(mContext.getString(R.string.error), "네트워크에 연결할 수 없습니다.\n연결 상태를 확인 후 재시도 해 주시기 바랍니다.", true);
            dialogFragment.show(mFragmentManager, "Network Error");
            return;
        }

        Runnable runnable = new SchoolExamDownloadRunnable(mFragmentManager, mContext, year);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static class SchoolExamDownloadRunnable implements Runnable {
        private final Handler mHandler;
        private final String mYear;

        SchoolExamDownloadRunnable(FragmentManager fragmentManager, Context context, String year){
            mHandler = new MyHandler(fragmentManager, context);
            mYear = year;
        }

        @Override
        public void run() {
            mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADING_SCHOOL_EVENT);

            try {
                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("https://dc-api.jun0.dev/exams/" + mYear, 15000);

                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(mYear, jsonObjectResponse.get("exams").toString());
                editor.apply();

                Log.d("TAG", "doInBackground: " + jsonObjectResponse.get("exams").toString());

                mHandler.sendEmptyMessage(MyHandler.HIDE_DIALOG);
                mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADED_SUCCESSFULLY);
            } catch (JSONException | IOException e) {
                mHandler.sendEmptyMessage(MyHandler.HIDE_DIALOG);
                mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADING_FAILED);
                e.printStackTrace();
            }
        }
    }
}
