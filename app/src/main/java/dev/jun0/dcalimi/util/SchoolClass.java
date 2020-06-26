package dev.jun0.dcalimi.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.json.JSONObject;

import java.io.IOException;

import dev.jun0.dcalimi.R;

public class SchoolClass {
    private View mView;
    private final FragmentManager mFragmentManager;

    public SchoolClass(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mView = view;
    }

    public void downloadTotalNumber(int grade, OnDownloadTotalNumberCompleteListener onDownloadTotalNumberCompleteListener){
        Runnable runnable = new SchoolClassTotalNumberDownloadRunnable(mFragmentManager, mView, grade, onDownloadTotalNumberCompleteListener);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public interface OnDownloadTotalNumberCompleteListener {
        void onDownloadComplete(int count);
    }

    private static class SchoolClassTotalNumberDownloadRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final int mGrade;
        private final OnDownloadTotalNumberCompleteListener mOnDownloadTotalNumberCompleteListener;

        SchoolClassTotalNumberDownloadRunnable(FragmentManager fragmentManager, View view, int grade, OnDownloadTotalNumberCompleteListener onDownloadTotalNumberCompleteListener){
            mHandler = new MyHandler(fragmentManager, view);
            mContext = view.getContext();
            mGrade = grade;
            mOnDownloadTotalNumberCompleteListener = onDownloadTotalNumberCompleteListener;
        }

        @Override
        public void run() {
            try {
                if(mGrade < 1 || mGrade > 3)
                    throw new IllegalArgumentException();

                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info), mContext.getString(R.string.downloading_class_total_number), false,false);
                Thread.sleep(500);

                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("https://dc-api.jun0.dev/classes/count/" + mGrade, 15000);

                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                int totalNum = jsonObjectResponse.getInt("classCount");
                if(totalNum <= 0)
                    throw new IOException();

                sendHandlerHideDialog();
                if(mOnDownloadTotalNumberCompleteListener != null)
                    sendHandlerCallDownloadComplete(totalNum, mOnDownloadTotalNumberCompleteListener);
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

        private void sendHandlerCallDownloadComplete(int totalNum, OnDownloadTotalNumberCompleteListener onDownloadCompleteListener){
            Message msg = new Message();
            msg.what = MyHandler.CALL_SCHOOL_CLASS_TOTAL_NUM_DOWNLOAD_COMPLETE;
            msg.arg1 = totalNum;
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
