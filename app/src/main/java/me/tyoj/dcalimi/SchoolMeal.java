package me.tyoj.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Locale;

public class SchoolMeal {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private static SharedPreferences pref;

   SchoolMeal(FragmentManager fragmentManager, Context context){
        mFragmentManager = fragmentManager;
        mContext = context;
        pref = mContext.getSharedPreferences("meal", Context.MODE_PRIVATE);
   }

    public String get(String year, String month, String date, boolean dinner) throws NullPointerException{
        String strJSONMeal = pref.getString(year + month,null);
        if (strJSONMeal == null)
            return mContext.getString(R.string.meal_need_download);
        try {
            JSONObject jsonObject = new JSONObject(strJSONMeal);

            if(!jsonObject.has(date))
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
        return (pref.getString(year + month,null) != null);
    }

    public void download(String year, String month){
        int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
        if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            DialogFragment dialogFragment = new MyDialogFragment("WRN", "네트워크에 연결할 수 없습니다.\n연결 상태를 확인 후 재시도 해 주시기 바랍니다.", true);
            dialogFragment.show(mFragmentManager, "Network Error");
            return;
        }

        Runnable runnable = new SchoolMealDownloadRunnable(mFragmentManager, year, month);
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private static class SchoolMealDownloadRunnable implements Runnable {
       private Handler mHandler;
       private String mYear;
       private String mMonth;

       SchoolMealDownloadRunnable(FragmentManager fragmentManager, String year, String month){
            mHandler = new MyHandler(fragmentManager);
            mYear = year;
            mMonth = month;
       }

       @Override
        public void run() {
            mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADING_SCHOOL_MEAL);
            try {
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

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(mYear + mMonth, jsonParentObject.toString());
                editor.apply();

                mHandler.sendEmptyMessage(MyHandler.HIDE_DIALOG);
                mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADED_SUCCESSFULLY);
            } catch (IOException | JSONException e) {
                mHandler.sendEmptyMessage(MyHandler.HIDE_DIALOG);
                mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADING_FAILED);
                e.printStackTrace();
            }
        }
    }
}
