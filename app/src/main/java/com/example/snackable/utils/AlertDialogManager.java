package com.example.snackable.utils;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import androidx.core.text.HtmlCompat;

public class AlertDialogManager {

    public AlertDialogManager() {
        //constructor
    }

    public AlertDialog.Builder removeItemAlertDialogBuilder (Context context, String productName, String list){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = "You want to remove " + "<b>" + productName + "</b>" + " from "+ list+ "?";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        else {
            builder.setMessage(Html.fromHtml(message));
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        return builder;
    }
}
