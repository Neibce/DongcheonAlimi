package dev.jun0.dcalimi.util;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.jun0.dcalimi.item.PostItem;

public class Post {
    public static final int NOTICE = 0;
    public static final int SUGGESTION = 1;

    public static void getList(int postType, int lastPostId, OnListDownloadCompleteListener onDownloadCompleteListener){
        new DownloadPostListAsyncTask(onDownloadCompleteListener).execute(postType, lastPostId);
    }

    public static void getBody(int postId, OnBodyDownloadCompleteListener onBodyDownloadCompleteListener){
        new DownloadPostBodyAsyncTask(onBodyDownloadCompleteListener).execute(postId);
    }


    public interface OnListDownloadCompleteListener {
        void onDownloadComplete(List<PostItem> result, boolean isLast);
        void onDownloadFailed();
    }

    public interface OnBodyDownloadCompleteListener {
        void onDownloadComplete(String result);
        void onDownloadFailed();
    }

    private static class DownloadPostListAsyncTask extends AsyncTask<Integer, Void, Pair<List<PostItem>, Boolean>> {
        OnListDownloadCompleteListener mOnListDownloadCompleteListener;

        DownloadPostListAsyncTask(OnListDownloadCompleteListener onListDownloadCompleteListener) {
            mOnListDownloadCompleteListener = onListDownloadCompleteListener;
        }
        @Override
        protected Pair<List<PostItem>, Boolean> doInBackground(Integer... integers) {
            try {
                String strResponse = new RequestHttpURLConnection().get("https://dc-api.jun0.dev/board/list?type=" + integers[0] + "&lastPostId=" + integers[1], 10000);
                JSONObject jsonObject = new JSONObject(strResponse);
                JSONArray jsonArray = jsonObject.getJSONArray("posts");
                boolean isLast = jsonObject.getBoolean("isLast");

                List<PostItem> postItemList = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject postJSONObject = jsonArray.getJSONObject(i);
                    int postId = postJSONObject.getInt("id");
                    String postTitle = postJSONObject.getString("title");
                    String postUploader = postJSONObject.getString("uploader");
                    String postCreatedAt = postJSONObject.getString("createdAt");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    postCreatedAt = formatTimeString(sdf.parse(postCreatedAt).getTime());

                    PostItem postItem = new PostItem();
                    postItem.set(postId, postTitle, postUploader, postCreatedAt);

                    postItemList.add(postItem);
                }
                return Pair.create(postItemList, isLast);
            } catch (IOException | NetworkErrorException | JSONException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static class TIME_MAXIMUM{
            private static final int SEC = 60;
            private static final int MIN = 60;
            private static final int HOUR = 24;
            private static final int DAY = 30;
            private static final int MONTH = 12;
        }

        private static String formatTimeString(long regTime) {
            long curTime = System.currentTimeMillis() - 32400000;
            long diffTime = (curTime - regTime) / 1000;
            String msg = null;
            if (diffTime < TIME_MAXIMUM.SEC) {
                msg = "방금 전";
            } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
                msg = diffTime + "분 전";
            } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
                msg = (diffTime) + "시간 전";
            } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
                msg = (diffTime) + "일 전";
            } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
                msg = (diffTime) + "달 전";
            } else {
                msg = (diffTime) + "년 전";
            }
            return msg;
        }

        @Override
        protected void onPostExecute(Pair<List<PostItem>, Boolean> resultPair) {
            if(resultPair != null && resultPair.first != null && resultPair.second != null)
                mOnListDownloadCompleteListener.onDownloadComplete(resultPair.first, resultPair.second) ;
            else
                mOnListDownloadCompleteListener.onDownloadFailed();
        }
    }

    private static class DownloadPostBodyAsyncTask extends AsyncTask<Integer, Void, String> {
        OnBodyDownloadCompleteListener mOnBodyDownloadCompleteListener;

        DownloadPostBodyAsyncTask(OnBodyDownloadCompleteListener onBodyDownloadCompleteListener) {
            mOnBodyDownloadCompleteListener = onBodyDownloadCompleteListener;
        }
        @Override
        protected String doInBackground(Integer... integers) {
            try {
                String strResponse = new RequestHttpURLConnection().get("https://dc-api.jun0.dev/board/" + integers[0], 10000);
                JSONObject jsonObject = new JSONObject(strResponse);
                return jsonObject.getString("body");
            } catch (IOException | NetworkErrorException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null)
                mOnBodyDownloadCompleteListener.onDownloadComplete(result);
            else
                mOnBodyDownloadCompleteListener.onDownloadFailed();
        }
    }
}