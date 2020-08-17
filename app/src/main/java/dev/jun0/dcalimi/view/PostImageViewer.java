package dev.jun0.dcalimi.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import dev.jun0.dcalimi.R;

public class PostImageViewer extends RelativeLayout {
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public PostImageViewer(Context context) {
        super(context);
        initializeView(context);
    }

    public PostImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public PostImageViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context);
    }

    private void initializeView(Context context) {
        setGravity(Gravity.CENTER);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.post_image_viewer, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = findViewById(R.id.imageView);
        mProgressBar = findViewById(R.id.progressBar);

        mImageView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mProgressBar.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
    }
}
