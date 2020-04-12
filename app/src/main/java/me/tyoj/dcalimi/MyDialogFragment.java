package me.tyoj.dcalimi;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    private String mStrTitle;
    private String mStrMessage;
    private Boolean mHasPositiveButton;

    MyDialogFragment(String strTitle, String strMessage, Boolean hasPositiveButton){
        mStrTitle = strTitle;
        mStrMessage = strMessage;
        mHasPositiveButton = hasPositiveButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(mStrTitle);
        alertDialogBuilder.setMessage(mStrMessage);

        if(mHasPositiveButton)
            alertDialogBuilder.setPositiveButton("확인", null);

        return alertDialogBuilder.create();
    }
}
