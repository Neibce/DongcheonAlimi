package dev.jun0.dcalimi.fragment.board;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.activity.CreatePostActivity;
import dev.jun0.dcalimi.adapter.NoticeRecyclerAdapter;
import dev.jun0.dcalimi.fragment.main.BoardFragment;
import dev.jun0.dcalimi.item.PostItem;
import dev.jun0.dcalimi.util.Post;

import static android.app.Activity.RESULT_OK;

public class SuggestionFragment extends Fragment {
    private static final int CREATE_POST = 0;

    private RecyclerView mRecyclerView = null ;
    private NoticeRecyclerAdapter mAdapter = null ;
    private List<PostItem> mPostList = new ArrayList<>();
    private BoardFragment mBoardFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mSuggestionProgressBar;
    private LinearLayout mNetworkErrorLinearLayout;

    private boolean mIsLoadedAllPosts = false;
    private boolean mIsOnDownloading = false;

    public static SuggestionFragment newInstance() {
        return new SuggestionFragment();
    }

    public SuggestionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_suggestion, container, false);

        mBoardFragment = (BoardFragment)getParentFragment();

        mRecyclerView = view.findViewById(R.id.suggestionRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FloatingActionButton buttonCreatePost = view.findViewById(R.id.buttonCreatePost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivityForResult(intent, CREATE_POST);
            }
        });

        Button retryButton = view.findViewById(R.id.retryButton);
        mSwipeRefreshLayout = view.findViewById(R.id.suggestionSwipeRefreshLayout);
        mNetworkErrorLinearLayout = view.findViewById(R.id.networkErrorLinearLayout);
        mSuggestionProgressBar = view.findViewById(R.id.suggestionProgressBar);

        mSwipeRefreshLayout.setVisibility(View.GONE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
        mSuggestionProgressBar.setVisibility(View.GONE);

        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = view.getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = typedArray.getColor(0, 0);
        mSwipeRefreshLayout.setColorSchemeColors(color);
        typedArray.recycle();

        mAdapter = new NoticeRecyclerAdapter(mPostList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mIsOnDownloading = true;
        showProgressBarCenter();

        retryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideNetworkErrorCenter();
                mSuggestionProgressBar.setVisibility(View.VISIBLE);

                getPostList(true);
            }
        });

        mAdapter.setOnItemClickListener(new NoticeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i("NF", "onItemClick: " + position);
                mBoardFragment.showFragmentWithTransition(SuggestionFragment.this, PostViewerFragment.newInstance(), "postViewer", v, mPostList.get(position), position);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(1) && !mIsOnDownloading && !mIsLoadedAllPosts) {
                    Log.i("NF", "onScrolled: END");

                    getPostList(false);
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostList(true);
            }
        });

        getPostList(true);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CREATE_POST && resultCode == RESULT_OK) {
            getPostList(true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPostList(boolean resetList){
        mIsOnDownloading = true;
        int lastPostId;
        if(!resetList){
            lastPostId = mPostList.get(mPostList.size() - 2).getId();
        }else{
            lastPostId = -1;
            mIsLoadedAllPosts = false;
        }
        Post.getList(Post.SUGGESTION, lastPostId, new PostDownloadCompleteListener(resetList));
    }

    private void showPostList(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mSuggestionProgressBar.setVisibility(View.GONE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
    }

    private void showProgressBarCenter(){
        mRecyclerView.setVisibility(View.GONE);
        mSuggestionProgressBar.setVisibility(View.VISIBLE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
    }

    private void showNetworkErrorCenter(){
        mRecyclerView.setVisibility(View.GONE);
        mSuggestionProgressBar.setVisibility(View.GONE);
        mNetworkErrorLinearLayout.setVisibility(View.VISIBLE);
    }

    private void hideNetworkErrorCenter(){
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
    }

    private class PostDownloadCompleteListener implements Post.OnListDownloadCompleteListener{
        Boolean mResetList;
        private PostDownloadCompleteListener(boolean resetList){
            mResetList = resetList;
        }

        @Override
        public void onDownloadComplete(List<PostItem> result, boolean isLast) {
            int scrollPosition = mPostList.size();
            if(scrollPosition - 1 > 0) {
                mPostList.remove(scrollPosition - 1);
                mAdapter.notifyItemRemoved(scrollPosition);
            }

            if(mResetList) {
                mPostList.clear();
                mSwipeRefreshLayout.setRefreshing(false);
                hideNetworkErrorCenter();
            }
            mPostList.addAll(result);

            if(!isLast) {
                mPostList.add(null);
            }else {
                mIsLoadedAllPosts = true;
            }

            mAdapter.notifyDataSetChanged();
            mRecyclerView.swapAdapter(mAdapter, false);

            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

            if(mResetList)
                showPostList();

            mIsOnDownloading = false;
        }

        @Override
        public void onDownloadFailed() {
            Log.i("NF", "onDownloadFailed: ");
            if(mResetList) {
                mSwipeRefreshLayout.setRefreshing(false);
            }else{
                int scrollPosition = mPostList.size();
                if(scrollPosition - 1 > 0 && mPostList.get(scrollPosition - 1) == null) {
                    mPostList.remove(scrollPosition - 1);
                    mAdapter.notifyItemRemoved(scrollPosition);
                }

                mIsOnDownloading = false;
            }
            showNetworkErrorCenter();
        }
    }
}
