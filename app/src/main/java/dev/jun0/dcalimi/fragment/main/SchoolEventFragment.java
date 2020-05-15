package dev.jun0.dcalimi.fragment.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.SchoolEvent;
import dev.jun0.dcalimi.item.SchoolEventListItem;
import dev.jun0.dcalimi.adapter.SchoolEventRecyclerAdapter;
import dev.jun0.dcalimi.util.MyDate;

public class SchoolEventFragment extends Fragment {
    private SchoolEventRecyclerAdapter mAdapter = new SchoolEventRecyclerAdapter();
    private SchoolEvent mSchoolEvent;
    private SharedPreferences mPreferenceSharedPreferences;
    private MyDate mMyDate;

    private RecyclerView mRecyclerView;
    private TextView mTvEmptyElement;

    public SchoolEventFragment(){}

    public void refreshRecyclerView(){
       if(mSchoolEvent.hasList(mMyDate.getYear(), mMyDate.getMonth())) {
           ArrayList<SchoolEventListItem> schoolEventListItems = mSchoolEvent.getList(mMyDate.getYear(), mMyDate.getMonth());

            if(!schoolEventListItems.isEmpty()) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mTvEmptyElement.setVisibility(View.GONE);

                mAdapter.setList(schoolEventListItems);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }else {
                mTvEmptyElement.setText(R.string.event_non_exist);

                mRecyclerView.setVisibility(View.GONE);
                mTvEmptyElement.setVisibility(View.VISIBLE);
            }
        }else {
            mTvEmptyElement.setText(R.string.event_need_download);

            mRecyclerView.setVisibility(View.GONE);
            mTvEmptyElement.setVisibility(View.VISIBLE);
        }
    }

    public void checkSchoolEventAutoDownload(){
        if(mPreferenceSharedPreferences != null && mSchoolEvent != null) {
            boolean isGetEventAuto = mPreferenceSharedPreferences.getBoolean("schoolEventAutoDownload", false);
            if (isGetEventAuto) {
                boolean hasMealList = mSchoolEvent.hasList(mMyDate.getYear(), mMyDate.getMonth());

                if (!hasMealList) {
                    mSchoolEvent.setOnDownloadCompleteListener(new SchoolEvent.OnDownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            refreshRecyclerView();
                        }
                    });
                    mSchoolEvent.download(mMyDate.getYear(), mMyDate.getMonth());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_event, container, false);

        Context context = view.getContext();

        mRecyclerView = view.findViewById(R.id.CalenderRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mMyDate = new MyDate();

        mPreferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mTvEmptyElement = view.findViewById(R.id.emptyElement);
        mSchoolEvent = new SchoolEvent(getParentFragmentManager(), view);

        refreshRecyclerView();

        return view;
    }
}
