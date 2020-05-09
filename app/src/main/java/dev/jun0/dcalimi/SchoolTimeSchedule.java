package dev.jun0.dcalimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SchoolTimeSchedule {
    private Context mContext;
    private View mView;
    private FragmentManager mFragmentManager;
    private static SharedPreferences mSharedPreferences;

    SchoolTimeSchedule(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mContext = view.getContext();
        mView = view;
        mSharedPreferences = mContext.getSharedPreferences("schedule", Context.MODE_PRIVATE);
    }

    public void download(int _grade, int _class, OnDownloadCompleteListener onDownloadCompleteListener){
        Runnable runnable = new SchoolTimeScheduleDownloadRunnable(mFragmentManager, mView, _grade, _class, onDownloadCompleteListener);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public interface OnDownloadCompleteListener {
        void onDownloadComplete();
    }

    private static class SchoolTimeScheduleDownloadRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final int mGrade;
        private final int mClass;
        private final OnDownloadCompleteListener mOnDownloadCompleteListener;

        SchoolTimeScheduleDownloadRunnable(FragmentManager fragmentManager, View view, int _grade, int _class, OnDownloadCompleteListener onDownloadCompleteListener){
            mHandler = new MyHandler(fragmentManager, view);
            mContext = view.getContext();
            mGrade = _grade;
            mClass = _class;
            mOnDownloadCompleteListener = onDownloadCompleteListener;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info), mContext.getString(R.string.downloading_class_total_number), false,false);
                Thread.sleep(500);

                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("https://dc-api.jun0.dev/schedules/" + mGrade + "/" + mClass, 15000);

                Log.d("STS", "run: "+mGrade+"-"+mClass+": "+strResponse);
                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                String downloadedList = mSharedPreferences.getString("list", "[]");
                JSONArray jsonArray = new JSONArray(downloadedList);
                boolean alreadyInList = false;
                for(int i = 0; i < jsonArray.length(); i++){
                    if(jsonArray.getJSONObject(i).getInt("grade") == mGrade
                            && jsonArray.getJSONObject(i).getInt("class") == mClass) {
                        alreadyInList = true;
                        break;
                    }
                }


                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(mGrade +"-"+ mClass, jsonObjectResponse.getJSONArray("schedules").toString());

                if(!alreadyInList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("grade",mGrade);
                    jsonObject.put("class", mClass);

                    jsonArray.put(jsonObject);

                    List<JSONObject> jsonValues = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonValues.add(jsonArray.getJSONObject(i));
                    }

                    Collections.sort(jsonValues, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            int gradeA = 0;
                            int gradeB = 0;
                            int classA = 0;
                            int classB = 0;

                            try {
                                gradeA = a.getInt("grade");
                                gradeB = b.getInt("grade");
                                classA = a.getInt("class");
                                classB = b.getInt("class");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(gradeA > gradeB)
                                return 1;
                            else if(gradeA < gradeB)
                                return -1;
                            else return Integer.compare(classA, classB);
                        }
                    });

                    JSONArray sortedJsonArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        sortedJsonArray.put(jsonValues.get(i));
                    }

                    editor.putString("list", sortedJsonArray.toString());
                }

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

        private void sendHandlerCallDownloadComplete(OnDownloadCompleteListener onDownloadCompleteListener){
            Message msg = new Message();
            msg.what = MyHandler.CALL_SCHOOL_SCHEDULE_DOWNLOAD_COMPLETE;
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
