package dev.jun0.dcalimi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;

import dev.jun0.dcalimi.R;

public class SchoolMeal {
    private Context mContext;
    private View mView;
    private FragmentManager mFragmentManager;
    private static SharedPreferences mSharedPreferences;

    public SchoolMeal(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mContext = view.getContext();
        mView = view;
        mSharedPreferences = mContext.getSharedPreferences("meal", Context.MODE_PRIVATE);
    }

    public String get(String year, String month, String date, boolean dinner) throws NullPointerException {
        String strJSONMeal = mSharedPreferences.getString(year + month, null);
        if (strJSONMeal == null)
            return mContext.getString(R.string.meal_need_download);
        try {
            JSONObject jsonObject = new JSONObject(strJSONMeal);

            if(!jsonObject.has(date)
                    || !dinner && !jsonObject.getJSONObject(date).has("lunch")
                    || dinner && !jsonObject.getJSONObject(date).has("dinner"))
                return mContext.getString(R.string.meal_non_exist);

            String strMeal;
            if(dinner)
                strMeal = jsonObject.getJSONObject(date).getString("dinner");
            else
                strMeal = jsonObject.getJSONObject(date).getString("lunch");

            if(strMeal.equals(""))
                strMeal = mContext.getString(R.string.meal_non_exist);

            Log.d("MEAL", strMeal);
            return strMeal;
        } catch (JSONException e) {
            e.printStackTrace();
            return mContext.getString(R.string.meal_error);
        }
    }

    public Boolean hasList(String year, String month){
        return (mSharedPreferences.getString(year + month,null) != null);
    }

    public void download(String year, String month, OnDownloadCompleteListener onDownloadCompleteListener){
        Runnable runnable = new SchoolMealDownloadRunnable(mFragmentManager, mView, year, month, onDownloadCompleteListener);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public interface OnDownloadCompleteListener {
        void onDownloadComplete();
    }


    private static class SchoolMealDownloadRunnable implements Runnable {
       private final Handler mHandler;
       private final Context mContext;
       private final String mYear;
       private final String mMonth;
       private final OnDownloadCompleteListener mOnDownloadCompleteListener;

       SchoolMealDownloadRunnable(FragmentManager fragmentManager, View view, String year, String month, OnDownloadCompleteListener onDownloadCompleteListener){
            mHandler = new MyHandler(fragmentManager, view);
            mContext = view.getContext();
            mYear = year;
            mMonth = month;
            mOnDownloadCompleteListener=onDownloadCompleteListener;
       }

       @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
                    throw new Exception();
                }

                sendHandlerShowDialog(mContext.getString(R.string.info),mContext.getString(R.string.downloading_school_meal_list), false,false);
                Thread.sleep(500);

                Document doc = Jsoup.connect("https://stu.pen.go.kr/sts_sci_md00_001.do?ay=" + mYear + "&mm=" + mMonth + "&insttNm=동천고등학교&schulCode=C100000412&schulKndScCode=04&schulCrseScCode=4").timeout(15000).post();
                Elements tds = doc.getElementsByTag("td");

                JSONObject jsonParentObject = new JSONObject();
                for (Element td : tds) {
                    JSONObject jsonChildObject = new JSONObject();

                    String tdStr = td.toString();

                    tdStr = tdStr.replaceAll(" ","");
                    tdStr = tdStr.replaceAll("&amp;","&");
                    tdStr = tdStr.replaceAll("<br>|\\(동천\\)|<td.*>\n|<div>\n|</div></td>","");
                    tdStr = tdStr.replaceAll("[0-9]?[0-9]\\.","");
                    int lunchPos = tdStr.indexOf("[중식]");
                    int dinnerPos = tdStr.indexOf("[석식]");

                    if(lunchPos == -1)
                        continue;

                    if(dinnerPos == -1)
                        jsonChildObject.put("lunch", tdStr.substring(lunchPos, tdStr.length() - 1));
                    else {
                        jsonChildObject.put("lunch", tdStr.substring(lunchPos, dinnerPos - 1));
                        jsonChildObject.put("dinner", tdStr.substring(dinnerPos, tdStr.length() - 1));
                    }

                    jsonParentObject.put(String.format(Locale.getDefault(), "%02d", Integer.parseInt(tdStr.substring(0, lunchPos - 1))), jsonChildObject);
                }

                Log.d("MEAL", jsonParentObject.toString());

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(mYear + mMonth, jsonParentObject.toString());
                editor.apply();

                sendHandlerHideDialog();
                sendHandlerShowSnackbar(mContext.getString(R.string.download_successfully));

                if(mOnDownloadCompleteListener != null)
                    sendHandlerCallDownloadComplete(mOnDownloadCompleteListener);
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowSnackbar(mContext.getString(R.string.download_failed));
                e.printStackTrace();
            }
        }

        private void sendHandlerShowDialog(String dialogTitle, String dialogMessage, boolean hasPositiveButton, boolean cancelable){
            Bundle data = new Bundle();
            data.putString("title", dialogTitle);
            data.putString("msg", dialogMessage);
            data.putBoolean("hasPositive", hasPositiveButton);
            data.putBoolean("cancelable", cancelable);

            Message msg = new Message();
            msg.setData(data);
            msg.what = MyHandler.SHOW_DIALOG;
            mHandler.sendMessage(msg);
        }

        private void sendHandlerHideDialog(){
            Message msg = new Message();
            msg.what = MyHandler.HIDE_DIALOG;
            mHandler.sendMessage(msg);
        }

        private void sendHandlerCallDownloadComplete(OnDownloadCompleteListener onDownloadCompleteListener){
            Message msg = new Message();
            msg.what = MyHandler.CALL_SCHOOL_MEAL_DOWNLOAD_COMPLETE;
            msg.obj = onDownloadCompleteListener;
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
