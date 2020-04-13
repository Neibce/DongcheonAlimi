package me.tyoj.dcalimi;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class MyHandler extends Handler {
    static final int HIDE_DIALOG = 0;
    static final int SHOW_DIALOG_DOWNLOADING_SCHOOL_MEAL = 1;
    static final int SHOW_DIALOG_DOWNLOADING_SCHOOL_EVENT = 2;
    static final int SHOW_DIALOG_DOWNLOADED_SUCCESSFULLY = 3;
    static final int SHOW_DIALOG_DOWNLOADING_FAILED = 4;
    static final int UPDATE_BUS_INFO = 5;
    static final int ERROR_TO_UPDATE_BUS_INFO = 6;

    private FragmentManager mFragmentManager;
    private DialogFragment mDialogFragment;
    private View mView;

    MyHandler(FragmentManager fragmentManager){
        mFragmentManager = fragmentManager;
    }

    MyHandler(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mView = view;
    }

    public void handleMessage(Message msg){
        if(msg.what == HIDE_DIALOG){
            if(mDialogFragment != null && mDialogFragment.getDialog() != null && mDialogFragment.getDialog().isShowing() && !mDialogFragment.isRemoving())
                mDialogFragment.dismissAllowingStateLoss();
        }else if(msg.what == SHOW_DIALOG_DOWNLOADING_SCHOOL_MEAL){
            mDialogFragment = new MyDialogFragment("NTC", "급식 정보 다운로드 중...", false);
            mDialogFragment.setCancelable(false);
            mDialogFragment.show(mFragmentManager, "Downloading meal");
        }else if(msg.what == SHOW_DIALOG_DOWNLOADING_SCHOOL_EVENT){
            mDialogFragment = new MyDialogFragment("NTC", "학사일정 정보 다운로드 중...", false);
            mDialogFragment.setCancelable(false);
            mDialogFragment.show(mFragmentManager, "Downloading meal");
        }else if(msg.what == SHOW_DIALOG_DOWNLOADED_SUCCESSFULLY){
            mDialogFragment = new MyDialogFragment("NTC", "다운로드가 성공적으로 완료되었습니다.", true);
            mDialogFragment.setCancelable(true);
            mDialogFragment.show(mFragmentManager, "Downloaded SuccessFully");
        }else if(msg.what == SHOW_DIALOG_DOWNLOADING_FAILED){
            mDialogFragment = new MyDialogFragment("ERR", "다운로드 중 오류가 발생하였습니다.\n잠시 후 다시 시도해주세요.", true);
            mDialogFragment.setCancelable(true);
            mDialogFragment.show(mFragmentManager, "Downloaded SuccessFully");
        }else if(msg.what == UPDATE_BUS_INFO){
            TextView tvBusLeft = mView.findViewById(R.id.tvBusLeft);
            tvBusLeft.setText(msg.obj.toString());
            ImageButton btnBusInfoRefresh = mView.findViewById(R.id.btnBusInfoRefresh);
            btnBusInfoRefresh.clearAnimation();
        }else if(msg.what == ERROR_TO_UPDATE_BUS_INFO){
            TextView tvBusLeft = mView.findViewById(R.id.tvBusLeft);
            tvBusLeft.setText(msg.obj.toString());
            ImageButton btnBusInfoRefresh = mView.findViewById(R.id.btnBusInfoRefresh);
            btnBusInfoRefresh.clearAnimation();
        }
    }
}