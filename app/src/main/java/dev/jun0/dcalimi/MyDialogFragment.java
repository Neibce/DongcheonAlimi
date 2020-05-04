package dev.jun0.dcalimi;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyDialogFragment extends DialogFragment {
    private final String mStrTitle;
    private final String mStrMessage;
    private final Boolean mHasPositiveButton;

    MyDialogFragment(String strTitle, String strMessage, Boolean hasPositiveButton){
        mStrTitle = strTitle;
        mStrMessage = strMessage;
        mHasPositiveButton = hasPositiveButton;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
        materialAlertDialogBuilder.setTitle(mStrTitle);
        materialAlertDialogBuilder.setMessage(mStrMessage);

        if(mHasPositiveButton)
            materialAlertDialogBuilder.setPositiveButton("확인", null);

        return materialAlertDialogBuilder.create();
    }
}
