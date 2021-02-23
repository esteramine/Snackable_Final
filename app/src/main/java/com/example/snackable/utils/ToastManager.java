package com.example.snackable.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastManager {
    public ToastManager() {
        //constructor
    }

    public void removedFromListToast(Context context, String list){
        String text = "Removed from "+ list;
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void savedToListToast(Context context, String list){
        String text = list.equals("Saved")? "Saved": "Added to Compare";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
