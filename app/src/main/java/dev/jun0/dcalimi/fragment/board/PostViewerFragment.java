package dev.jun0.dcalimi.fragment.board;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.adapter.PostCommentRecyclerAdapter;
import dev.jun0.dcalimi.item.PostCommentItem;
import dev.jun0.dcalimi.util.Post;
import dev.jun0.dcalimi.view.PostImageViewer;

public class PostViewerFragment extends Fragment {
    private PostCommentRecyclerAdapter mPostCommentRecyclerAdapter;
    private List<PostCommentItem> mPostCommentList = new ArrayList<>();

    public static PostViewerFragment newInstance() {
        PostViewerFragment fragment = new PostViewerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Fragment currentBoardFragment = getTargetFragment();
        Bundle bundle = getArguments();

        int postId = 0;
        String transCardName = "";
        String title = "";
        String uploader = "";
        String uploadDate = "";

        if (bundle != null) {
            postId = bundle.getInt("POST_ID");
            transCardName = bundle.getString("TRANS_CARD_NAME");
            title = bundle.getString("TITLE");
            uploader = bundle.getString("UPLOADER");
            uploadDate = bundle.getString("UPLOAD_DATE");
        }

        final View view = inflater.inflate(R.layout.fragment_post_viewer, container, false);
        final Context context = view.getContext();

        SharedPreferences preferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        final String manageToken = preferenceSharedPreferences.getString("enterCode" , null);

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.setTransitionName(transCardName);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvUploader = view.findViewById(R.id.tvUploader);
        tvUploader.setText(uploader);

        TextView tvUploadDate = view.findViewById(R.id.tvUploadDate);
        tvUploadDate.setText(uploadDate);

        final ImageButton imageButtonDeletePost = view.findViewById(R.id.imageButtonDeletePost);
        imageButtonDeletePost.setVisibility(View.GONE);

        final int finalPostId = postId;

        final PostImageViewer postImageViewer1 = view.findViewById(R.id.postImageViewer1);
        final PostImageViewer postImageViewer2 = view.findViewById(R.id.postImageViewer2);

        final ProgressBar progressBar = view.findViewById(R.id.postProgressBar);

        final TextView tvBody = view.findViewById(R.id.tvBody);
        final TextView tvViewCount = view.findViewById(R.id.tvViewCount);
        tvViewCount.setVisibility(View.GONE);

        Post.getBody(postId, new Post.OnBodyDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(String body, int views, final boolean isOwner, String imageName1, String imageName2) {
                tvBody.setText(body);
                tvViewCount.setText(String.format(Locale.getDefault(), "%d", views));
                tvViewCount.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                if(isOwner || manageToken != null){
                    imageButtonDeletePost.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.info);
                            if(isOwner)
                                builder.setMessage("정말로 이 게시글을 삭제하시겠습니까?");
                            else
                                builder.setMessage("관리자 권한으로 이 게시글을 삭제하시겠습니까?");
                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Post.delete(getChildFragmentManager(), context, manageToken, finalPostId, new Post.OnPostDeleteCompleteListener(){
                                        @Override
                                        public void onDeleteComplete() {
                                            Toast.makeText(context, "정상적으로 삭제되었습니다.", Toast.LENGTH_LONG).show();
                                            refreshCurrentBoardPostList(currentBoardFragment);
                                            getParentFragmentManager().popBackStack();
                                        }

                                        @Override
                                        public void onDeleteFailed() {

                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, null);
                            builder.show();
                        }
                    });

                    imageButtonDeletePost.setVisibility(View.VISIBLE);
                }

                if (imageName1 != null && !imageName1.equals("null")) {
                    postImageViewer1.showProgressBar();
                    new ImageDownloadAsyncTask(new OnImageDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete(Drawable drawable) {
                            postImageViewer1.setImageDrawable(drawable);
                        }

                        @Override
                        public void onDownloadFailed() {

                        }
                    }).execute(imageName1);
                }
                if (imageName2 != null && !imageName2.equals("null")) {
                    postImageViewer2.showProgressBar();
                    new ImageDownloadAsyncTask(new OnImageDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete(Drawable drawable) {
                            postImageViewer2.setImageDrawable(drawable);
                        }

                        @Override
                        public void onDownloadFailed() {

                        }
                    }).execute(imageName2);
                }
            }

            @Override
            public void onDownloadFailed() {
                Toast.makeText(context, "삭제되었거나 존재하지 않는 게시글입니다.", Toast.LENGTH_LONG).show();
                refreshCurrentBoardPostList(currentBoardFragment);
                getParentFragmentManager().popBackStack();
            }
        });

        mPostCommentRecyclerAdapter = new PostCommentRecyclerAdapter(mPostCommentList);
        RecyclerView recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewComments.setAdapter(mPostCommentRecyclerAdapter);

        mPostCommentList.add(new PostCommentItem(0, "적극 수렴 후 검토하겠습니다.", "관리자", "2일 전"));
        mPostCommentList.add(new PostCommentItem(0, "거절되었습니다.", "관리자", "2일 전"));
        mPostCommentRecyclerAdapter.notifyDataSetChanged();


        return view;
    }

    private void refreshCurrentBoardPostList(Fragment fragment){
        if(fragment instanceof NoticeFragment)
            ((NoticeFragment)fragment).refreshPostList();
        else
            ((SuggestionFragment)fragment).refreshPostList();
    }

    public interface OnImageDownloadCompleteListener {
        void onDownloadComplete(Drawable drawable);
        void onDownloadFailed();
    }

    private static class ImageDownloadAsyncTask extends AsyncTask<String, Void, Drawable> {
        OnImageDownloadCompleteListener mOnImageDownloadCompleteListener;
        ImageDownloadAsyncTask(OnImageDownloadCompleteListener onImageDownloadCompleteListener) {
            mOnImageDownloadCompleteListener = onImageDownloadCompleteListener;
        }
        @Override
        protected Drawable doInBackground(String... strings) {
            try {
                InputStream is = (InputStream) new URL("https://dc-api.jun0.dev/images/" + strings[0]).getContent();
                return Drawable.createFromStream(is, "srcName");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            mOnImageDownloadCompleteListener.onDownloadComplete(drawable);
        }
    }
}
