package com.example.kesha.blog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.scribejava.core.model.OAuth1RequestToken;

public class Utils {
    public static OAuth1RequestToken requestToken2;

    public static void showDialogSave(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Сохранить данные входа?")
                .setCancelable(false)
                .setNegativeButton("нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

}
