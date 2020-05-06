package dev.jun0.dcalimi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

public class MyHandler extends Handler {
    static final int HIDE_DIALOG = 0;
    static final int SHOW_DIALOG = 1;
    static final int SHOW_SNACKBAR = 2;
    static final int CALL_SCHOOL_MEAL_DOWNLOAD_COMPLETE = 3;
    static final int CALL_SCHOOL_EVENT_DOWNLOAD_COMPLETE = 4;
    static final int CALL_SCHOOL_EXAM_DOWNLOAD_COMPLETE = 5;
    static final int UPDATE_BUS_INFO = 6;
    static final int ERROR_TO_UPDATE_BUS_INFO = 7;

    private final FragmentManager mFragmentManager;
    private DialogFragment mDialogFragment;
    private View mView;

    MyHandler(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mView = view;
    }

    public void handleMessage(Message msg) {
        if (msg.what == HIDE_DIALOG) {
            hideDialogIfExist();
        } else if (msg.what == SHOW_DIALOG) {
            hideDialogIfExist();
            mDialogFragment = new MyDialogFragment(msg.getData().getString("title"), msg.getData().getString("msg"), msg.getData().getBoolean("hasPositive", false));
            mDialogFragment.setCancelable(msg.getData().getBoolean("cancelable", true));
            mDialogFragment.show(mFragmentManager, "HD_TAG");
        } else if (msg.what == SHOW_SNACKBAR) {
            Snackbar snackbar = Snackbar.make(mView, msg.obj.toString(), Snackbar.LENGTH_SHORT);
            snackbar.setAnchorView(mView.getRootView().findViewById(R.id.bottom_navigation));
            snackbar.show();
        } else if (msg.what == CALL_SCHOOL_MEAL_DOWNLOAD_COMPLETE) {
            ((SchoolMeal.OnDownloadCompleteListener)msg.obj).onDownloadComplete();
        } else if (msg.what == CALL_SCHOOL_EVENT_DOWNLOAD_COMPLETE) {
            ((SchoolEvent.OnDownloadCompleteListener)msg.obj).onDownloadComplete();
        } else if (msg.what == CALL_SCHOOL_EXAM_DOWNLOAD_COMPLETE) {
            ((SchoolExam.OnDownloadCompleteListener)msg.obj).onDownloadComplete();
        } else if (msg.what == UPDATE_BUS_INFO) {
            TextView tvBusLeft = mView.findViewById(R.id.tvBusLeft);
            tvBusLeft.setText(msg.obj.toString());
            ImageButton btnBusInfoRefresh = mView.findViewById(R.id.btnBusInfoRefresh);
            btnBusInfoRefresh.clearAnimation();
        } else if (msg.what == ERROR_TO_UPDATE_BUS_INFO) {
            TextView tvBusLeft = mView.findViewById(R.id.tvBusLeft);
            if (msg.obj != null)
                tvBusLeft.setText(msg.obj.toString());
            ImageButton btnBusInfoRefresh = mView.findViewById(R.id.btnBusInfoRefresh);
            btnBusInfoRefresh.clearAnimation();
        }
    }

    private void hideDialogIfExist(){
        if(mDialogFragment != null && mDialogFragment.getDialog() != null && mDialogFragment.getDialog().isShowing() && !mDialogFragment.isRemoving())
            mDialogFragment.dismissAllowingStateLoss();
    }
}