package com.example.contactlist.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.contactlist.R;

public class ChangePhotoDialog extends DialogFragment {
    private static final String TAG = "ChangePhotoDialog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.dialog_changephoto,container,false);
        Log.d(TAG, "onCreateView: view class set and inflated");

        //init to take pic from camera
        TextView takePhoto = (TextView) view.findViewById(R.id.dialogTakePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: camera should be starting");

            }
        });

        //choose picture from gallery
        TextView selectPhoto = (TextView) view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: should be navigating to browse memory for photos");

            }
        });

        //cancel dialog
        TextView cancel = (TextView) view.findViewById(R.id.dialogCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: exiting dialog");
                getDialog().dismiss();
                Log.d(TAG, "onClick: Dialog box should have exited");
            }
        });

       return view;
    }
}
