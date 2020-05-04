package dev.jun0.dcalimi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
        String transCardName = "";
        String title = "";
        String uploader = "";
        String uploadDate = "";

        if (bundle != null) {
            transCardName = bundle.getString("TRANS_CARD_NAME");
            title = bundle.getString("TITLE");
            uploader = bundle.getString("UPLOADER");
            uploadDate = bundle.getString("UPLOAD_DATE");
        }

        View view = inflater.inflate(R.layout.fragment_post_viewer, container, false);

        ConstraintLayout constraintLayout = view.findViewById(R.id.ConstraintLayout);
        constraintLayout.setTransitionName(transCardName);

        TextView tvTitle = view.findViewById(R.id.title);
        tvTitle.setText(title);

        TextView tvUploader = view.findViewById(R.id.uploader);
        tvUploader.setText(uploader);

        TextView tvUploadDate = view.findViewById(R.id.uploadDate);
        tvUploadDate.setText(uploadDate);

        return view;
    }
}
