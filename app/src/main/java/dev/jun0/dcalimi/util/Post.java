package dev.jun0.dcalimi.util;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.item.PostCommentItem;
import dev.jun0.dcalimi.item.PostItem;

public class Post {
    public static final int NOTICE = 0;
    public static final int SUGGESTION = 1;

    public static void getList(int postType, int lastPostId, OnListDownloadCompleteListener onDownloadCompleteListener){
        new DownloadPostListAsyncTask(onDownloadCompleteListener).execute(postType, lastPostId);
    }

    public static void getBody(final int postId, final OnBodyDownloadCompleteListener onBodyDownloadCompleteListener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String fcmToken = task.getResult().getToken();
                        new DownloadPostBodyAsyncTask(fcmToken, onBodyDownloadCompleteListener).execute(postId);
                    }
                });
    }

    public static void getComments(final int postId, final OnRequestCommentsCompleteListener onRequestCommentsCompleteListener){
        new RequestCommentsAsyncTask(onRequestCommentsCompleteListener).execute(postId);
    }

    public static void upload(final FragmentManager fragmentManager, final Context context, final String manageToken,
                              final int postType, final String title, final String uploader, final String body, final Bitmap bitmap1, final Bitmap bitmap2,
                              final OnPostUploadCompleteListener onPostUploadCompleteListener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Runnable runnable = new UploadPostRunnable(fragmentManager, context, fcmToken, manageToken,
                                postType, title, uploader, body, bitmap1, bitmap2,
                                onPostUploadCompleteListener);
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                });
    }

    public static void delete(final FragmentManager fragmentManager, final Context context, final String manageToken, final int postId,
                              final OnPostDeleteCompleteListener onPostDeleteCompleteListener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Runnable runnable = new DeletePostRunnable(fragmentManager, context, fcmToken, manageToken, postId, onPostDeleteCompleteListener);
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                });
    }

    private static class TIME_MAXIMUM {
        private static final int SEC = 60;
        private static final int MIN = 60;
        private static final int HOUR = 24;
        private static final int DAY = 30;
        private static final int MONTH = 12;
    }

    private static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis() - 32400000;
        long diffTime = (curTime - regTime) / 1000;
        String msg;
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

    public interface OnListDownloadCompleteListener {
        void onDownloadComplete(List<PostItem> result, boolean isLast);
        void onDownloadFailed();
    }

    public interface OnBodyDownloadCompleteListener {
        void onDownloadComplete(String body, int views, boolean isOwner, String imageUrl1, String imageUrl2);
        void onDownloadFailed();
    }

    public interface OnRequestCommentsCompleteListener {
        void onComplete(List<PostCommentItem> result);
        void onFailed();
    }

    public interface OnPostUploadCompleteListener {
        void onUploadComplete();
    }

    public interface OnPostDeleteCompleteListener {
        void onDeleteComplete();
        void onDeleteFailed();
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

        @Override
        protected void onPostExecute(Pair<List<PostItem>, Boolean> resultPair) {
            if(resultPair != null && resultPair.first != null && resultPair.second != null)
                mOnListDownloadCompleteListener.onDownloadComplete(resultPair.first, resultPair.second) ;
            else
                mOnListDownloadCompleteListener.onDownloadFailed();
        }
    }

    private static class DownloadPostBodyAsyncTask extends AsyncTask<Integer, Void, JSONObject> {
        private String mFcmToken;
        private OnBodyDownloadCompleteListener mOnBodyDownloadCompleteListener;

        DownloadPostBodyAsyncTask(String fcmToken, OnBodyDownloadCompleteListener onBodyDownloadCompleteListener) {
            mFcmToken = fcmToken;
            mOnBodyDownloadCompleteListener = onBodyDownloadCompleteListener;
        }
        @Override
        protected JSONObject doInBackground(Integer... integers) {
            try {
                String strResponse = new RequestHttpURLConnection().get("https://dc-api.jun0.dev/board/" + integers[0] + "?fcmToken=" + mFcmToken, 10000);
                return new JSONObject(strResponse);
            } catch (IOException | NetworkErrorException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null) {
                try {
                    String imageName1 = null;
                    String imageName2 = null;
                    if(jsonObject.has("image1"))
                        imageName1 = jsonObject.getString("image1");
                    if(jsonObject.has("image2"))
                        imageName2 = jsonObject.getString("image2");

                    mOnBodyDownloadCompleteListener.onDownloadComplete(jsonObject.getString("body"), jsonObject.getInt("views"), jsonObject.getBoolean("isOwner"), imageName1, imageName2);
                } catch (JSONException e) {
                    mOnBodyDownloadCompleteListener.onDownloadFailed();
                }
            }else
                mOnBodyDownloadCompleteListener.onDownloadFailed();
        }
    }

    private static class RequestCommentsAsyncTask extends AsyncTask<Integer, Void, List<PostCommentItem>> {
        OnRequestCommentsCompleteListener mOnRequestCommentsCompleteListener;
        RequestCommentsAsyncTask(OnRequestCommentsCompleteListener onRequestCommentsCompleteListener) {
            mOnRequestCommentsCompleteListener = onRequestCommentsCompleteListener;
        }
        @Override
        protected List<PostCommentItem> doInBackground(Integer... integers) {
            try {
                String strResponse = new RequestHttpURLConnection().get("https://dc-api.jun0.dev/board/" + integers[0] + "/comments", 10000);
                JSONObject jsonObject = new JSONObject(strResponse);
                JSONArray jsonArray = jsonObject.getJSONArray("comments");

                List<PostCommentItem> postCommentItems = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectComment = jsonArray.getJSONObject(i);

                    String commentCreatedAt = jsonObjectComment.getString("createdAt");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    commentCreatedAt = formatTimeString(sdf.parse(commentCreatedAt).getTime());

                    postCommentItems.add(new PostCommentItem(commentCreatedAt, jsonObjectComment.getString("uploader"), jsonObjectComment.getString("body")));
                }
                return postCommentItems;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<PostCommentItem> postCommentItems) {
            if(postCommentItems == null)
                mOnRequestCommentsCompleteListener.onFailed();
            else
                mOnRequestCommentsCompleteListener.onComplete(postCommentItems);
        }
    }

    private static class UploadPostRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final String mFcmToken;
        private final String mManagerToken;
        private final int mPostType;
        private final String mStrTitle;
        private final String mStrUploader;
        private final String mStrBody;
        private final Bitmap mBitmap1;
        private final Bitmap mBitmap2;
        private final OnPostUploadCompleteListener mOnPostUploadCompleteListener;

        UploadPostRunnable(FragmentManager fragmentManager, Context context, String fcmToken, String managerToken,
                           int postType, String title, String uploader, String body, Bitmap bitmap1, Bitmap bitmap2,
                           OnPostUploadCompleteListener onPostUploadCompleteListener){
            mHandler = new MyHandler(fragmentManager);
            mContext = context;
            mFcmToken = fcmToken;
            mManagerToken = managerToken;
            mPostType = postType;
            mStrTitle = title;
            mStrUploader = uploader;
            mStrBody = body;
            mBitmap1 = bitmap1;
            mBitmap2 = bitmap2;
            mOnPostUploadCompleteListener = onPostUploadCompleteListener;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info), mContext.getString(R.string.uploading_post), false,false);

                final String boundary = "9651385161650";
                final String crlf = "\r\n";
                final String twoHyphens = "--";

                URL url = new URL("https://dc-api.jun0.dev/board/new?fcmToken=" + mFcmToken + "&type=" + mPostType);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setConnectTimeout(20000);
                urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                OutputStream httpConnOutputStream = urlConn.getOutputStream();
                DataOutputStream request = new DataOutputStream(httpConnOutputStream);

                if(mPostType == NOTICE){
                    if(mManagerToken == null)
                        throw new IOException();

                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"manageToken\""+ crlf);
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                    request.writeBytes(crlf);
                    request.write(mManagerToken.getBytes(StandardCharsets.UTF_8));
                    request.writeBytes(crlf);
                }

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"title\""+ crlf);
                request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                request.writeBytes(crlf);
                request.write(mStrTitle.getBytes(StandardCharsets.UTF_8));
                request.writeBytes(crlf);

                if(mStrUploader != null) {
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"uploader\"" + crlf);
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                    request.writeBytes(crlf);
                    request.write(mStrUploader.getBytes(StandardCharsets.UTF_8));
                    request.writeBytes(crlf);
                }

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"body\""+ crlf);
                request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                request.writeBytes(crlf);
                request.write(mStrBody.getBytes(StandardCharsets.UTF_8));
                request.writeBytes(crlf);

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"deviceModel\""+ crlf);
                request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                request.writeBytes(crlf);
                request.write(Build.MANUFACTURER.getBytes(StandardCharsets.UTF_8));
                request.writeBytes(" ");
                request.write(Build.MODEL.getBytes(StandardCharsets.UTF_8));
                request.writeBytes(crlf);

                if(mBitmap1 != null) {
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"bitmap1.jpeg\"" + crlf);
                    request.writeBytes("Content-Type: image/jpeg" + crlf);
                    request.writeBytes(crlf);
                    request.write(bitmapToByteArray(mBitmap1));
                    request.writeBytes(crlf);
                }

                if(mBitmap2 != null) {
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"bitmap2.jpeg\"" + crlf);
                    request.writeBytes("Content-Type: image/jpeg" + crlf);
                    request.writeBytes(crlf);
                    request.write(bitmapToByteArray(mBitmap2));
                    request.writeBytes(crlf);
                }

                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                request.flush();
                request.close();

                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    throw new NetworkErrorException();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));
                String strLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((strLine = reader.readLine()) != null) {
                    stringBuilder.append(strLine);
                }

                JSONObject jsonObjectResponse = new JSONObject(stringBuilder.toString());

                int resultCode = jsonObjectResponse.getInt("resultCode");
                if(resultCode != 0)
                    throw new IOException();

                sendHandlerHideDialog();
                if (mOnPostUploadCompleteListener != null)
                    sendHandlerCallDownloadComplete(mOnPostUploadCompleteListener);
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowDialog(mContext.getString(R.string.error), mContext.getString(R.string.error_in_get_quiz), true,true);
                e.printStackTrace();
            }
        }

        public byte[] bitmapToByteArray( Bitmap bitmap ) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) ;
            return stream.toByteArray();
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

        private void sendHandlerCallDownloadComplete(OnPostUploadCompleteListener onPostUploadCompleteListener){
            Message msg = new Message();
            msg.what = MyHandler.CALL_POST_UPLOAD_COMPLETE;
            msg.obj = onPostUploadCompleteListener;

            mHandler.sendMessage(msg);
        }
    }

    private static class DeletePostRunnable implements Runnable {
        private final Handler mHandler;
        private final Context mContext;
        private final String mFcmToken;
        private final String mManagerToken;
        private final int mPostId;
        private final OnPostDeleteCompleteListener mOnPostDeleteCompleteListener;

        DeletePostRunnable(FragmentManager fragmentManager, Context context, String fcmToken, String managerToken,
                           int postId, OnPostDeleteCompleteListener onPostDeleteCompleteListener){
            mHandler = new MyHandler(fragmentManager);
            mContext = context;
            mFcmToken = fcmToken;
            mManagerToken = managerToken;
            mPostId = postId;
            mOnPostDeleteCompleteListener = onPostDeleteCompleteListener;
        }

        @Override
        public void run() {
            try {
                int connectivityStatus = NetworkStatus.getConnectivityStatus(mContext);
                if(connectivityStatus == NetworkStatus.TYPE_NOT_CONNECTED)
                    throw new Exception();

                sendHandlerShowDialog(mContext.getString(R.string.info), "게시글 삭제 중...", false,false);

                final String boundary = "9651385161650";
                final String crlf = "\r\n";
                final String twoHyphens = "--";

                URL url = new URL("https://dc-api.jun0.dev/board/" + mPostId + "/delete?fcmToken=" + mFcmToken);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setConnectTimeout(20000);
                urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                OutputStream httpConnOutputStream = urlConn.getOutputStream();
                DataOutputStream request = new DataOutputStream(httpConnOutputStream);

                if(mManagerToken != null) {
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"manageToken\"" + crlf);
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
                    request.writeBytes(crlf);
                    request.write(mManagerToken.getBytes(StandardCharsets.UTF_8));
                    request.writeBytes(crlf);
                }

                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                request.flush();
                request.close();

                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    throw new NetworkErrorException();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));
                String strLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((strLine = reader.readLine()) != null) {
                    stringBuilder.append(strLine);
                }

                JSONObject jsonObjectResponse = new JSONObject(stringBuilder.toString());

                int resultCode = jsonObjectResponse.getInt("resultCode");
                if(resultCode != 0)
                    throw new IOException();

                sendHandlerHideDialog();
                if (mOnPostDeleteCompleteListener != null)
                    sendHandlerCallDownloadComplete(mOnPostDeleteCompleteListener);
            } catch (Exception e) {
                sendHandlerHideDialog();
                sendHandlerShowDialog(mContext.getString(R.string.error), "게시글 삭제 중 오류가 발생하였습니다.", true,true);
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

        private void sendHandlerCallDownloadComplete(OnPostDeleteCompleteListener onPostDeleteCompleteListener){
            Message msg = new Message();
            msg.what = MyHandler.CALL_POST_DELETE_COMPLETE;
            msg.obj = onPostDeleteCompleteListener;

            mHandler.sendMessage(msg);
        }
    }
}