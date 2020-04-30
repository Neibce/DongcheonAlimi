package me.tyoj.dcalimi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class MyHandler extends Handler {
    static final int HIDE_DIALOG = 0;
    static final int SHOW_DIALOG = 7;
    static final int UPDATE_BUS_INFO = 5;
    static final int ERROR_TO_UPDATE_BUS_INFO = 6;

    private final FragmentManager mFragmentManager;
    private DialogFragment mDialogFragment;
    private View mView;
    private Context mContext;

    MyHandler(FragmentManager fragmentManager){
        mFragmentManager = fragmentManager;
    }

    MyHandler(FragmentManager fragmentManager, View view){
        mFragmentManager = fragmentManager;
        mView = view;
    }

    MyHandler(FragmentManager fragmentManager, Context context){
        mFragmentManager = fragmentManager;
        mContext = context;
    }

    public void handleMessage(Message msg){
        if(msg.what == HIDE_DIALOG){
            hideDialogIfExist();
        }else if(msg.what == SHOW_DIALOG){
            hideDialogIfExist();
            mDialogFragment = new MyDialogFragment(msg.getData().getString("title"), msg.getData().getString("msg"), msg.getData().getBoolean("hasPositive", false));
            mDialogFragment.setCancelable(msg.getData().getBoolean("cancelable", true));
            mDialogFragment.show(mFragmentManager, "HD_TAG");
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

    private void hideDialogIfExist(){
        if(mDialogFragment != null && mDialogFragment.getDialog() != null && mDialogFragment.getDialog().isShowing() && !mDialogFragment.isRemoving())
            mDialogFragment.dismissAllowingStateLoss();
    }
}