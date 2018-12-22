package io.dume.dume.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import io.dume.dume.R;

public class RadioBtnDialogue extends DialogFragment {
    String title;
    String[] radioOptions;
    DialogInterface.OnClickListener myListener;
    DialogInterface.OnClickListener cancelListener;
    DialogInterface.OnClickListener selectListener;
    private AlertDialog.Builder builder;

    public void setItemChoiceListener(DialogInterface.OnClickListener myListener) {
        this.myListener = myListener;
    }

    public void setCancelListener(DialogInterface.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setSelectListener(DialogInterface.OnClickListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = Objects.requireNonNull(getArguments()).getString("title");
        radioOptions = Objects.requireNonNull(getArguments().getStringArray("radioOptions"));

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if(cancelListener == null && selectListener == null){
            builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.RadioDialogTheme);
            builder.setTitle(title).setSingleChoiceItems(radioOptions, 0, myListener)
                    .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(), "Set", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else if(cancelListener == null){
            builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.RadioDialogTheme);
            builder.setTitle(title).setSingleChoiceItems(radioOptions, 0, myListener)
                    .setPositiveButton("Select", selectListener)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.RadioDialogTheme);
            builder.setTitle(title).setSingleChoiceItems(radioOptions, 0, myListener)
                    .setPositiveButton("Select", selectListener)
                    .setNegativeButton("Cancel",cancelListener);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Typeface font = Typeface.createFromAsset(Objects.requireNonNull(getContext()).getAssets(), "fonts/Cairo_Regular.ttf");
        TextView titleView = alertDialog.findViewById(android.support.v7.appcompat.R.id.alertTitle);
        if(titleView != null){
            Objects.requireNonNull(titleView).setTypeface(font);
        }
        /*Button button1 = (Button) dialog.getWindow().findViewById(android.R.id.button1);
        Button button2 = (Button) dialog.getWindow().findViewById(android.R.id.button2);
        button1.setTypeface(font);
        button2.setTypeface(font);*/
        return alertDialog;
    }
}
