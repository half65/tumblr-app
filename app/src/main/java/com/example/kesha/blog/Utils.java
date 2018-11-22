package com.example.kesha.blog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.seratch.signedrequest4j.HttpResponse;
import com.github.seratch.signedrequest4j.OAuthAccessToken;
import com.github.seratch.signedrequest4j.OAuthConsumer;
import com.github.seratch.signedrequest4j.SignedRequest;
import com.github.seratch.signedrequest4j.SignedRequestFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static boolean registrationFlag = false;

    public static void showDialogSave(final Context context, final String accessTokenKey,final String accessSecretTokenKey){

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
                        SharedPreferences preferences;
                        preferences = context.getSharedPreferences(Constants.S_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constants.KEY_ACCESS_TOKEN, accessTokenKey);
                        editor.apply();
                        editor.putString(Constants.KEY_ACCESS_SECRET_TOKEN, accessSecretTokenKey);
                        editor.apply();
                        editor.commit();
                        Intent intent = new Intent(context,MainActivity.class);
                        context.startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void completeRequestURL(String accessTokenKey,String accessSecretTokenKey) {
//Example url: "http://api.tumblr.com/v2/blog/anime-fyi/followers"
        SignedRequest signedRequestResponse;
        OAuthConsumer copyoAuthConsumer = new OAuthConsumer(Constants.consumerKey, Constants.consumerSecret);
        OAuthAccessToken copyAccessToken = new OAuthAccessToken(accessTokenKey, accessSecretTokenKey);
        signedRequestResponse = SignedRequestFactory.create(copyoAuthConsumer, copyAccessToken);

        HttpResponse responseResult = null;
        HttpResponse responseResult2 = null;
        HttpResponse responseResult3 = null;
        HttpResponse responseResult4 = null;
        HttpResponse responseResult5 = null;
        HttpResponse responseResult6 = null;
        HttpResponse responseResult7 = null;

        try {
            responseResult = signedRequestResponse.doGet(Constants.userInfoUrl, "UTF-8");
            responseResult2 = signedRequestResponse.doGet(Constants.userDashboardUrl, "UTF-8");
            responseResult3 = signedRequestResponse.doGet(Constants.userLikesUrl, "UTF-8");
            responseResult4 = signedRequestResponse.doGet(Constants.taggedUrl+"111", "UTF-8");
            responseResult5 = signedRequestResponse.doGet(String.format(Constants.blogInfo,"anime.fyi"), "UTF-8");
            responseResult6 = signedRequestResponse.doGet(String.format(Constants.blogAvatar,"anime.fyi"), "UTF-8");
            responseResult7 = signedRequestResponse.doGet(String.format(Constants.blogPosts,"anime.fyi"), "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.toString());
        }

    }

}
