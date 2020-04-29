package me.tyoj.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class SchoolEvent {
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final ArrayList<SchoolEventListItem> mCalenderListItems = new ArrayList<>();
    private static SharedPreferences pref;

    SchoolEvent(FragmentManager fragmentManager, Context context){
        mFragmentManager = fragmentManager;
        mContext = context;
        pref = mContext.getSharedPreferences("event", MODE_PRIVATE);
    }

    public ArrayList<SchoolEventListItem> getList(String year, String month) {
        String strJSONEvent = pref.getString(year + month,null);
        if(strJSONEvent == null)
            return mCalenderListItems;
        try {
            JSONArray jsonArray = new JSONArray(strJSONEvent);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String strDate = jsonObject.getString("date");
                String strTitle = jsonObject.getString("title");

                SchoolEventListItem schoolEventListItem = new SchoolEventListItem();
                schoolEventListItem.setDate(jsonObject.getString("date"));
                int pos = mCalenderListItems.indexOf(schoolEventListItem);
                if(pos != -1)
                    mCalenderListItems.get(pos).setEvent(mCalenderListItems.get(pos).getEvent() + "/" + strTitle);
                else
                    addEventItem(strDate, getDay(strDate), strTitle);

                Log.i("SE", strDate + ": " + pos);
            }
            return mCalenderListItems;
        } catch (JSONException e) {
            e.printStackTrace();
            return mCalenderListItems;
        }
    }

    public Boolean hasList(String year, String month){
        return (pref.getString(year + month,null) != null);
    }

    public void download(String year, String month){
        int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
        if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            DialogFragment dialogFragment = new MyDialogFragment(mContext.getString(R.string.error), "네트워크에 연결할 수 없습니다.\n연결 상태를 확인 후 재시도 해 주시기 바랍니다.", true);
            dialogFragment.show(mFragmentManager, "Network Error");
            return;
        }

        Runnable runnable = new SchoolEventDownloadRunnable(mFragmentManager, mContext, year, month);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private String getDay(String date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, Integer.parseInt(date));

        String strWeek = null;
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = "일요일";
        } else if (nWeek == 2) {
            strWeek = "월요일";
        } else if (nWeek == 3) {
            strWeek = "화요일";
        } else if (nWeek == 4) {
            strWeek = "수요일";
        } else if (nWeek == 5) {
            strWeek = "목요일";
        } else if (nWeek == 6) {
            strWeek = "금요일";
        } else if (nWeek == 7) {
            strWeek = "토요일";
        }

        return strWeek;
    }


    private void addEventItem(String date, String day, String event) {
        SchoolEventListItem item = new SchoolEventListItem();

        item.setDate(date);
        item.setDay(day);
        item.setEvent(event);

        mCalenderListItems.add(item);
    }

    private static class SchoolEventDownloadRunnable implements Runnable {
        private final Handler mHandler;
        private final String mYear;
        private final String mMonth;

        SchoolEventDownloadRunnable(FragmentManager fragmentManager, Context context, String year, String month){
            mHandler = new MyHandler(fragmentManager, context);
            mYear = year;
            mMonth = month;
        }

        @Override
        public void run() {
            mHandler.sendEmptyMessage(MyHandler.SHOW_DIALOG_DOWNLOADING_SCHOOL_EVENT);

            try {
                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("http://school.busanedu.net/dongcheon-h/sv/schdulView/selectSvList.do?sysId=dongcheon-h&monthFirst="+mYear+"/"+mMonth+"/01&monthEnmt="+mYear+"/"+mMonth+"/31", 15000);

                JSONArray jsonArrayResponse = new JSONArray(strResponse);
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < jsonArrayResponse.length(); i++) {
                    if(jsonArrayResponse.getJSONObject(i).has("sysId") && jsonArrayResponse.getJSONObject(i).getString("sysId").equals("dongcheon-h")) {
                        String strDate = jsonArrayResponse.getJSONObject(i).getString("bgnde");
                        strDate = strDate.substring(strDate.length() - 2);
                        String strTitle = jsonArrayResponse.getJSONObject(i).getString("schdulTitle");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("date", strDate);
                        jsonObject.put("title", strTitle);

                        jsonArray.put(jsonObject);
                    }
                }
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(mYear + mMonth, jsonArray.toString());
                editor.apply();

                Log.d("TAG", "doInBackground: " + jsonArray.toString());

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
