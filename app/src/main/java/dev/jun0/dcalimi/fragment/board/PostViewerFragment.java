package dev.jun0.dcalimi.fragment.board;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.Post;

public class PostViewerFragment extends Fragment {
    public static PostViewerFragment newInstance(){
        PostViewerFragment fragment = new PostViewerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        View view = inflater.inflate(R.layout.fragment_post_viewer, container, false);

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.setTransitionName(transCardName);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        TextView tvUploader = view.findViewById(R.id.tvUploader);
        tvUploader.setText(uploader);

        TextView tvUploadDate = view.findViewById(R.id.tvUploadDate);
        tvUploadDate.setText(uploadDate);

        final ProgressBar progressBar = view.findViewById(R.id.postProgressBar);

        final TextView tvBody = view.findViewById(R.id.tvBody);
        Post.getBody(postId, new Post.OnBodyDownloadCompleteListener(){
            @Override
            public void onDownloadComplete(String result) {
                tvBody.setText(result);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDownloadFailed() {

            }
        });

        return view;
    }
}
