package dev.jun0.dcalimi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeFragment extends Fragment {
    private RecyclerView mRecyclerView = null ;
    private NoticeRecyclerAdapter mAdapter = null ;
    private ArrayList<NoticeListItem> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        mRecyclerView = view.findViewById(R.id.NoticeRecycler) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())) ;

        mList = new ArrayList<>();

        mAdapter = new NoticeRecyclerAdapter(mList) ;
        mRecyclerView.setAdapter(mAdapter) ;

        addItem("신종코로나바이러스 감염증 관련 공지사항", "동천고등학교", "2020-01-28");
        addItem("KBS1 도전골든벨 130대 골든벨 주인공 탄생 '1학년 류승현 학생'", "동천고등학교", "2020-01-19");
        addItem("KBS 도전골든벨 동천고등학교 방영 안내", "동천고등학교", "2019-12-23");
        addItem("2019학년도 학예제 및 아나바다 장터 안내", "동천고등학교", "2019-12-19");
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new NoticeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i("NF", "onItemClick: " + position);

                ((MainActivity)getContext()).showFragmentWithTransition(NoticeFragment.this, PostViewerFragment.newInstance(), "postViewer", v, position);
            }
        });

        return view;
    }

    private void addItem(String title, String uploader, String date) {
        NoticeListItem item = new NoticeListItem();

        item.setTitle(title);
        item.setUploader(uploader);
        item.setDate(date);

        mList.add(item);
    }
}
