package com.example.kesha.blog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.github.scribejava.core.model.OAuth1RequestToken;

public class Utils {
    public static OAuth1RequestToken requestToken2;

    public static void showDialogSave(final Context context, final String oauthVerifier){

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
                        SharedPreferences preferences = context.getSharedPreferences(MainActivity.S_PREF_NAME, context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(MainActivity.KEY_VERIFIER, oauthVerifier);
                        editor.apply();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

}
