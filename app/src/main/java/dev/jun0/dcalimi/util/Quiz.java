package dev.jun0.dcalimi.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.item.QuizItem;

public class Quiz {
    private Context mContext;
    private final FragmentManager mFragmentManager;

    public Quiz(FragmentManager fragmentManager, Context context){
        mFragmentManager = fragmentManager;
        mContext = context;
    }

    public void get(final OnDownloadCompleteListener onDownloadCompleteListener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            onDownloadCompleteListener.onDownloadFailed();
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Runnable runnable = new GetQuizRunnable(mFragmentManager, mContext, fcmToken, onDownloadCompleteListener);
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                });
    }

    public void checkAnswer(final int answerNumber, final OnAnswerCheckCompleteListener onAnswerCheckCompleteListener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            onAnswerCheckCompleteListener.onCheckFailed();
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Runnable runnable = new CheckAnswerRunnable(mFragmentManager, mContext, fcmToken, answerNumber, onAnswerCheckCompleteListener);
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                });
    }

    public interface OnDownloadCompleteListener {
        void onDownloadComplete(QuizItem quizItem);
        void onAttemptExceeded();
        void onDownloadFailed();
    }

    public interface OnAnswerCheckCompleteListener {
        void onAnswerCorrect();
        void onAnswerIncorrect(int remainingAttempts);
        void onCheckFailed();
    }

    private static class GetQuizRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final String mFcmToken;
        private final OnDownloadCompleteListener mOnDownloadCompleteListener;

        GetQuizRunnable(FragmentManager fragmentManager, Context context, String fcmToken, OnDownloadCompleteListener onDownloadTotalNumberCompleteListener){
            mHandler = new MyHandler(fragmentManager);
            mContext = context;
            mFcmToken = fcmToken;
            mOnDownloadCompleteListener = onDownloadTotalNumberCompleteListener;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info),mContext.getString(R.string.getting_quiz), false,false);

                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.get("https://dc-api.jun0.dev/quiz/new?fcmToken=" + mFcmToken, 15000);

                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                int resultCode = jsonObjectResponse.getInt("resultCode");
                if(resultCode != 0)
                    throw new IOException();

                boolean isAttemptExceeded = jsonObjectResponse.getBoolean("isAttemptExceeded");
                if(isAttemptExceeded) {
                    sendHandlerHideDialog();
                    if (mOnDownloadCompleteListener != null)
                        sendHandlerCallDownloadComplete(true, mOnDownloadCompleteListener);
                }else {
                    String strQuestion = jsonObjectResponse.getString("question");
                    String[] strOptions = new String[5];

                    JSONArray jsonArrayOptions = jsonObjectResponse.getJSONArray("options");
                    for (int i = 0; i < jsonArrayOptions.length(); i++) {
                        strOptions[i] = jsonArrayOptions.getString(i);
                    }

                    sendHandlerHideDialog();
                    if (mOnDownloadCompleteListener != null)
                        sendHandlerCallDownloadComplete(new QuizItem(strQuestion, strOptions), false, mOnDownloadCompleteListener);
                }
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowDialog(mContext.getString(R.string.error), mContext.getString(R.string.error_in_get_quiz), true,true);
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

        private void sendHandlerCallDownloadComplete(boolean attemptExceeded, OnDownloadCompleteListener onDownloadCompleteListener){
            sendHandlerCallDownloadComplete(null, attemptExceeded, onDownloadCompleteListener);
        }

        private void sendHandlerCallDownloadComplete(QuizItem quizItem, boolean isAttemptExceeded, OnDownloadCompleteListener onDownloadCompleteListener){
            Bundle data = new Bundle();
            if(quizItem != null)
                data.putSerializable("quizItem", quizItem);
            data.putBoolean("isAttemptExceeded", isAttemptExceeded);

            Message msg = new Message();
            msg.setData(data);
            msg.what = MyHandler.CALL_QUIZ_DOWNLOAD_COMPLETE;
            msg.obj = onDownloadCompleteListener;

            mHandler.sendMessage(msg);
        }
    }

    private static class CheckAnswerRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final String mFcmToken;
        private final int mAnswerNumber;
        private final OnAnswerCheckCompleteListener mOnAnswerCheckCompleteListener;

        CheckAnswerRunnable(FragmentManager fragmentManager, Context context, String fcmToken, int answerNumber, OnAnswerCheckCompleteListener onAnswerCheckCompleteListener){
            mHandler = new MyHandler(fragmentManager);
            mContext = context;
            mFcmToken = fcmToken;
            mAnswerNumber = answerNumber;
            mOnAnswerCheckCompleteListener = onAnswerCheckCompleteListener;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info), mContext.getString(R.string.checking_quiz_answer), false,false);

                RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
                String strResponse = requestHttpURLConnection.post("https://dc-api.jun0.dev/quiz/check-answer?fcmToken=" + mFcmToken + "&answerNumber=" + mAnswerNumber, 15000);

                JSONObject jsonObjectResponse = new JSONObject(strResponse);

                int resultCode = jsonObjectResponse.getInt("resultCode");
                if(resultCode != 0)
                    throw new IOException();

                boolean isAnswerCorrect = jsonObjectResponse.getBoolean("isAnswerCorrect");

                if(isAnswerCorrect) {
                    sendHandlerHideDialog();
                    if (mOnAnswerCheckCompleteListener != null)
                        sendHandlerCallDownloadComplete(true, mOnAnswerCheckCompleteListener);
                }else{
                    int remainingAttempts = jsonObjectResponse.getInt("remainingAttempts");

                    sendHandlerHideDialog();
                    if (mOnAnswerCheckCompleteListener != null)
                        sendHandlerCallDownloadComplete(false, remainingAttempts, mOnAnswerCheckCompleteListener);
                }
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowDialog(mContext.getString(R.string.error), mContext.getString(R.string.error_in_get_quiz), true,true);
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

        private void sendHandlerCallDownloadComplete(boolean isAnswerCorrect, OnAnswerCheckCompleteListener onAnswerCheckCompleteListener){
            Bundle data = new Bundle();
            data.putBoolean("isAnswerCorrect", isAnswerCorrect);

            Message msg = new Message();
            msg.setData(data);
            msg.what = MyHandler.CALL_QUIZ_ANSWER_CHECK_COMPLETE;
            msg.obj = onAnswerCheckCompleteListener;

            mHandler.sendMessage(msg);
        }

        private void sendHandlerCallDownloadComplete(boolean isAnswerCorrect, int remainingAttempts, OnAnswerCheckCompleteListener onAnswerCheckCompleteListener){
            Bundle data = new Bundle();
            data.putBoolean("isAnswerCorrect", isAnswerCorrect);
            data.putInt("remainingAttempts", remainingAttempts);

            Message msg = new Message();
            msg.setData(data);
            msg.what = MyHandler.CALL_QUIZ_ANSWER_CHECK_COMPLETE;
            msg.obj = onAnswerCheckCompleteListener;

            mHandler.sendMessage(msg);
        }
    }
}
