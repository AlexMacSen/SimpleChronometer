package com.alexsen.dialogfragmetns;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.alexsen.chronometer.ChronometerActivity;
import com.alexsen.chronometer.R;

@SuppressLint("ValidFragment")
public class Fragment1 extends DialogFragment {

    public Fragment1(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        setArguments(args);
    }

    @Override
    // create custom Dialog object
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(getString(R.string.menu_ok), new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int whichButton) {
                        ((ChronometerActivity) getActivity()).doPositiveClick();
                    }
                })
                .setNegativeButton(getString(R.string.menu_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int whichButton) {
                        ((ChronometerActivity) getActivity()).doNegativeClick();
                    }
                }).create();
    }
}


