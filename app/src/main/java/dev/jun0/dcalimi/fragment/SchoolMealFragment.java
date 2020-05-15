package dev.jun0.dcalimi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.util.SchoolMeal;

public class SchoolMealFragment extends Fragment {
    private String mSelYear;
    private String mSelMonth;
    private String mSelDate;
    private int mPosition;

    public static SchoolMealFragment newInstance(int position, String selYear, String selMonth, String selDate) {
        SchoolMealFragment fragment = new SchoolMealFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("selYear", selYear);
        args.putString("selMonth", selMonth);
        args.putString("selDate", selDate);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt("position", 0);
            mSelYear = getArguments().getString("selYear", null);
            mSelMonth = getArguments().getString("selMonth", null);
            mSelDate = getArguments().getString("selDate", null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_meal, container, false);
        TextView tvLabel = view.findViewById(R.id.textViewMeal);

        SchoolMeal schoolMeal = new SchoolMeal(getParentFragmentManager(), view);
        if(mPosition == 0)
            tvLabel.setText(schoolMeal.get(mSelYear, mSelMonth, mSelDate, false));
        else
            tvLabel.setText(schoolMeal.get(mSelYear, mSelMonth, mSelDate, true));

        return view;
    }
}
