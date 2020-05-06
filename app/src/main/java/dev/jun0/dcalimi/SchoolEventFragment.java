package dev.jun0.dcalimi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SchoolEventFragment extends Fragment {
    private SchoolEventRecyclerAdapter mAdapter = new SchoolEventRecyclerAdapter();
    private SchoolEvent mSchoolEvent;

    private RecyclerView mRecyclerView;
    private TextView mTvEmptyElement;

    private String mStrNowYear;
    private String mStrNowMonth;

    public void refreshRecyclerView(){
       if(mSchoolEvent.hasList(mStrNowYear, mStrNowMonth)) {
           ArrayList<SchoolEventListItem> schoolEventListItems = mSchoolEvent.getList(mStrNowYear, mStrNowMonth);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_event, container, false);

        mRecyclerView = view.findViewById(R.id.CalenderRecycler) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        MyDate myDate = new MyDate();
        mStrNowYear = myDate.getYear();
        mStrNowMonth = myDate.getMonth();


        mTvEmptyElement = view.findViewById(R.id.emptyElement);
        mSchoolEvent = new SchoolEvent(getParentFragmentManager(), view);
        refreshRecyclerView();

        return view;
    }
}
