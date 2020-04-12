package me.tyoj.dcalimi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SchoolEventFragment extends Fragment {
    private RecyclerView mRecyclerView = null ;
    private SchoolEventRecyclerAdapter mAdapter = null ;
    private ArrayList<SchoolEventListItem> mSchoolEventListItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_event, container, false);
        mRecyclerView = view.findViewById(R.id.CalenderRecycler) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())) ;

        Calendar calendar = Calendar.getInstance();
        String strNowYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
        String strNowMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(calendar.getTime());

        TextView tvEmptyElement = view.findViewById(R.id.emptyElement);

        SchoolEvent schoolEvent = new SchoolEvent(getFragmentManager(), view.getContext());
        if(schoolEvent.hasList(strNowYear, strNowMonth)) {
            tvEmptyElement.setText(R.string.app_name);
            mSchoolEventListItems = schoolEvent.getList(strNowYear, strNowMonth);

            if(!mSchoolEventListItems.isEmpty()) {
                mRecyclerView.setVisibility(View.VISIBLE);
                tvEmptyElement.setVisibility(View.GONE);

                mAdapter = new SchoolEventRecyclerAdapter(mSchoolEventListItems);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }else {
                tvEmptyElement.setText(R.string.event_non_exist);

                mRecyclerView.setVisibility(View.GONE);
                tvEmptyElement.setVisibility(View.VISIBLE);
            }
        }else {
            tvEmptyElement.setText(R.string.event_need_download);

            mRecyclerView.setVisibility(View.GONE);
            tvEmptyElement.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
