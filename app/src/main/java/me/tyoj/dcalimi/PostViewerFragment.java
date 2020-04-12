package me.tyoj.dcalimi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class PostViewerFragment extends Fragment {
    public static PostViewerFragment newInstance(){
        PostViewerFragment fragment = new PostViewerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String transTitleName = "";
        String transUploaderName = "";
        String transUploadDateName = "";
        String title = "";
        String uploader = "";
        String uploadDate = "";

        if (bundle != null) {
            transTitleName = bundle.getString("TRANS_TITLE_NAME");
            transUploaderName = bundle.getString("TRANS_UPLOADER_NAME");
            transUploadDateName = bundle.getString("TRANS_UPLOAD_DATE_NAME");
            title = bundle.getString("TITLE");
            uploader = bundle.getString("UPLOADER");
            uploadDate = bundle.getString("UPLOAD_DATE");
        }

        View view = inflater.inflate(R.layout.fragment_post_viewer, container, false);

        TextView tvTitle = view.findViewById(R.id.title);
        tvTitle.setTransitionName(transTitleName);
        tvTitle.setText(title);

        TextView tvUploader = view.findViewById(R.id.uploader);
        tvUploader.setTransitionName(transUploaderName);
        tvUploader.setText(uploader);

        TextView tvUploadDate = view.findViewById(R.id.uploadDate);
        tvUploadDate.setTransitionName(transUploadDateName);
        tvUploadDate.setText(uploadDate);

        return view;
    }
}
