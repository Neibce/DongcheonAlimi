package dev.jun0.dcalimi.fragment.board;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import dev.jun0.dcalimi.fragment.main.BoardFragment;
import dev.jun0.dcalimi.adapter.NoticeRecyclerAdapter;
import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.Post;
import dev.jun0.dcalimi.item.PostItem;

public class NoticeFragment extends Fragment {
    private RecyclerView mRecyclerView = null ;
    private NoticeRecyclerAdapter mAdapter = null ;
    private List<PostItem> mPostList = new ArrayList<>();
    private BoardFragment mBoardFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mNoticeProgressBar;
    private LinearLayout mNetworkErrorLinearLayout;

    private boolean mIsLoadedAllPosts = false;
    private boolean mIsOnDownloading = false;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    public NoticeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notice, container, false);

        mBoardFragment = (BoardFragment)getParentFragment();

        mRecyclerView = view.findViewById(R.id.noticeRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Button retryButton = view.findViewById(R.id.retryButton);
        mSwipeRefreshLayout = view.findViewById(R.id.noticeSwipeRefreshLayout);
        mNetworkErrorLinearLayout = view.findViewById(R.id.networkErrorLinearLayout);
        mNoticeProgressBar = view.findViewById(R.id.noticeProgressBar);

        mSwipeRefreshLayout.setVisibility(View.GONE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
        mNoticeProgressBar.setVisibility(View.GONE);

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
                mNoticeProgressBar.setVisibility(View.VISIBLE);

                getPostList(true);
            }
        });

        mAdapter.setOnItemClickListener(new NoticeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i("NF", "onItemClick: " + position);
                mBoardFragment.showFragmentWithTransition(NoticeFragment.this, PostViewerFragment.newInstance(), "postViewer", v, mPostList.get(position), position);
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

    private void getPostList(boolean resetList){
        mIsOnDownloading = true;
        int lastPostId;
        if(!resetList){
            lastPostId = mPostList.get(mPostList.size() - 2).getId();
        }else{
            lastPostId = -1;
            mIsLoadedAllPosts = false;
        }
        Post.getList(Post.NOTICE, lastPostId, new PostDownloadCompleteListener(resetList));
    }

    private void showPostList(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoticeProgressBar.setVisibility(View.GONE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
    }

    private void showProgressBarCenter(){
        mRecyclerView.setVisibility(View.GONE);
        mNoticeProgressBar.setVisibility(View.VISIBLE);
        mNetworkErrorLinearLayout.setVisibility(View.GONE);
    }

    private void showNetworkErrorCenter(){
        mRecyclerView.setVisibility(View.GONE);
        mNoticeProgressBar.setVisibility(View.GONE);
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
